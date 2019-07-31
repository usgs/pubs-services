package gov.usgs.cida.pubs.busservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.dao.intfc.ICrossRefLogDao;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CrossRefLog;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.transform.CrossrefTransformer;
import gov.usgs.cida.pubs.utility.PubsEMailer;

@Service
public class CrossRefBusService implements ICrossRefBusService {

	private static final Logger LOG = LoggerFactory.getLogger(CrossRefBusService.class);

	private final ConfigurationService configurationService;
	private final PubsEMailer pubsEMailer;
	private final ICrossRefLogDao crossRefLogDao;
	private final Configuration templateConfiguration;
	private final IPublicationBusService pubBusService;
	@Autowired
	public CrossRefBusService(
			final ConfigurationService configurationService,
			final PubsEMailer pubsEMailer,
			final ICrossRefLogDao crossRefLogDao,
			@Qualifier("freeMarkerConfiguration")
			Configuration templateConfiguration,
			IPublicationBusService publicationBusService
	) {
		this.configurationService = configurationService;
		this.pubsEMailer = pubsEMailer;
		this.crossRefLogDao = crossRefLogDao;
		this.templateConfiguration = templateConfiguration;
		this.pubBusService = publicationBusService;
	}

	/**
	 * 
	 * @param pub
	 * @return String XML in Crossref Format
	 * @throws UnsupportedEncodingException
	 * @throws IOException 
	 */
	protected String getCrossRefXml(Publication<?> pub) throws UnsupportedEncodingException, IOException {
		String xml = null;
		try (	ByteArrayOutputStream baos = new ByteArrayOutputStream();
				CrossrefTransformer transformer = new CrossrefTransformer(
					baos,
					templateConfiguration,
					configurationService,
					pubBusService,
					ContributorType.AUTHOR_KEY,
					ContributorType.EDITOR_KEY
				);
			){

			transformer.write(pub);
			transformer.end();

			xml = new String(baos.toByteArray(), PubsConstantsHelper.DEFAULT_ENCODING);

			//it is important to log the XML, even if it is invalid
			CrossRefLog logEntry = new CrossRefLog(transformer.getBatchId(), pub.getId(), xml);
			crossRefLogDao.add(logEntry);

		} catch (UnsupportedEncodingException ex){
			throw ex;
		} catch (IOException ex) {
			String msg = "Error converting pub to Crossref XML before submitting to Crossref webservices.";
			throw new IOException(msg, ex);
		}
		return xml;
	}

	/**
	 * Null-safe method to create a message for identifying a publication
	 * @param pub
	 * @return a message
	 */
	protected String getIndexIdMessage(Publication<?> pub){
		String msg = "";
		if (null != pub) {
			String indexId = pub.getIndexId();
			if (null != indexId && 0 != indexId.length()) {
				msg = "Publication Index Id: '" + indexId + "'";
			}
		}
		return msg;
	}

	/**
	 * Builds a url for registering crossref content.
	 * https://support.crossref.org/hc/en-us/articles/214960123-Using-HTTPS-to-POST-Files
	 * 
	 * @return String URL with safely-encoded parameters
	 * @throws java.io.UnsupportedEncodingException
	 * @throws java.net.URISyntaxException
	 */
	protected String buildCrossRefUrl() throws UnsupportedEncodingException, URISyntaxException {
		String url = null;
		try {
			String query = "?operation=doMDUpload&login_id=" +
			URLEncoder.encode(configurationService.getCrossrefUser(), PubsConstantsHelper.URL_ENCODING) +
			"&login_passwd=" +
			URLEncoder.encode(configurationService.getCrossrefPwd(), PubsConstantsHelper.URL_ENCODING) +
			"&area=live";
			URI uri = new URI(configurationService.getCrossrefProtocol(), null, configurationService.getCrossrefHost(),
					configurationService.getCrossrefPort(), configurationService.getCrossrefUrl(), null, null);
			url = uri.toString() + query;
		} catch (URISyntaxException ex) {
			/**
			 * we omit the original exception because the URI could
			 * contain passwords that we do not want logged or 
			 * emailed.
			 */
			throw new URISyntaxException(String.join(",", configurationService.getCrossrefProtocol(), configurationService.getCrossrefHost(),
					configurationService.getCrossrefPort().toString(), configurationService.getCrossrefUrl(), configurationService.getCrossrefUser(),
					"password omitted"), "Could not construct Crossref submission url");
		} catch (UnsupportedEncodingException ex) {
			throw ex;
		}
		return url;
	}

