package gov.usgs.cida.pubs.busservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.CrossRefLogDao;
import gov.usgs.cida.pubs.dao.intfc.ICrossRefLogDao;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.transform.CrossrefTestPubBuilder;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.validation.xml.XMLValidationException;

@ContextConfiguration(classes = FreeMarkerAutoConfiguration.class)
@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LocalValidatorFactoryBean.class, CrossRefLogDao.class,
			PublicationBusService.class, ConfigurationService.class,
			ContributorType.class, ContributorTypeDao.class})
public class CrossRefBusServiceIT extends BaseIT {

	@MockBean
	protected PubsEMailer pubsEMailer;
	@Autowired
	protected IPublicationBusService pubBusService;
	@Autowired
	@Qualifier("freeMarkerConfiguration")
	protected Configuration templateConfiguration;
	@Autowired
	protected ICrossRefLogDao crossRefLogDao;
	@Autowired
	protected ConfigurationService configurationService;

	protected CrossRefBusService busService;

	@Before
	public void initTest() throws Exception {
		busService = new CrossRefBusService(
			configurationService,
			pubsEMailer,
			crossRefLogDao,
			templateConfiguration,
			pubBusService
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

	@Test
	public void submitMinimalCrossRefTest() throws IOException, XMLValidationException, UnsupportedEncodingException, HttpException, URISyntaxException {
		MpPublication pub = (MpPublication) CrossrefTestPubBuilder.buildNumberedSeriesPub(new MpPublication());
		pub.setContributors(null);
		try (CloseableHttpClient httpClient = HttpClients.createDefault()){
			busService.submitCrossRef(pub, httpClient);
		}
		verify(pubsEMailer, Mockito.never()).sendMail(any(), any());
	}

	@Test
	public void submitDataReleaseCrossRefTest() throws IOException, XMLValidationException, UnsupportedEncodingException, HttpException, URISyntaxException {
		MpPublication pub = (MpPublication) CrossrefTestPubBuilder.buildNumberedSeriesPub(new MpPublication());
		pub.getLinks().add(CrossrefTestPubBuilder.buildDataReleaseLink());
		try (CloseableHttpClient httpClient = HttpClients.createDefault()){
			busService.submitCrossRef(pub, httpClient);
		}
		verify(pubsEMailer, Mockito.never()).sendMail(any(), any());
	}
}
