package gov.usgs.cida.pubs.busservice;

import static org.mockito.Mockito.*;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.dao.intfc.ICrossRefLogDao;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.transform.CrossrefTestPubBuilder;
import gov.usgs.cida.pubs.transform.TransformerFactory;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.validation.xml.XMLValidationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import org.apache.http.HttpException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Category(IntegrationTest.class)
public class CrossRefBusServiceIntegrationTest extends BaseSpringTest {

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
	protected IPublicationBusService pubBusService;
	@Autowired
	protected TransformerFactory transformerFactory;
	@Autowired
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
	public void submitNumberedPubCrossRefTest() throws IOException, XMLValidationException, UnsupportedEncodingException, HttpException, URISyntaxException {
		MpPublication pub = (MpPublication) CrossrefTestPubBuilder.buildNumberedSeriesPub(new MpPublication());
		try (CloseableHttpClient httpClient = HttpClients.createDefault()){
			busService.submitCrossRef(pub, httpClient);
		}
		verify(pubsEMailer, Mockito.never()).sendMail(any(), any());
	}
	
	@Test
	public void submitUnNumberedPubCrossRefTest() throws IOException, XMLValidationException, UnsupportedEncodingException, HttpException, URISyntaxException {
		MpPublication pub = (MpPublication) CrossrefTestPubBuilder.buildUnNumberedSeriesPub(new MpPublication());
		try (CloseableHttpClient httpClient = HttpClients.createDefault()){
			busService.submitCrossRef(pub, httpClient);
		}
		verify(pubsEMailer, Mockito.never()).sendMail(any(), any());
	}
}
