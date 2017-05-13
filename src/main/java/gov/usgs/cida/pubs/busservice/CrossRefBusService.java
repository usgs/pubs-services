package gov.usgs.cida.pubs.busservice;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.CrossRefLog;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.transform.CrossrefTransformer;
import gov.usgs.cida.pubs.transform.TransformerFactory;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.validation.xml.XMLValidationException;
import gov.usgs.cida.pubs.validation.xml.XMLValidator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
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

@Service
public class CrossRefBusService implements ICrossRefBusService {

	private static final Logger LOG = LoggerFactory.getLogger(CrossRefBusService.class);

	protected final String crossRefProtocol;
	protected final String crossRefHost;
	protected final String crossRefUrl;
	protected final Integer crossRefPort;
	protected final String crossRefUser;
	protected final String crossRefPwd;
	protected final PubsEMailer pubsEMailer;
	protected final String crossRefSchemaUrl;
	protected final TransformerFactory transformerFactory;
	protected final XMLValidator xmlValidator;
	protected final IDao<CrossRefLog> crossRefLogDao;
	@Autowired
	public CrossRefBusService(
			@Qualifier("crossRefProtocol")
			final String crossRefProtocol,
			@Qualifier("crossRefHost")
			final String crossRefHost,
			@Qualifier("crossRefUrl")
			final String crossRefUrl,
			@Qualifier("crossRefPort")
			final Integer crossRefPort,
			@Qualifier("crossRefUser")
			final String crossRefUser,
			@Qualifier("crossRefPwd")
			final String crossRefPwd,
			@Qualifier("crossRefSchemaUrl")
			final String crossRefSchemaUrl,
			final PubsEMailer pubsEMailer,
			final TransformerFactory transformerFactory,
			final XMLValidator xmlValidator,
			final IDao<CrossRefLog> crossRefLogDao
	) {
		//url-related variables:
		this.crossRefProtocol = crossRefProtocol;
		this.crossRefHost = crossRefHost;
		this.crossRefUrl = crossRefUrl;
		this.crossRefPort = crossRefPort;
		this.crossRefUser = crossRefUser;
		this.crossRefPwd = crossRefPwd;
		//non-url variables:
		this.pubsEMailer = pubsEMailer;
		this.crossRefSchemaUrl = crossRefSchemaUrl;
		this.transformerFactory = transformerFactory;
		this.xmlValidator = xmlValidator;
		this.crossRefLogDao = crossRefLogDao;
	}

