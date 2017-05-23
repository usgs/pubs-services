package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.intfc.ICrossRefLogDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.transform.CrossrefTestPubBuilder;
import gov.usgs.cida.pubs.transform.TransformerFactory;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.validation.xml.XMLValidationException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import org.springframework.test.annotation.DirtiesContext;

/**
 * In this class we're overriding CrossRefBusService's ICrossRefLogDao member with
 * a mock. We don't want subsequent tests to have the opportunity to access the
 * mock, so we tell Spring to invalidate the Context cache via @DirtiesContext.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CrossRefBusServiceTest extends BaseSpringTest {

	@Autowired
	protected String warehouseEndpoint;
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
	@Autowired
	protected TransformerFactory transformerFactory;
	@Mock
	protected ICrossRefLogDao crossRefLogDao;
	
	protected CrossRefBusService busService;
	
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
			crossRefLogDao
		);
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
	public void getIndexIdMessageForPubWithEmptyIndexId() {
		Publication<?> pub = new Publication<>();
		pub.setIndexId("");
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
	public void testBuildCrossrefUrl() throws UnsupportedEncodingException, URISyntaxException {
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
	public void verifyCrossrefUrlBuilderDoesNotSwallowURIProblems() throws UnsupportedEncodingException, URISyntaxException {
		//do not specify characters that need %-encoding
		String password = "MockPassword";
		try{
			busService.buildCrossRefUrl("", "", -2, "", "", password);
			Assert.fail("Should have raised Exception");
		} catch (URISyntaxException ex) {
			assertFalse(ex.getMessage().contains(password));
			if(null != ex.getCause()){
				assertFalse(ex.getCause().getMessage().contains(password));
			}
		}
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
		String expectedIndexId = "sir123456789";
		HttpPost post = busService.buildCrossRefPost(expectedBody, expectedUrl, expectedIndexId);
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
		assertTrue(requestBody.contains(expectedIndexId));
	}
	
	@Test
	public void getGoodCrossrefXml() throws XMLValidationException, IOException {
		Publication<?> pub = CrossrefTestPubBuilder.buildUnNumberedSeriesPub(new Publication<>());
		String xml = busService.getCrossRefXml(pub);
		
		//verify that the attempt was logged
		verify(crossRefLogDao).add(any());
		
		assertNotNull(xml);
		assertTrue("should get some XML", 0 < xml.length());
	}
	
	@Test
	public void testHandleGoodResponse() throws HttpException {
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.OK.value());
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(statusLine);
		busService.handleResponse(response);
	}
	
	@Test(expected = HttpException.class)
	public void testHandleNullResponse() throws HttpException {
		busService.handleResponse(null);
	}
	
	@Test(expected = HttpException.class)
	public void testEmptyStatusLineResponse() throws HttpException {
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(null);
		busService.handleResponse(response);
	}
	
	@Test(expected = HttpException.class)
	public void testHandleNotFoundResponse() throws HttpException {
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND.value());
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(statusLine);
		busService.handleResponse(response);
	}
	
	@Test(expected = HttpException.class)
	public void testHandleUnauthorizedResponse() throws HttpException {
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED.value());
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(statusLine);
		busService.handleResponse(response);
	}
	
	@Test
	public void testThatSubmitCrossrefSwallowsAndEmailsExceptions () {
		//construct a mock transformer factory that causes the 
		//submission to fail early
		
		String msg = "very good reason";
		TransformerFactory mockTransformerFactory = mock(TransformerFactory.class);
		when(mockTransformerFactory.getTransformer(any(), any(), any())).thenThrow(new RuntimeException(msg));
		
		busService = new CrossRefBusService(
			crossRefProtocol,
			crossRefHost,
			crossRefUrl,
			crossRefPort,
			crossRefUser,
			crossRefPwd,
			crossRefSchemaUrl,
			pubsEMailer,
			mockTransformerFactory,
			crossRefLogDao
		);
		MpPublication pub = (MpPublication) CrossrefTestPubBuilder.buildNumberedSeriesPub(new MpPublication());
		
		busService.submitCrossRef(pub);
		verify(pubsEMailer).sendMail(anyString(), Mockito.contains(msg));
	}
}
