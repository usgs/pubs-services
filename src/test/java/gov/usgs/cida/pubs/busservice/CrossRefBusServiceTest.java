package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CrossRefLog;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.transform.CrossrefTestPubBuilder;
import gov.usgs.cida.pubs.transform.CrossrefTransformerTest;
import gov.usgs.cida.pubs.transform.TransformerFactory;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.validation.xml.XMLValidationException;
import gov.usgs.cida.pubs.validation.xml.XMLValidator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.springframework.http.HttpStatus;

public class CrossRefBusServiceTest extends BaseSpringTest {

	@Autowired
	public String warehouseEndpoint;
	@Autowired
	protected String crossRefProtocol;
	@Autowired
	protected String crossRefHost;
	@Autowired
	protected String crossRefUrl;
	@Autowired
	protected Integer crossRefPort;
	@Autowired
	protected String crossRefUser;
	@Autowired
	protected String crossRefPwd;
	@Autowired
	@Qualifier("crossRefDepositorEmail")
	protected String depositorEmail;
	@Autowired
	protected String crossRefSchemaUrl;
	@Mock
	protected PubsEMailer pubsEMailer;
	@Mock
	IPublicationBusService pubBusService;
	@Autowired
	TransformerFactory transformerFactory;
	@Mock
	XMLValidator xmlValidator;
	@Mock
	IDao<CrossRefLog> crossRefDao;
	
