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
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.IOUtils;

public class CrossrefTransformer extends Transformer {
	private static final Logger LOG = LoggerFactory.getLogger(JsonTransformer.class);
	
	protected Configuration templateConfiguration;
	protected OutputStreamWriter strWriter;
	protected String crossRefDepositorEmail;
	protected ICrossRefBusService crossRefBusService;

	/**
	 * 
	 * @param target
	 * @param templateConfiguration
	 * @param crossRefDepositorEmail String email used in crossref submission
	 * @param crossRefBusService
	 */
	public CrossrefTransformer(
		OutputStream target,
		Configuration templateConfiguration,
		String crossRefDepositorEmail,
		ICrossRefBusService crossRefBusService
	) {
		super(target, null);
		this.templateConfiguration = templateConfiguration;
		this.crossRefDepositorEmail = crossRefDepositorEmail;
		this.strWriter = new OutputStreamWriter(target);
		this.crossRefBusService = crossRefBusService;
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
		writeModelToTemplate(model, "crossref/header.xml");
	}

	@Override
	protected void writeData(Map<?, ?> result) throws IOException {
		LOG.warn(this.getClass().getCanonicalName() + ".writeData() has no effect. This class uses POJOs instead of Maps");
	}

	@Override
	public void write(Object result) throws IOException {
		Publication pub = (Publication)result;
		LOG.trace("Writing crossref report entry for publication with indexId = '" + pub.getIndexId() + "'");
		
		Map<String, Object> model = new HashMap<>();
		model.put("pub", pub);
		
		boolean isNumberedSeries = PubsUtilities.isUsgsNumberedSeries(pub.getPublicationSubtype());
		model.put("isNumberedSeries", isNumberedSeries);
		
		String indexPage = crossRefBusService.getIndexPage(pub);
		model.put("indexPage", indexPage);
		
		List<PublicationContributor<?>> contributors = crossRefBusService.getContributors(pub);
		model.put("pubContributors", contributors);
		
		model.put("authorKey", ContributorType.AUTHORS);
		
		writeModelToTemplate(model, "crossref/body.xml");
	}

	/** output the closing tags and close stuff as appropriate. */
	@Override
	public void end() {
		LOG.trace("writing crossref footer");
		Map<String, Object> model = new HashMap<>();
		try{
			writeModelToTemplate(model, "crossref/footer.xml");
		} finally {
			IOUtils.closeQuietly(strWriter);
		}
	}
	
	/**
	 *
	 * @param model
	 * @param templatePath a classpath relative path
	 */
	protected void writeModelToTemplate(Map<?, ?> model, String templatePath){
		Template t;
		try {
			t = this.templateConfiguration.getTemplate(templatePath);
		} catch (IOException ex) {
			throw new RuntimeException("Error loading template", ex);
		}
		try {
			t.process(model, this.strWriter);
		} catch (TemplateException|IOException ex) {
			throw new RuntimeException("Error processing template", ex);
		}
	}
}
