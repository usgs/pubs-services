package gov.usgs.cida.pubs.busservice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.intfc.ICrossRefLogDao;
import gov.usgs.cida.pubs.domain.CrossRefLog;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.transform.CrossrefTestPubBuilder;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.validation.xml.XMLValidationException;

@ContextConfiguration(classes = FreeMarkerAutoConfiguration.class)
@SpringBootTest(webEnvironment=WebEnvironment.NONE)
public class CrossRefBusServiceTest extends BaseTest {
	protected String UUID_REGEX = "[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}";
	protected String TIMESTAMP_REGEX = "[1-9][0-9]+";

	@Captor
	protected ArgumentCaptor<String> captor;

	@Mock
	protected ConfigurationService configurationService;
	@MockBean
	protected PubsEMailer pubsEMailer;
	@MockBean
	protected ICrossRefLogDao crossRefLogDao;
	@Autowired
	protected Configuration templateConfiguration;

	protected CrossRefBusService busService;

	public static final String CROSSREF_DEPOSITOR_EMAIL = "nobody@usgs.gov";
	public static final String CROSSREF_PROTOCOL = "https";
	public static final String CROSSREF_HOST = "test.crossref.org";
	public static final String CROSSREF_URL = "/servlet/deposit";
	public static final Integer CROSSREF_PORT = -1;
	public static final String CROSSREF_USERNAME = "CROSSREF_USERNAME";
	public static final String CROSSREF_PASSWORD = "CROSSREF_PASSWORD";
	public static final String CROSSREF_SCHEMA_URL = "http://www.crossref.org/schema/deposit/crossref4.4.0.xsd";

	@BeforeEach
	public void initTest() {
		when(configurationService.getCrossrefDepositorEmail()).thenReturn(CROSSREF_DEPOSITOR_EMAIL);
		when(configurationService.getCrossrefProtocol()).thenReturn(CROSSREF_PROTOCOL);
		when(configurationService.getCrossrefHost()).thenReturn(CROSSREF_HOST);
		when(configurationService.getCrossrefUrl()).thenReturn(CROSSREF_URL);
		when(configurationService.getCrossrefPort()).thenReturn(CROSSREF_PORT);
		when(configurationService.getCrossrefUser()).thenReturn(CROSSREF_USERNAME);
		when(configurationService.getCrossrefPwd()).thenReturn(CROSSREF_PASSWORD);
		when(configurationService.getCrossrefSchemaUrl()).thenReturn(CROSSREF_SCHEMA_URL);
		when(configurationService.getWarehouseEndpoint()).thenReturn(PUBS_WAREHOUSE_ENDPOINT);
		when(configurationService.getDisplayHost()).thenReturn(SWAGGER_DISPLAY_HOST);
		busService = new CrossRefBusService(
			configurationService,
			pubsEMailer,
			crossRefLogDao,
			templateConfiguration
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

		when(configurationService.getCrossrefProtocol()).thenReturn(protocol);
		when(configurationService.getCrossrefHost()).thenReturn(host);
		when(configurationService.getCrossrefPort()).thenReturn(port);
		when(configurationService.getCrossrefUrl()).thenReturn(base);
		when(configurationService.getCrossrefUser()).thenReturn(user);
		when(configurationService.getCrossrefPwd()).thenReturn(password);

		String actual = busService.buildCrossRefUrl();

		assertFalse(actual.contains(user), "special user characters should be escaped from url");
		assertFalse(actual.contains(password), "special password characters should be escaped from url");

		String encodedUser = URLEncoder.encode(user, "UTF-8");
		String encodedPassword = URLEncoder.encode(password, "UTF-8");

		assertTrue(actual.contains(encodedUser), "special user characters should be escaped from url");
		assertTrue(actual.contains(encodedPassword), "special password characters should be escaped from url");
	}

	@Test
	public void verifyCrossrefUrlBuilderDoesNotSwallowURIProblems() throws UnsupportedEncodingException {
		//do not specify characters that need %-encoding
		String password = "MockPassword";
		when(configurationService.getCrossrefProtocol()).thenReturn("");
		when(configurationService.getCrossrefHost()).thenReturn("");
		when(configurationService.getCrossrefPort()).thenReturn(-2);
		when(configurationService.getCrossrefUrl()).thenReturn("");
		when(configurationService.getCrossrefUser()).thenReturn("");
		when(configurationService.getCrossrefPwd()).thenReturn(password);

		try{
			busService.buildCrossRefUrl();
			fail("Should have raised Exception");
		} catch (URISyntaxException ex) {
			assertFalse(ex.getMessage().contains(password));
			if(null != ex.getCause()){
				assertFalse(ex.getCause().getMessage().contains(password));
			}
		}
	}

	@Test
	public void performMockCrossRefPost() throws IOException {
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

	@Test()
	public void performFailingCrossRefPost() throws IOException {
		HttpPost httpPost = mock(HttpPost.class);
		CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
		when(httpClient.execute(any(), eq(httpPost), any(HttpContext.class))).thenThrow(IOException.class);
		assertThrows(IOException.class, () -> {
			busService.performCrossRefPost(httpPost, httpClient);
		});
	}

	@Test
	public void tetBuildCrossRefPost() throws IOException {
		String expectedBody = "expectedBody";
		String expectedIndexId = "sir123456789";
		HttpPost post = busService.buildCrossRefPost(expectedBody, PUBS_WAREHOUSE_ENDPOINT, expectedIndexId);
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
		String requestBody = baos.toString(PubsConstantsHelper.DEFAULT_ENCODING);
		StringUtils.isNotBlank(requestBody);
		assertTrue(requestBody.contains(PubsConstantsHelper.DEFAULT_ENCODING));
		assertTrue(requestBody.contains(PubsConstantsHelper.MEDIA_TYPE_CROSSREF_VALUE));
		assertTrue(requestBody.contains(expectedBody));
		assertTrue(requestBody.contains(expectedIndexId));
	}

	@Test
	public void getGoodCrossrefXml() throws XMLValidationException, IOException {
		Publication<?> pub = CrossrefTestPubBuilder.buildUnNumberedSeriesPub(new Publication<>());
		String xml = busService.getCrossRefXml(pub);

		//verify that the attempt was logged
		verify(crossRefLogDao).add(any());

		assertTrue(StringUtils.isNotBlank(xml), "should get some XML");

		xml = replaceUuidAndTimestamp(harmonizeXml(xml));
		String expectedXml = harmonizeXml(getFile("testResult/goodCrossref.xml"));
		assertEquals(expectedXml, xml);
	}

	@Test
	public void testHandleGoodResponse() {
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.OK.value());
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(statusLine);
		assertDoesNotThrow(() -> {
			busService.handleResponse(response);
		});
	}

	@Test()
	public void testHandleNullResponse() {
		assertThrows(HttpException.class, () -> {
			busService.handleResponse(null);
		});
	}

	@Test()
	public void testEmptyStatusLineResponse() {
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(null);
		assertThrows(HttpException.class, () -> {
			busService.handleResponse(response);
		});
	}

	@Test()
	public void testHandleNotFoundResponse() {
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND.value());
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(statusLine);
		assertThrows(HttpException.class, () -> {
			busService.handleResponse(response);
		});
	}