	private CrossRefBusService busService;
	@Before
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		busService = new CrossRefBusService(
			crossRefProtocol,
			crossRefHost,
			crossRefUrl,
			crossRefPort,
			crossRefUser,
			crossRefPwd,
			crossRefSchemaUrl,
			pubsEMailer,
			transformerFactory,
			xmlValidator,
			crossRefDao
		);
	}
	
	@Test
	public void submitCrossRefTest() {
		MpPublication pub = (MpPublication) CrossrefTestPubBuilder.buildNumberedSeriesPub(new MpPublication());
		busService.submitCrossRef(pub);
	}
	
	@Test
	public void verifyEmailSentOnSubmissionError() throws XMLValidationException {
		//create an error during the submission process
		Mockito.doThrow(Exception.class).when(xmlValidator).validate(any(), any());
		MpPublication pub = (MpPublication) CrossrefTestPubBuilder.buildNumberedSeriesPub(new MpPublication());
		busService.submitCrossRef(pub);
		Mockito.verify(pubsEMailer).sendMail(any(), any());
	}
	
	@Test
	public void getIndexIdMessageForNullPub() {
		assertEquals("", busService.getIndexIdMessage(null));
	}
	
	@Test
	public void getIndexIdMessageForPubWithoutIndexId() {
		Publication<?> pub = new Publication<>();
		assertEquals("", busService.getIndexIdMessage(pub));
	}
	
	@Test
	public void getIndexIdForPubWithIndexId() {
		String indexId = "greatPubIndexId07";
		Publication<?> pub = new Publication<>();
		pub.setIndexId(indexId);
		assertTrue(busService.getIndexIdMessage(pub).contains(indexId));
	}
	
	@Test
	public void testBuildCrossrefUrl() throws UnsupportedEncodingException {
		String protocol = "https";
		String host = "test.crossref.org";
		int port = 443;
		String base = "/servlet/";
		//place url-unsafe characters in these fields
		String user = "demonstration_username&?%";
		String password = "demonstration_password&?%";
		
		String actual = busService.buildCrossRefUrl(protocol, host, port, base, user, password);
		
		assertFalse("special user characters should be escaped from url", actual.contains(user));
		assertFalse("special password characters should be escaped from url", actual.contains(password));
		
		String encodedUser = URLEncoder.encode(user, "UTF-8");
		String encodedPassword = URLEncoder.encode(password, "UTF-8");
		
		assertTrue("special user characters should be escaped from url", actual.contains(encodedUser));
		assertTrue("special password characters should be escaped from url", actual.contains(encodedPassword));
	}
	
	@Test
	public void performMockCrossRefPost() throws IOException{
		//whitebox testing of posting to Crossref web services
		HttpPost httpPost = mock(HttpPost.class);
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		int expectedStatus = 200;
		CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(expectedStatus);
		when(mockResponse.getStatusLine()).thenReturn(statusLine);
		when(httpClient.execute(any(), eq(httpPost), any(HttpContext.class))).thenReturn(mockResponse);
		HttpResponse response = busService.performCrossRefPost(httpPost, httpClient);
		verify(httpClient).execute(any(), eq(httpPost), any(HttpContext.class));
		assertEquals(expectedStatus, response.getStatusLine().getStatusCode());
	}
	
	@Test(expected = IOException.class)
	public void performFailingCrossRefPost() throws IOException{
		//whitebox testing to ensure that implementation does not
		//swallow expected Exception
		HttpPost httpPost = mock(HttpPost.class);
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		when(httpClient.execute(any(), eq(httpPost), any(HttpContext.class))).thenThrow(IOException.class);
		busService.performCrossRefPost(httpPost, httpClient);
	}
	
	@Test
	public void tetBuildCrossRefPost() throws IOException {
		String expectedBody = "expectedBody";
		String expectedUrl = "http://pubs.er.usgs.gov/";
		HttpPost post = busService.buildCrossRefPost(expectedBody, expectedUrl);
		HttpEntity entity = post.getEntity();
		assertNotNull(entity);
		EntityUtils.consume(entity);
		
		/**
		 * Apache does not provide an easy way to extract a 
		 * MultiPartEntity from an HttpPost's Entity, so instead we 
		 * extract a String and make some loose assertions on the String
		 */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream content = entity.getContent();
		assertNotNull(content);
		IOUtils.copy(content, baos);
		String requestBody = baos.toString(PubsConstants.DEFAULT_ENCODING);
		assertNotNull(requestBody);
		assertTrue(0 < requestBody.length());
		assertTrue(requestBody.contains(PubsConstants.DEFAULT_ENCODING));
		assertTrue(requestBody.contains(PubsConstants.MEDIA_TYPE_CROSSREF_VALUE));
		assertTrue(requestBody.contains(expectedBody));
	}
	
	@Test
	public void getGoodCrossrefXml() throws XMLValidationException, IOException {
		Publication<?> pub = CrossrefTestPubBuilder.buildUnNumberedSeriesPub(new Publication<>());
		String xml = busService.getCrossRefXml(pub);
		
		//verify that the attempt was logged
		verify(crossRefDao).add(any());
		
		assertNotNull(xml);
		assertTrue("should get some XML", 0 < xml.length());

	}	
	 
	@Test
	public void verifyInvalidXmlAttemptIsLogged() throws XMLValidationException, IOException {
		Publication<?> pub = CrossrefTestPubBuilder.buildNumberedSeriesPub(new Publication<>());
		Mockito.doThrow(new XMLValidationException())
			.when(xmlValidator).validate(anyString(), anyString());
		try{
			busService.getCrossRefXml(pub);
			Assert.fail("XMLValidationException should have been raised");
		} catch (XMLValidationException ex) {
			//verify that the attempt was logged even if the xml
			//was invalid
			verify(crossRefDao).add(any());
		}
	}
	
	@Test
	public void testHandleGoodResponse() {
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.OK.value());
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(statusLine);
		busService.handleResponse(response);
		verify(pubsEMailer, never()).sendMail(any(), any());
	}
	
	@Test
	public void testHandleNullResponse() {
		busService.handleResponse(null);
		verify(pubsEMailer).sendMail(anyString(), anyString());
	}
	
	@Test
	public void testEmptyStatusLineResponse() {
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(null);
		busService.handleResponse(response);
		verify(pubsEMailer).sendMail(anyString(), anyString());
	}
	
	@Test
	public void testHandleNotFoundResponse() {
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND.value());
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(statusLine);
		busService.handleResponse(response);
		verify(pubsEMailer).sendMail(anyString(), anyString());
	}
}
