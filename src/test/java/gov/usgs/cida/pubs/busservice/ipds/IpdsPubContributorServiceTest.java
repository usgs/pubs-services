package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.ContributorDaoTest;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/affiliation.xml")
})
public class IpdsPubContributorServiceTest extends BaseIpdsTest {

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

	@Mock
	IpdsWsRequester requester;
	@Autowired
	IBusService<PersonContributor<?>> personContributorBusService;
	IpdsCostCenterService ipdsCostCenterService;
	@Autowired
	IBusService<CostCenter> costCenterBusService;

	@Before
	public void setup() {
		ipdsCostCenterService = new IpdsCostCenterService(parser, requester, costCenterBusService);
		ipdsUsgsContributorService = new IpdsUsgsContributorService(parser, requester, ipdsCostCenterService, personContributorBusService);
		service = new IpdsPubContributorService(ipdsParser, ipdsUsgsContributorService, ipdsOutsideContributorService);
	}

	@Test
	public void createOutsideAuthorContributor() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument(newOutsideAuthor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		IpdsOutsideContributorServiceTest.assertJaneODoe(pubContributor.getContributor());
		assertEquals(1, pubContributor.getRank().intValue());
		assertEquals("Authors", pubContributor.getContributorType().getText());
	}

	@Test
	public void createOutsideEditorContributor() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument(newOutsideEditor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		IpdsOutsideContributorServiceTest.assertJillODoe(pubContributor.getContributor());
		assertEquals(3, pubContributor.getRank().intValue());
		assertEquals("Editors", pubContributor.getContributorType().getText());
	}

	@DatabaseSetup("classpath:/testData/contributor.xml")
	@Test
	public void getOutsideContributor() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument(existingOutsideAuthor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		ContributorDaoTest.assertContributor3(pubContributor.getContributor());
		assertEquals(1, pubContributor.getRank().intValue());
		assertEquals("Authors", pubContributor.getContributorType().getText());
	}

	@Test
	public void createUsgsAuthorContributor() throws SAXException, IOException {
		when(requester.getContributor(anyString(), anyString())).thenReturn(usgsContributorXml);
		when(requester.getCostCenter(anyString(), anyString())).thenReturn(costCenterXml);
		Document d = ipdsParser.makeDocument(newUsgsAuthor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		IpdsUsgsContributorServiceTest.assertUsgsContributorData((UsgsContributor) pubContributor.getContributor());
		assertEquals(1, pubContributor.getRank().intValue());
		assertEquals("Authors", pubContributor.getContributorType().getText());
	}

	@Test
	public void createUsgsEditorContributor() throws SAXException, IOException {
		when(requester.getContributor(anyString(), anyString())).thenReturn(usgsContributorXml);
		when(requester.getCostCenter(anyString(), anyString())).thenReturn(costCenterXml);
		Document d = ipdsParser.makeDocument(newUsgsEditor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		IpdsUsgsContributorServiceTest.assertUsgsContributorData((UsgsContributor) pubContributor.getContributor());
		assertEquals(1, pubContributor.getRank().intValue());
		assertEquals("Editors", pubContributor.getContributorType().getText());
	}

	@DatabaseSetup("classpath:/testData/contributor.xml")
	@Test
	public void getUsgsContributor() throws SAXException, IOException {
		when(requester.getContributor(anyString(), anyString())).thenReturn(usgsContributorXml);
		when(requester.getCostCenter(anyString(), anyString())).thenReturn(costCenterXml);
		Document d = ipdsParser.makeDocument(newUsgsAuthor);
		MpPublicationContributor pubContributor = service.buildPublicationContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		IpdsUsgsContributorServiceTest.assertUsgsContributorData((UsgsContributor) pubContributor.getContributor());
		assertEquals(1, pubContributor.getRank().intValue());
		assertEquals("Authors", pubContributor.getContributorType().getText());
	}

}