	@Test
	public void testHandleUnauthorizedResponse() {
		StatusLine statusLine = mock(StatusLine.class);
		when(statusLine.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED.value());
		HttpResponse response = mock(HttpResponse.class);
		when(response.getStatusLine()).thenReturn(statusLine);
		assertThrows(HttpException.class, () -> {
			busService.handleResponse(response);
		});
	}

	@Test
	public void testThatSubmitCrossrefSwallowsAndEmailsExceptions () {
		//cause the submission to fail
		String causeMessage = "very good reason";
		when(crossRefLogDao.add(any(CrossRefLog.class))).thenThrow(new RuntimeException(causeMessage));

		busService = new CrossRefBusService(
			configurationService,
			pubsEMailer,
			crossRefLogDao,
			templateConfiguration
		);
		MpPublication pub = (MpPublication) CrossrefTestPubBuilder.buildNumberedSeriesPub(new MpPublication());

		busService.submitCrossRef(pub);
		verify(pubsEMailer).sendMail(anyString(), captor.capture());
		String emailBody = captor.getValue();
		assertTrue(emailBody.contains(causeMessage));
		assertTrue(emailBody.contains(configurationService.getDisplayHost()));
		assertTrue(emailBody.contains(pub.getIndexId()));
	}

	/**
	 * Replace randomly-generated and time stamp values in xml with test values so that we can compare consistent values.
	 */
	private String replaceUuidAndTimestamp(String xml) {
		String updated = xml;
		updated = updated.replaceFirst(UUID_REGEX, "fb51e752-ee91-4b4d-a6d8-ec5d20190b80");
		updated = updated.replaceFirst("<timestamp>" + TIMESTAMP_REGEX + "</timestamp>",
				"<timestamp>1586278323273</timestamp>");

		return updated;
	}

}