	@Override
	public void submitCrossRef(final MpPublication mpPublication) {
		String publicationIndexIdMessage = ""; 
		try (CloseableHttpClient httpClient = HttpClients.createDefault()){
			publicationIndexIdMessage = getIndexIdMessage(mpPublication);
			submitCrossRef(mpPublication, httpClient);
			LOG.info("Publication successfully published. " + publicationIndexIdMessage);
		} catch (Exception ex) {
			/**
			 * There's a lot of I/O going on here, and this isn't a
			 * crucial process, so we use an intentionally broad 
			 * catch to prevent interruption of control flow in 
			 * callers
			 */
			String errorId = UUID.randomUUID().toString();
			String subject = "Error submitting publication to Crossref";
			String logMessage = subject + ". Error ID#:" + errorId + ". "+ publicationIndexIdMessage;
			LOG.error(logMessage, ex);
			String emailMessage = subject + ".\n" + 
				"Error Message: " + ex.getMessage() + "\n" +
				publicationIndexIdMessage + "\n" +
				"More information is available in the server logs.\n" +
				"Host: " + configurationService.getDisplayHost() + ".\n" +
				"Error ID#: " + errorId + ".\n";

			pubsEMailer.sendMail(subject, emailMessage);
		}
	}

	/**
	 * Builds Crossref XML for a publication and then submits it to Crossref web services.
	 * @param mpPublication Crossref XML is generated from this publication
	 * @param httpClient used to submit the publication's Crossref XML to Crossref web services
	 * @throws UnsupportedEncodingException
	 * @throws IOException 
	 * @throws org.apache.http.HttpException 
	 * @throws java.net.URISyntaxException 
	 */
	protected void submitCrossRef(final MpPublication mpPublication, CloseableHttpClient httpClient) throws UnsupportedEncodingException, IOException, HttpException, URISyntaxException {
		String crossRefXml = getCrossRefXml(mpPublication);
		String url = buildCrossRefUrl();
		HttpPost httpPost = buildCrossRefPost(crossRefXml, url, mpPublication.getIndexId());
		HttpResponse response = performCrossRefPost(httpPost, httpClient);
		handleResponse(response);
	}

	/**
	 * 
	 * @param httpPost
	 * @param httpClient
	 * @return the response from Crossref web services
	 * @throws IOException 
	 */
	protected HttpResponse performCrossRefPost(HttpPost httpPost, CloseableHttpClient httpClient) throws IOException {
		LOG.debug("Posting to " + configurationService.getCrossrefProtocol() + "://" + configurationService.getCrossrefHost() + ":" + configurationService.getCrossrefPort());
		try {
			HttpHost httpHost = new HttpHost(configurationService.getCrossrefHost(), configurationService.getCrossrefPort(), configurationService.getCrossrefProtocol());
			HttpResponse response = httpClient.execute(httpHost, httpPost, new BasicHttpContext());
			return response;
		} catch (IOException ex) {
			throw new IOException("Unexpected network error when POSTing to Crossref", ex);
		}
	}

	/**
	 * 
	 * @param crossRefXml
	 * @param url
	 * @param indexId the index ID of the publication
	 * @return an HttpPost that is ready to send to Crossref web services
	 * @throws IOException 
	 */
	protected HttpPost buildCrossRefPost(String crossRefXml, String url, String indexId) throws IOException {
		HttpPost httpPost = new HttpPost(url);

		File crossRefTempFile = writeCrossRefToTempFile(crossRefXml);
		ContentType contentType = ContentType.create(PubsConstantsHelper.MEDIA_TYPE_CROSSREF_VALUE, PubsConstantsHelper.DEFAULT_ENCODING);

		//The filename is displayed in Crossref's dashboard, so put some
		//useful info in it
		String filename = indexId + "." + PubsConstantsHelper.MEDIA_TYPE_CROSSREF_EXTENSION;

		FileBody fileBody = new FileBody(crossRefTempFile, contentType, filename);
		HttpEntity httpEntity = MultipartEntityBuilder.create()
			.addPart("fname", fileBody)
			.build();
		httpPost.setEntity(httpEntity);
		return httpPost;
	}

	/**
	 * 
	 * @param crossRefXML
	 * @return temp file containing the specified XML
	 * @throws IOException 
	 */
	protected File writeCrossRefToTempFile(String crossRefXML) throws IOException {
		try {
			File crossRefTempFile = File.createTempFile("crossref", "xml");
			FileUtils.writeStringToFile(crossRefTempFile, crossRefXML, PubsConstantsHelper.DEFAULT_ENCODING);
			return crossRefTempFile;
		} catch (IOException ex) {
			throw new IOException("Error writing crossref XML to temp file", ex);
		}
	}

	/**
	 * Check the response from Crossref web services, throw an 
	 * HttpException with a descriptive message if anything is wrong.
	 * @param response 
	 * @throws HttpException when the Crossref web services return an error
	 */
	protected void handleResponse(HttpResponse response) throws HttpException {
		String exceptionMessage = null;
		if (null == response) {
			exceptionMessage = "Response was null.";
		} else if (null == response.getStatusLine()) {
			exceptionMessage = "Response status line was null.";
		} else if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
			exceptionMessage = response.getStatusLine().toString();
		}
		if(null != exceptionMessage) {
			throw new HttpException("Error in response from Crossref Submission: " + exceptionMessage);
		}
	}
}
