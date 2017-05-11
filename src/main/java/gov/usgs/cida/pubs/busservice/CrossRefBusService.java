package gov.usgs.cida.pubs.busservice;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.transform.TransformerFactory;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;
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
			final XMLValidator xmlValidator
	) {
		this.crossRefProtocol = crossRefProtocol;
		this.crossRefHost = crossRefHost;
		this.crossRefUrl = crossRefUrl;
		this.crossRefPort = crossRefPort;
		this.crossRefUser = crossRefUser;
		this.crossRefPwd = crossRefPwd;
		this.pubsEMailer = pubsEMailer;
		this.crossRefSchemaUrl = crossRefSchemaUrl;
		this.transformerFactory = transformerFactory;
		this.xmlValidator = xmlValidator;
	}

	protected String getCrossRefXml(Publication<?> pub) {
		String xml = null;
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ITransformer transformer = transformerFactory.getTransformer(PubsConstants.MEDIA_TYPE_XML_EXTENSION, baos, null);
			transformer.write(pub);
			transformer.end();

			xml = new String(baos.toByteArray(), PubsConstants.DEFAULT_ENCODING);
			
			xmlValidator.validate(crossRefSchemaUrl, xml);
		} catch (XMLValidationException ex) {
			String msg = "The Crossref XML generated for the publication did not validate against the Crossref schema. " + getIndexIdMessage(pub);
			LOG.error(msg, ex);
			msg += "\n" + ex.getMessage();
			pubsEMailer.sendMail("Invalid Crossref XML Generated for Publication", msg);
		} catch (Exception ex) {
			//broad catch prevents interruption of control flow elsewhere
			String msg = "Error converting pub to Crossref XML before submitting to Crossref webservices. " + getIndexIdMessage(pub);
			LOG.error(msg, ex);
			msg += "\n" + ex.getMessage();
			pubsEMailer.sendMail("Could not generate Crossref XML for Publication", msg);
		}
		return xml;
	}
	
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
	 */
	protected String buildCrossRefUrl(String protocol, String host, int port, String base, String user, String password) {
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
			LOG.error("Could not construct Crossref submission url", ex);
		} catch (UnsupportedEncodingException ex) {
			//no recovery possible
			throw new RuntimeException(ex);
		}
		return url;
	}
	
	@Override
	public void submitCrossRef(final MpPublication mpPublication) {
		String crossRefXml = getCrossRefXml(mpPublication);
		if (null != crossRefXml) {
			LOG.debug("Posting to " + crossRefHost + "://" + crossRefHost + ":" + crossRefPort);
			String url = buildCrossRefUrl(crossRefProtocol, crossRefHost, crossRefPort, crossRefUrl, crossRefUser, crossRefPwd);
			if (null != url) {
				HttpResponse response = null;
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpPost httpPost = buildCrossRefPost(crossRefXml, url);
				HttpHost httpHost = new HttpHost(crossRefHost, crossRefPort, crossRefProtocol);

				try {
					response = httpClient.execute(httpHost, httpPost, new BasicHttpContext());
				} catch (IOException e) {
					String subject = "Unexpected error in POST to crossref";
					LOG.info(subject, e);
					pubsEMailer.sendMail(subject, e.getMessage());
				}

				handleResponse(response);
			}
		}
	}
	
	protected HttpPost buildCrossRefPost(String crossRefXml, String url) {
		HttpPost httpPost = new HttpPost(url);
		
		File crossRefTempFile = writeCrossRefToTempFile(crossRefXml);
		ContentType contentType = ContentType.create(PubsConstants.MEDIA_TYPE_CROSSREF_VALUE, PubsConstants.DEFAULT_ENCODING);
		FileBody file = new FileBody(crossRefTempFile, contentType);
		HttpEntity httpEntity = MultipartEntityBuilder.create()
			.addPart("fname", file)
			.build();
		httpPost.setEntity(httpEntity);
		return httpPost;
	}
	
	protected File writeCrossRefToTempFile(String crossRefXml) {
		File crossRefTempFile = null;
		try {
			crossRefTempFile = File.createTempFile("crossref", "xml");
			FileUtils.writeStringToFile(crossRefTempFile, crossRefXml);
		} catch (IOException ex) {
			LOG.error("Error writing crossref xml to temp file", ex);
		}
		return crossRefTempFile;
	}
	
	protected void handleResponse(HttpResponse response) {
		if (null == response || null == response.getStatusLine()
			|| HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
			String msg = null == response ? "rtn is null" : response.getStatusLine().toString();
			LOG.error("not cool" + msg);
			pubsEMailer.sendMail("Unexpected error in POST to crossref", msg);
		}
	}
}
