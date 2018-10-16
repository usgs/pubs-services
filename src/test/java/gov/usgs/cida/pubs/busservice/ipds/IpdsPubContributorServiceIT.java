package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.busservice.CostCenterBusService;
import gov.usgs.cida.pubs.busservice.OutsideAffiliationBusService;
import gov.usgs.cida.pubs.busservice.PersonContributorBusService;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.dao.ContributorDao;
import gov.usgs.cida.pubs.dao.ContributorDaoIT;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, TestSpringConfig.class, LocalValidatorFactoryBean.class,
			IpdsParserService.class, IpdsOutsideContributorService.class, PersonContributorBusService.class,
			CostCenterBusService.class, OutsideAffiliationBusService.class, OutsideContributor.class, Contributor.class,
			ContributorDao.class, PersonContributorDao.class, ContributorType.class, ContributorTypeDao.class,
			CostCenter.class, CostCenterDao.class, AffiliationDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/affiliation.xml")
})
public class IpdsPubContributorServiceIT extends BaseIpdsTest {

	@Autowired
	IpdsParserService parser;
	IpdsUsgsContributorService ipdsUsgsContributorService;
	@Autowired
	IpdsOutsideContributorService ipdsOutsideContributorService;
	@Autowired
	String existingOutsideAuthor;
	@Autowired
	String newOutsideAuthor;
	@Autowired
	String newOutsideEditor;
	@Autowired
	String newUsgsAuthor;
	@Autowired
	String newUsgsEditor;
	@Autowired
	String usgsContributorXml;

	IpdsPubContributorService service;

	@Autowired
	IBusService<PersonContributor<?>> personContributorBusService;
	IpdsCostCenterService ipdsCostCenterService;
	@Autowired
	IBusService<CostCenter> costCenterBusService;

	@Before
	public void setup() {
		ipdsCostCenterService = new IpdsCostCenterService(parser, ipdsWsRequester, costCenterBusService);
		ipdsUsgsContributorService = new IpdsUsgsContributorService(parser, ipdsWsRequester, ipdsCostCenterService, personContributorBusService);
		service = new IpdsPubContributorService(ipdsParser, ipdsUsgsContributorService, ipdsOutsideContributorService);
	}

	@Test
	public void createOutsideAuthorContributor() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument(newOutsideAuthor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		IpdsOutsideContributorServiceIT.assertJaneODoe(pubContributor.getContributor());
		assertEquals(1, pubContributor.getRank().intValue());
		assertEquals("Authors", pubContributor.getContributorType().getText());
	}

	@Test
	public void createOutsideEditorContributor() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument(newOutsideEditor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		IpdsOutsideContributorServiceIT.assertJillODoe(pubContributor.getContributor());
		assertEquals(3, pubContributor.getRank().intValue());
		assertEquals("Editors", pubContributor.getContributorType().getText());
	}

	@DatabaseSetup("classpath:/testData/contributor.xml")
	@Test
	public void getOutsideContributor() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument(existingOutsideAuthor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		ContributorDaoIT.assertContributor3(pubContributor.getContributor());
		assertEquals(1, pubContributor.getRank().intValue());
		assertEquals("Authors", pubContributor.getContributorType().getText());
	}

	@Test
	public void createUsgsAuthorContributor() throws SAXException, IOException {
		when(ipdsWsRequester.getContributor(anyString(), anyString())).thenReturn(usgsContributorXml);
		when(ipdsWsRequester.getCostCenter(anyInt(), anyInt())).thenReturn(costCenterXml);
		Document d = ipdsParser.makeDocument(newUsgsAuthor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		IpdsUsgsContributorServiceIT.assertUsgsContributorData((UsgsContributor) pubContributor.getContributor());
		assertEquals(1, pubContributor.getRank().intValue());
		assertEquals("Authors", pubContributor.getContributorType().getText());
	}

	@Test
	public void createUsgsEditorContributor() throws SAXException, IOException {
		when(ipdsWsRequester.getContributor(anyString(), anyString())).thenReturn(usgsContributorXml);
		when(ipdsWsRequester.getCostCenter(anyInt(), anyInt())).thenReturn(costCenterXml);
		Document d = ipdsParser.makeDocument(newUsgsEditor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		IpdsUsgsContributorServiceIT.assertUsgsContributorData((UsgsContributor) pubContributor.getContributor());
		assertEquals(1, pubContributor.getRank().intValue());
		assertEquals("Editors", pubContributor.getContributorType().getText());
	}

	@DatabaseSetup("classpath:/testData/contributor.xml")
	@Test
	public void getUsgsContributor() throws SAXException, IOException {
		when(ipdsWsRequester.getContributor(anyString(), anyString())).thenReturn(usgsContributorXml);
		when(ipdsWsRequester.getCostCenter(anyInt(), anyInt())).thenReturn(costCenterXml);
		Document d = ipdsParser.makeDocument(newUsgsAuthor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		IpdsUsgsContributorServiceIT.assertUsgsContributorData((UsgsContributor) pubContributor.getContributor());
		assertEquals(1, pubContributor.getRank().intValue());
		assertEquals("Authors", pubContributor.getContributorType().getText());
	}

}
