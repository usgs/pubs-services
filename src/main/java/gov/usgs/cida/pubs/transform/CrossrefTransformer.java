package gov.usgs.cida.pubs.transform;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.utility.DataNormalizationUtils;
import gov.usgs.cida.pubs.utility.PubsUtils;

/**
 * Transforms Publications into Crossref XML. One Transformer should be 
 * instantiated for each batch.
 */
public class CrossrefTransformer extends Transformer {
	private static final Logger LOG = LoggerFactory.getLogger(CrossrefTransformer.class);

	protected Configuration templateConfiguration;
	protected OutputStreamWriter streamWriter;
	protected BufferedWriter bufferedWriter;
	protected ConfigurationService configurationService;
	protected final String batchId;
	protected final String timestamp;

	public static final String[] CONTRIBUTOR_KEYS = {ContributorType.AUTHOR_KEY, ContributorType.EDITOR_KEY, ContributorType.COMPILER_KEY};

	/**
	 * Constructs and initializes a transformer with a particular batch id
	 * and timestamp every time.
	 * @param target
	 * @param templateConfiguration
	 * @param crossRefDepositorEmail String email used in Crossref submission
	 */
	public CrossrefTransformer(
		OutputStream target,
		Configuration templateConfiguration,
		ConfigurationService configurationService
	) {
		super(target, null);
		this.templateConfiguration = templateConfiguration;
		this.configurationService = configurationService;
		try {
			this.streamWriter = new OutputStreamWriter(target, PubsConstantsHelper.DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException ex) {
			//we can't do anything about this
			throw new RuntimeException(ex);
		}
		this.bufferedWriter = new BufferedWriter(streamWriter);
		this.batchId = UUID.randomUUID().toString();
		this.timestamp = String.valueOf(new Date().getTime());
		init();
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getBatchId() {
		return batchId;
	}

	@Override
	protected void init() {
		Map<String, String> model = Map.of(
			"doi_batch_id", getBatchId(),
			"submission_timestamp", getTimestamp(),
			"depositor_email", configurationService.getCrossrefDepositorEmail()
		);
		writeHeader(model);
	}

	@Override
	protected void writeHeader(Map<?,?> model) {
		LOG.trace("writing crossref header");
		try{
			writeModelToTemplate(model, "crossref/header.ftlx");
		} catch (IOException | TemplateException e) {
			throw new RuntimeException("Error writing header", e);
		}
	}

	@Override
	protected void writeData(Map<?, ?> result) throws IOException {
		LOG.warn(this.getClass().getCanonicalName() + ".writeData() has no effect. This class uses POJOs instead of Maps");
	}

	@Override
	public void write(Object result) throws IOException {
		//We do not catch any ClassCastExceptions because we want loud, 
		//early failure.
		writeResult((Publication<?>)result);
	}

	/**
	 * 
	 * @param pub
	 * @throws IOException 
	 */
	protected void writeResult(Publication<?> pub) throws IOException {
		Map<String, Object> model = makeModel(pub);
		boolean createMinimal = false;

		// first try to write the full Crossref document
		try {
			LOG.trace("Writing crossref report entry for publication with indexId = '" + pub.getIndexId() + "'");
			writeModelToTemplate(model, "crossref/body.ftlx");
		} catch (TemplateException | IOException e) {
			String logMessage = String.format("Error transforming publication (indexId: %s doi: %s) into Crossref XML, retrying with minimal crossref",
								pub.getIndexId(), pub.getDoi());
			LOG.error(logMessage, e);
			createMinimal = true;
		}

		// if full document failed, try creating a minimal Crossref
		if(createMinimal) {
			String logMessage = "";
			try {
				writeModelToTemplate(model, "crossref/body_minimal.ftlx");
				logMessage = String.format("Created minimal Crossref XML (indexId: %s doi: %s)", pub.getIndexId(), pub.getDoi());
				LOG.info(logMessage);
			} catch (TemplateException | IOException e) {
				logMessage = String.format("Error transforming publication (indexId: %s doi: %s) into minimal Crossref XML.",
											pub.getIndexId(), pub.getDoi());
				LOG.error(logMessage, e);
				writeComment(String.format("Excluded Problematic Publication (indexId: %s doi: %s)", pub.getIndexId(), pub.getDoi()));
			}
		}
	}

	protected Map<String, Object> makeModel(Publication<?> pub) {
		Map<String, Object> model = new HashMap<>();
		model.put("pub", pub);
		boolean isNumberedSeries = PubsUtils.isUsgsNumberedSeries(pub.getPublicationSubtype());
		model.put("isNumberedSeries", isNumberedSeries);

		model.put("warehousePage", getWarehousePage(pub));

		model.put("pubContributors", getCrossrefContributors(pub));
		model.put("ORCID_PREFIX", DataNormalizationUtils.ORCID_PREFIX);

		model.put("authorKey", ContributorType.AUTHORS);
		model.put("editorKey", ContributorType.EDITORS);
		model.put("compilerKey", ContributorType.COMPILERS);

		model.putAll(makeDataReleaseModel(pub));
		return model;
	}

	public String getWarehousePage(Publication<?> pub) {
		String rtn = "";
		if(pub != null && pub.getIndexId() != null) {
			rtn = configurationService.getWarehouseEndpoint() + "/publication/" + pub.getIndexId();
		}
		return rtn;
	}

	protected Map<String, Object> makeDataReleaseModel(Publication<?> pub) {
		List<PublicationLink<?>> dataReleaseLinks = pub.getLinksByLinkTypeId(LinkType.DATA_RELEASE);
		Map<String, Object> model = new HashMap<>();

		if (dataReleaseLinks.isEmpty()) {
			model.put("hasDataRelease", false);
		} else {
			PublicationLink<?> link = dataReleaseLinks.get(0);
			String url = link.getUrl();
			String doi = url == null ? "" : url.replaceAll("^https?://doi.org/", "");
			if (doi.isEmpty() || doi.equals(url)) { // drop dataset linkage block if doi not found
				LOG.error(String.format(
						"reference doi not found in publication's (indexId: %s doi: %s) Data Release link.",
						pub.getIndexId(), pub.getDoi()));
				LOG.error("Data Release dropped from Crossref XML");
				model.put("hasDataRelease", false);
			} else {
				model.put("hasDataRelease", true);
				model.put("dataReleaseDescription", link.getDescription() + link.getHelpText());
				model.put("dataReleaseDoi", doi);
			}
		}
		return model;
	}

	protected String wrapInComment(String message) {
		return "<!-- " + StringEscapeUtils.escapeXml11(message) + " -->\n";
	}

	/**
	 * Writes the given string as an XML comment
	 * @param message
	 * @throws IOException 
	 */
	protected void writeComment(String message) throws IOException {
		//add error message as a comment to the xml document
		bufferedWriter.append(wrapInComment(message));
	}

	/** output the closing tags and close stuff as appropriate. */
	@Override
	public void end() {
		LOG.trace("writing crossref footer");
		Map<String, Object> model = new HashMap<>();
		try{
			writeModelToTemplate(model, "crossref/footer.ftlx");
			bufferedWriter.flush();
		} catch (IOException | TemplateException e ) {
			throw new RuntimeException("Error writing footer", e);
		} finally {
			closeQuietly(bufferedWriter);
			closeQuietly(streamWriter);
		}
	}

	/**
	 *
	 * @param model
	 * @param templatePath a classpath relative path
	 * @throws java.io.IOException
	 * @throws freemarker.template.TemplateException
	 */
	protected void writeModelToTemplate(Map<?, ?> model, String templatePath) throws IOException, TemplateException{
		Template t;
		try {
			t = this.templateConfiguration.getTemplate(templatePath);
		} catch (IOException ex) {
			throw new RuntimeException("Error loading template", ex);
		}
		/**
		 * We only want to include reports that are successfully
		 * transformed into Crossref in the output. Thankfully, each
		 * report is small, so we can write each to its own in-memory 
		 * buffer. If the transformation is successful, then the result
		 * is written to the main output. If not, the caller is 
		 * responsible for handling the error.
		*/
		ByteArrayInputStream bais = null;
		try(
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter reportWriter = new OutputStreamWriter(baos)
		) {
			t.process(model, reportWriter);
			bais = new ByteArrayInputStream(baos.toByteArray());
			IOUtils.copy(bais, bufferedWriter, PubsConstantsHelper.DEFAULT_ENCODING);
		} finally {
			closeQuietly(bais);
		}
	}

	// null is returned so that an empty contributors section is not added to the Crossref xml
	protected List<PublicationContributor<?>> getCrossrefContributors(Publication<?> pub) {
		List<PublicationContributor<?>> rtn = new ArrayList<>();
		// This process requires that the contributors are in rank order.
		// And that the contributor is valid.
		if (null != pub && null != pub.getContributors() && !pub.getContributors().isEmpty()) {
			Map<String, List<PublicationContributor<?>>> contributorMap = pub.getContributorsToMap();
			for (String key : CONTRIBUTOR_KEYS) {
				List<PublicationContributor<?>> contributors = contributorMap.get(key);
				if (null != contributors && !contributors.isEmpty()) {
					rtn.addAll(contributors);
				}
			}
		}
		return rtn.isEmpty() ? null : rtn;
	}

	protected void closeQuietly(final Closeable closeable) {
		// per IOUTILS - deprecated with no replacement -  Please use the try-with-resources statement or handle suppressed exceptions manually.
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (final IOException ioe) {
			// ignore
		}
	}
}