	/**
	 * 
	 * @param pub
	 * @return String XML in Crossref Format
	 * @throws XMLValidationException
	 * @throws UnsupportedEncodingException
	 * @throws IOException 
	 */
	protected String getCrossRefXml(Publication<?> pub) throws XMLValidationException, UnsupportedEncodingException, IOException {
		String xml = null;
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			CrossrefTransformer transformer = (CrossrefTransformer) transformerFactory.getTransformer(PubsConstants.MEDIA_TYPE_CROSSREF_EXTENSION, baos, null);
			transformer.write(pub);
			transformer.end();
			
			xml = new String(baos.toByteArray(), PubsConstants.DEFAULT_ENCODING);
			
			//it is important to log the XML, even if it is invalid
			CrossRefLog logEntry = new CrossRefLog(transformer.getBatchId(), pub.getId(), xml);
			crossRefLogDao.add(logEntry);
			
			xmlValidator.validate(crossRefSchemaUrl, xml);
		} catch (XMLValidationException ex) {
			String msg = "The Crossref XML generated for the publication did not validate against the Crossref schema. " + getIndexIdMessage(pub);
			throw new XMLValidationException(msg, ex);
		} catch (UnsupportedEncodingException ex){
			throw ex;
		} catch (IOException ex) {
			String msg = "Error converting pub to Crossref XML before submitting to Crossref webservices. " + getIndexIdMessage(pub);
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
				msg = "Publication Index Id '" + indexId + "'";
			}
		}
		return msg;
	}
	
	/**
	 * Builds a url for registering crossref content.
	 * https://support.crossref.org/hc/en-us/articles/214960123-Using-HTTPS-to-POST-Files
	 * 
	 * @param protocol usually http or https
	 * @param host machine name. Usually test.crossref.org or doi.crossref.org
	 * @param port usually 80 or 443
	 * @param base the base path of the request
	 * @param user the value of the "login_id" parameter
	 * @param password the value of the "login_passwd" parameter
	 * @return String URL with safely-encoded parameters
	 * @throws java.io.UnsupportedEncodingException
	 */
	protected String buildCrossRefUrl(String protocol, String host, int port, String base, String user, String password) throws UnsupportedEncodingException {
		String url = null;
		try {
			/*
			  Java docs strongly recommend that we encode URL components
			  using UTF-8. PubsConstants.DEFAULT_ENCODING may or 
			  may not be UTF-8 over time.
			  https://docs.oracle.com/javase/8/docs/api/java/net/URLEncoder.html#encode-java.lang.String-java.lang.String-
			*/
			
			final String UTF8 = "UTF-8";
			String query = "?operation=doMDUpload&login_id=" +
			URLEncoder.encode(user, UTF8) +
			"&login_passwd=" +
			URLEncoder.encode(password, UTF8) +
			"&area=live";
			URI uri = new URI(protocol, null, host, port, base, null, null);
			url = uri.toString() + query;
		}catch (URISyntaxException ex) {
			throw new RuntimeException("Could not construct Crossref submission url", ex);
		} catch (UnsupportedEncodingException ex) {
			throw ex;
		}
		return url;
	}
	
	@Override
	public void submitCrossRef(final MpPublication mpPublication) {
		try (CloseableHttpClient httpClient = HttpClients.createDefault()){
			String crossRefXml = getCrossRefXml(mpPublication);
			String url = buildCrossRefUrl(crossRefProtocol, crossRefHost, crossRefPort, crossRefUrl, crossRefUser, crossRefPwd);
			HttpPost httpPost = buildCrossRefPost(crossRefXml, url);
			HttpResponse response = performCrossRefPost(httpPost, httpClient);
			handleResponse(response);
		} catch (Exception ex) {
			/**
			 * There's a lot of I/O going on here, and this isn't a
			 * crucial process, so we use an intentionally broad 
			 * catch to prevent interruption of control flow in 
			 * callers
			 */
			String subject = "Error submitting publication to Crossref";
			LOG.info(subject, ex);
			pubsEMailer.sendMail(subject, ex.getMessage());
		}
	}
	
	/**
	 * 
	 * @param httpPost
	 * @param httpClient
	 * @return the response from Crossref web services
	 * @throws IOException 
	 */
	protected HttpResponse performCrossRefPost(HttpPost httpPost, CloseableHttpClient httpClient) throws IOException {
		LOG.debug("Posting to " + crossRefProtocol + "://" + crossRefHost + ":" + crossRefPort);
		try {
			HttpHost httpHost = new HttpHost(crossRefHost, crossRefPort, crossRefProtocol);
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
	 * @return an HttpPost that is ready to send to Crossref web services
	 * @throws IOException 
	 */
	protected HttpPost buildCrossRefPost(String crossRefXml, String url) throws IOException {
		HttpPost httpPost = new HttpPost(url);
		
		File crossRefTempFile = writeCrossRefToTempFile(crossRefXml);
		ContentType contentType = ContentType.create(PubsConstants.MEDIA_TYPE_CROSSREF_VALUE, PubsConstants.DEFAULT_ENCODING);
		FileBody fileBody = new FileBody(crossRefTempFile, contentType);
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
			FileUtils.writeStringToFile(crossRefTempFile, crossRefXML);
			return crossRefTempFile;
		} catch (IOException ex) {
			throw new IOException("Error writing crossref XML to temp file", ex);
		}
	}
	
	/**
	 * Emails alerts if a bad response was received from Crossref web services.
	 * @param response 
	 */
	protected void handleResponse(HttpResponse response) {
		String msg = null;
		if (null == response) {
			msg = "response was null";
		} else if (null == response.getStatusLine()) {
			msg = "response status line was null";
		} else if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
			msg = response.getStatusLine().toString();
		}
		if (null != msg) {
			LOG.error("Error in response from Crossref Submission: " + msg);
			pubsEMailer.sendMail("Error in response from Crossref Submission", msg);
		}
	}
}
