package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.apache.http.HttpHost;
import org.apache.http.auth.NTCredentials;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;
import gov.usgs.cida.pubs.utility.PubsEMailer;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={TestSpringConfig.class})
public class IpdsWsRequesterTest extends BaseTest {

	protected static final String IPDS_HOST = "bad.usgs.gov";
	@Autowired
	protected NTCredentials credentials;
	@MockBean
	protected ConfigurationService configurationService;
	@MockBean
	protected PubsEMailer pubsEMailer;
	public IpdsWsRequester requester;

	@Before
	public void setUp() throws Exception {
		requester = new IpdsWsRequester(configurationService, credentials, pubsEMailer);
		when(configurationService.getIpdsEndpoint()).thenReturn(IPDS_HOST);
	}

//	@Test
//	public void doGetTest() {
//		//This might be brittle, but hopefully they don't delete costCenter 1.
//		HttpResponse response = requester.doGet(IpdsWsRequester.URL_PREFIX + "CostCenters(1)");
//		String xml = null;
//		try {
//			HttpEntity entity = response.getEntity();
//			xml = EntityUtils.toString(entity);
//			EntityUtils.consume(response.getEntity());
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//		assertTrue(xml.contains("<id>https://" + ipdsEndpoint + IpdsWsRequester.URL_PREFIX + "CostCenters(1)</id>"));
//		assertTrue(xml.contains("<d:Id m:type=\"Edm.Int32\">1</d:Id>"));
//	}

	//TODO Activate this Test?
//  @Test
//  public void getContributorTest() {
//  }

	//TODO Activate this Test?
//  @Test
//  public void getContributorsTest() {
//  }

	//TODO Activate this Test?
//  @Test
//  public void getCostCenterTest() {
//  }

	//TODO Activate this Test?
//	@Test
//	public void getHttpClientTest() {
//		HttpClient client = new DefaultHttpClient();
//		NTCredentials creds = new NTCredentials("TEST", "PASSWORD", "", "GS");
//		((DefaultHttpClient) httpClient).getCredentialsProvider().setCredentials(AuthScope.ANY, creds);
//
//		TestIpdsWsRequester requester = new TestIpdsWsRequester(client, creds);
//		DefaultHttpClient httpClient = (DefaultHttpClient) requester.getHttpClient();
//		assertNotNull(httpClient);
//		assertNotNull(httpClient.getCredentialsProvider());
//		assertNotNull(httpClient.getCredentialsProvider().getCredentials(AuthScope.ANY));
//		Credentials credentials = httpClient.getCredentialsProvider().getCredentials(AuthScope.ANY);
//		assertEquals(NTCredentials.class, credentials.getClass());
//		assertEquals("TEST", ((NTCredentials) credentials).getUserName());
//		assertEquals("PASSWORD", creds.getPassword());
//		assertEquals("", ((NTCredentials) credentials).getWorkstation());
//		assertEquals("GS", ((NTCredentials) credentials).getDomain());
//	}

	@Test
	public void getHttpHostTest() {
		IpdsWsRequester fRequester = new IpdsWsRequester(new ConfigurationService(), null, null);
		try {
			fRequester.getHttpHost();
			fail("Didn't fail without a host name.");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertEquals("Host name may not be null", e.getMessage());
		}

		HttpHost host = requester.getHttpHost();
		assertNotNull(host);
		assertEquals(IPDS_HOST, host.getHostName());
		assertEquals(IpdsWsRequester.IPDS_PORT, host.getPort());
		assertEquals(IpdsWsRequester.IPDS_PROTOCOL, host.getSchemeName());
	}

	//TODO Activate this Test?
//  @Test
//  public void getIpdsProductXmlTest() {
//  }

	//TODO Activate this Test?
//  @Test
//  public void getIpdsXmlTest() {
//  }

	//TODO Activate this Test?
//  @Test
//  public void getIpdsCostCenterXml() {
//  }

	//TODO Activate this Test?
//  @Test
//  public void getNotesTest() {
//  }

	//TODO Activate this Test?
//  @Test
//  public void getSpnProductionTest() {
//  }

	//TODO Activate this Test?
//	@Test
//	public void updateIpdsDoiTest() {
//		HttpClient client = new TestDefaultHttpClient();
//		NTCredentials creds = new NTCredentials("TEST", "PASSWORD", "", "GS");
//		TestIpdsWsRequester requester = new TestIpdsWsRequester(client, creds);
//		requester.setIpdsEndpoint("inIpdsEndpoint");
//		requester.setIpdsProtocol("http");
//		assertEquals("\n\tERROR: Unable to get Etag for:/_vti_bin/ListData.svc/InformationProduct(null)/DigitalObjectIdentifier - null response or status line.",
//				requester.updateIpdsDoi(new MpPublication()));
//		MpPublication mp = new MpPublication();
//		mp.setIpdsInternalId("1");
//		assertEquals("\n\tERROR: Unable to get Etag for:/_vti_bin/ListData.svc/InformationProduct(1)/DigitalObjectIdentifier - Status Code:404",
//				requester.updateIpdsDoi(mp));
//		mp.setIpdsInternalId("2");
//		assertEquals("\n\tERROR: Unable to get Etag for:/_vti_bin/ListData.svc/InformationProduct(2)/DigitalObjectIdentifier - Missing Etag header.",
//				requester.updateIpdsDoi(mp));
//		mp.setIpdsInternalId("3");
//		assertEquals("\n\tERROR: Bad Response from httpPut: /_vti_bin/ListData.svc/InformationProduct(3)/DigitalObjectIdentifier: No Response",
//				requester.updateIpdsDoi(mp));
//		mp.setIpdsInternalId("4");
//		assertEquals("\n\tERROR: Bad Response from httpPut: /_vti_bin/ListData.svc/InformationProduct(4)/DigitalObjectIdentifier: Status Code : 404",
//				requester.updateIpdsDoi(mp));
//		mp.setIpdsInternalId("5");
//		mp.setDoiName("doiname");
//		assertEquals("\n\tDOI updated in IPDS: doiname",
//				requester.updateIpdsDoi(mp));
//	}

}
