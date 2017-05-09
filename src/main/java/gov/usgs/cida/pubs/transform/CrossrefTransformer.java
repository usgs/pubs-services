package gov.usgs.cida.pubs.transform;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class CrossrefTransformer extends Transformer {
	private static final Logger LOG = LoggerFactory.getLogger(CrossrefTransformer.class);
	
	protected Configuration templateConfiguration;
	protected OutputStreamWriter streamWriter;
	protected BufferedWriter bufferedWriter;
	protected String crossRefDepositorEmail;
	protected IPublicationBusService pubBusService;
	protected final String AUTHOR_KEY;
	protected final String EDITOR_KEY;
	/**
	 * 
	 * @param target
	 * @param templateConfiguration
	 * @param crossRefDepositorEmail String email used in crossref submission
	 * @param pubBusService
	 */
	public CrossrefTransformer(
		OutputStream target,
		Configuration templateConfiguration,
		String crossRefDepositorEmail,
		IPublicationBusService pubBusService
	) {
		super(target, null);
		this.templateConfiguration = templateConfiguration;
		this.crossRefDepositorEmail = crossRefDepositorEmail;
		try {
			this.streamWriter = new OutputStreamWriter(target, PubsConstants.DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException ex) {
			//we can't do anything about this
			throw new RuntimeException(ex);
		}
		this.bufferedWriter = new BufferedWriter(streamWriter);
		this.pubBusService = pubBusService;
		//these values from the database are unlikely to change
		//during the lifetime of the object
		AUTHOR_KEY = PubsUtilities.getAuthorKey();
		EDITOR_KEY = PubsUtilities.getEditorKey();
		init();
	}
	protected String getTimeStamp(){
		String timestamp = String.valueOf(new Date().getTime());
		return timestamp;
	}
	protected String getBatchId(){
		return UUID.randomUUID().toString();
	}
	@Override
	protected void init() {
		
		String timestamp = getTimeStamp();
		String batchId = getBatchId();
		Map<String, String> model = ImmutableMap.of(
			"doi_batch_id", batchId,
			"submission_timestamp", timestamp,
			"depositor_email", crossRefDepositorEmail
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
		writeResult(result);
	}
	
	/**
	 * 
	 * @param result
	 * @return true if result was written succesfully, false otherwise.
	 * @throws IOException 
	 */
	protected boolean writeResult(Object result) throws IOException {
		boolean success = false;
		try {
			Publication<?> pub = (Publication)result;
			LOG.trace("Writing crossref report entry for publication with indexId = '" + pub.getIndexId() + "'");

			List<PublicationContributor<?>> contributors = this.getContributors(pub);
			if (contributors.isEmpty()) {
				String message = getExcludedErrorMessage(result);
				LOG.error(message + ". Publication had no contributors.");
				writeComment(message);
			} else {
				Map<String, Object> model = new HashMap<>();
				model.put("pub", pub);
				boolean isNumberedSeries = PubsUtilities.isUsgsNumberedSeries(pub.getPublicationSubtype());
				model.put("isNumberedSeries", isNumberedSeries);

				String indexPage = pubBusService.getIndexPage(pub);
				model.put("indexPage", indexPage);

				model.put("pubContributors", contributors);

				model.put("authorKey", ContributorType.AUTHORS);
				model.put("editorKey", ContributorType.EDITORS);
				model.put("compilerKey", ContributorType.COMPILERS);
				writeModelToTemplate(model, "crossref/body.ftlx");
				success = true;
			}
		} catch (TemplateException | IOException e) {
			/**
			 * Since publications are of varying quality, we omit 
			 * erroneous publications and continue on to the next
			 * publication.
			 */
			String message = getExcludedErrorMessage(result);
			LOG.error("Error transforming object into Crossref XML. "
				+ message, e);

			//add error message as a comment to the xml document
			writeComment(message);
		}
		return success;
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
	
	/**
	 * 
	 * @param result
	 * @return a generic message if 'result' is not a pub, else return a
	 * message that helps identify a problematic pub.
	 */
	protected String getExcludedErrorMessage(Object result) {
		String message = "Excluded Problematic Publication";
		if (result instanceof Publication) {
			message += " with Index Id: " 
				+ ((Publication) result).getIndexId();
		}
		return message;
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
			IOUtils.closeQuietly(bufferedWriter);
			IOUtils.closeQuietly(streamWriter);
		}
	}
	
	/**
	 *
	 * @param model
	 * @param templatePath a classpath relative path
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
			IOUtils.copy(bais, bufferedWriter);
		} finally {
			IOUtils.closeQuietly(bais);
		}
	}
	
	protected List<PublicationContributor<?>> getContributors(Publication<?> pub) {
		List<PublicationContributor<?>> rtn = new ArrayList<>();
		//This process requires that the contributors are in rank order.
		//And that the contributor is valid.
		if (null != pub && null != pub.getContributors() && !pub.getContributors().isEmpty()) {
			Map<String, List<PublicationContributor<?>>> contributors = pub.getContributorsToMap();
			List<PublicationContributor<?>> authors = contributors.get(AUTHOR_KEY);
			if (null != authors && !authors.isEmpty()) {
				rtn.addAll(authors);
			}
			List<PublicationContributor<?>> editors = contributors.get(EDITOR_KEY);
			if (null != editors && !editors.isEmpty()) {
				rtn.addAll(editors);
			}
		}
		return rtn;
	}
}
