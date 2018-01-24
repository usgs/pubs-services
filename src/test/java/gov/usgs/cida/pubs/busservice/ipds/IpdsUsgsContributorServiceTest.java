package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.ContributorDaoTest;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/ipdsUsgsContributorService.xml"),
	@DatabaseSetup("classpath:/testData/affiliation.xml")
})
public class IpdsUsgsContributorServiceTest extends BaseIpdsTest {

	@Autowired
	public String usgsContributorXml;

	@Autowired
	@Qualifier("personContributorBusService")
	private IBusService<PersonContributor<?>> personContributorBusService;
	@Mock
	private IBusService<PersonContributor<?>> mockPersonContributorService;

	private IpdsUsgsContributorService ipdsUsgsContributorService;
	@Autowired
	private IpdsCostCenterService ipdsCostCenterService;
	@Mock
	private IpdsCostCenterService mockCostCenterService;

	private String contributor1Xml = "<root><d:WorkEMail>con@usgs.gov</d:WorkEMail></root>";
	private String contributor4Xml = "<root><d:ORCID>http://orcid.org/0000-0000-0000-0004</d:ORCID></root>";
	private String contributor5Xml = "<root><d:WorkEMail>con5@usgs.gov</d:WorkEMail></root>";
	private String contributor6Xml = "<root><d:ORCID>http://orcid.org/0000-0000-0000-0006</d:ORCID></root>";
	private String contributor101Xml = "<root><d:WorkEMail>con101@usgs.gov</d:WorkEMail></root>";

	@Before
	public void setup() {
		when(ipdsWsRequester.getContributor("1", IpdsProcessTest.TEST_IPDS_CONTEXT)).thenReturn(contributor1Xml);
		when(ipdsWsRequester.getContributor("4", IpdsProcessTest.TEST_IPDS_CONTEXT)).thenReturn(contributor4Xml);
		when(ipdsWsRequester.getContributor("5", IpdsProcessTest.TEST_IPDS_CONTEXT)).thenReturn(contributor5Xml);
		when(ipdsWsRequester.getContributor("6", IpdsProcessTest.TEST_IPDS_CONTEXT)).thenReturn(contributor6Xml);
		when(ipdsWsRequester.getContributor("101", IpdsProcessTest.TEST_IPDS_CONTEXT)).thenReturn(contributor101Xml);
		when(ipdsWsRequester.getContributor("123", IpdsProcessTest.TEST_IPDS_CONTEXT)).thenReturn(usgsContributorXml);
		ipdsUsgsContributorService = new IpdsUsgsContributorService(ipdsParser, ipdsWsRequester, ipdsCostCenterService, personContributorBusService);
	}

	@Test
	public void getByORCIDNotFoundTest() throws SAXException, IOException {
		UsgsContributor contributor = ipdsUsgsContributorService.getByORCID("http://orcid.org/0000-0000-0000-0006");
		assertNull(contributor);
	}

	@Test
	public void getByORCIDMultipleTest() throws SAXException, IOException {
		UsgsContributor contributor = ipdsUsgsContributorService.getByORCID("http://orcid.org/0000-0000-0000-0004");
		ContributorDaoTest.assertContributor4(contributor);
	}

	@Test
	public void getByORCIDSingleTest() throws SAXException, IOException {
		UsgsContributor contributor = ipdsUsgsContributorService.getByORCID("http://orcid.org/0000-0000-0000-0104");
		assertContributor104(contributor);
	}

	@Test
	public void getByEmailNotFoundTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>5</d:AuthorNameId></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.getByEmail(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		assertNull(contributor);
	}

	@Test
	public void getByEmailNoEmailProvidedTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>4</d:AuthorNameId></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.getByEmail(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		assertNull(contributor);
	}

	@Test
	public void getByEmailMultipleTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>1</d:AuthorNameId></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.getByEmail(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		ContributorDaoTest.assertContributor1(contributor);
	}

	@Test
	public void getByEmailSingleTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>101</d:AuthorNameId><d:ORCID>http://orcid.org/0000-0000-0000-0101</d:ORCID></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.getByEmail(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		assertContributor101(contributor);
	}

	@Test
	public void createContributorTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>123</d:AuthorNameId><d:ORCID>http://orcid.org/0000-0000-0000-0000</d:ORCID><d:CostCenter>4</d:CostCenter></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.createContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		assertNotNull(contributor);
		assertNotNull(contributor.getId());
		assertUsgsContributorData(contributor);
		assertEquals(1, contributor.getAffiliations().size());
		assertEquals("1", contributor.getAffiliations().toArray(new CostCenter[1])[0].getId().toString());
	}

	@Test
	public void bindContributorTest() throws SAXException, IOException, ParserConfigurationException {
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>123</d:AuthorNameId><d:ORCID>http://orcid.org/0000-0000-0000-0000</d:ORCID></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.bindContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		assertNotNull(contributor);
		assertNull(contributor.getId());
		assertUsgsContributorBindData(contributor);
	}

	@Test
	public void getContributorNoOrcidOrEmailProvidedTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>4</d:AuthorNameId><d:CostCenter>1</d:CostCenter></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.getContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		assertNull(contributor);
	}

	@Test
	public void getContributorFoundByOrcidTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>4</d:AuthorNameId><d:ORCID>http://orcid.org/0000-0000-0000-0004</d:ORCID><d:CostCenter>1</d:CostCenter></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.getContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		ContributorDaoTest.assertContributor4(contributor);
		assertEquals(1, contributor.getAffiliations().size());
		assertEquals("4", contributor.getAffiliations().toArray(new CostCenter[1])[0].getId().toString());
	}

	@Test
	public void getContributorFoundByEmailTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root><d:AuthorNameId>101</d:AuthorNameId><d:ORCID>http://orcid.org/0000-0000-0000-0101</d:ORCID></root>");
		UsgsContributor contributor = ipdsUsgsContributorService.getContributor(d.getDocumentElement(), IpdsProcessTest.TEST_IPDS_CONTEXT);
		assertContributor101(contributor);
	}

	public static void assertUsgsContributorData(UsgsContributor contributor) {
		assertUsgsContributorBindData(contributor);
		assertTrue(contributor.isPreferred());
		assertEquals("http://orcid.org/0000-0000-0000-0000", contributor.getOrcid());
	}

	private static void assertUsgsContributorBindData(UsgsContributor contributor) {
		assertEquals("Jane", contributor.getGiven());
		assertEquals("Doe", contributor.getFamily());
		assertEquals("jmdoe@usgs.gov", contributor.getEmail());
		assertFalse(contributor.isCorporation());
		assertTrue(contributor.isUsgs());
	}

	public static void assertContributor101(Contributor<?> contributor) {
		assertEquals(101, contributor.getId().intValue());
		assertTrue(contributor instanceof UsgsContributor);
		UsgsContributor usgsContributor = (UsgsContributor) contributor;
		assertEquals("101Family", usgsContributor.getFamily());
		assertEquals("101Given", usgsContributor.getGiven());
		assertEquals("101Suffix", usgsContributor.getSuffix());
		assertEquals("con101@usgs.gov", usgsContributor.getEmail());
		assertEquals("http://orcid.org/0000-0000-0000-0101", usgsContributor.getOrcid());
		assertTrue(usgsContributor.isUsgs());
		assertFalse(usgsContributor.isCorporation());
		assertTrue(usgsContributor.isPreferred());
	}

	public static void assertContributor104(Contributor<?> contributor) {
		assertEquals(104, contributor.getId().intValue());
		assertTrue(contributor instanceof UsgsContributor);
		UsgsContributor usgsContributor = (UsgsContributor) contributor;
		assertEquals("104Family", usgsContributor.getFamily());
		assertEquals("104Given", usgsContributor.getGiven());
		assertEquals("104Suffix", usgsContributor.getSuffix());
		assertEquals("con104@usgs.gov", usgsContributor.getEmail());
		assertEquals("http://orcid.org/0000-0000-0000-0104", usgsContributor.getOrcid());
		assertTrue(usgsContributor.isUsgs());
		assertFalse(usgsContributor.isCorporation());
		assertTrue(usgsContributor.isPreferred());
	}

	@Test
	public void createCostCenterTest() throws SAXException, IOException {
		ipdsUsgsContributorService = new IpdsUsgsContributorService(ipdsParser, ipdsWsRequester, mockCostCenterService, personContributorBusService);
		CostCenter ccOne = new CostCenter();
		ccOne.setId(1);
		when(mockCostCenterService.getCostCenter(anyString())).thenReturn(null);
		when(mockCostCenterService.createCostCenter(anyString())).thenReturn(ccOne);
		Document d = ipdsParser.makeDocument("<root><d:CostCenter>1</d:CostCenter></root>");
		CostCenter costCenter = ipdsUsgsContributorService.getCostCenter(d.getDocumentElement());
		assertEquals(1, costCenter.getId().intValue());
		verify(mockCostCenterService).getCostCenter(anyString());
		verify(mockCostCenterService).createCostCenter(anyString());
	}


	@Test
	public void getCostCenterTest() throws SAXException, IOException {
		ipdsUsgsContributorService = new IpdsUsgsContributorService(ipdsParser, ipdsWsRequester, mockCostCenterService, personContributorBusService);
		CostCenter ccOne = new CostCenter();
		ccOne.setId(1);
		when(mockCostCenterService.getCostCenter(anyString())).thenReturn(ccOne);
		Document d = ipdsParser.makeDocument("<root><d:CostCenter>1</d:CostCenter></root>");
		CostCenter costCenter = ipdsUsgsContributorService.getCostCenter(d.getDocumentElement());
		assertEquals(1, costCenter.getId().intValue());
		verify(mockCostCenterService).getCostCenter(anyString());
		verify(mockCostCenterService, never()).createCostCenter(anyString());
	}

	@Test
	public void badCostCenterTest() throws SAXException, IOException {
		ipdsUsgsContributorService = new IpdsUsgsContributorService(ipdsParser, ipdsWsRequester, mockCostCenterService, personContributorBusService);
		Document d = ipdsParser.makeDocument("<root></root>");
		CostCenter costCenter = ipdsUsgsContributorService.getCostCenter(d.getDocumentElement());
		assertNull(costCenter);
		verify(mockCostCenterService, never()).getCostCenter(anyString());
		verify(mockCostCenterService, never()).createCostCenter(anyString());
	}

	@Test
	public void updateAffiliationsTest() {
		CostCenter ccOne = new CostCenter();
		ccOne.setId(1);
		UsgsContributor contributor = new UsgsContributor();
		UsgsContributor contributor2 = new UsgsContributor();
		contributor.getAffiliations().add(ccOne);
		when(mockPersonContributorService.updateObject(any())).thenAnswer(x -> contributor);
		ipdsUsgsContributorService = new IpdsUsgsContributorService(ipdsParser, ipdsWsRequester, mockCostCenterService, mockPersonContributorService);
		assertEquals(contributor, ipdsUsgsContributorService.updateAffiliations(contributor2, ccOne));
		verify(mockPersonContributorService).updateObject(any());
	}

	@Test
	public void doNotUpdateAffiliationsTest() {
		CostCenter ccOne = new CostCenter();
		ccOne.setId(1);
		UsgsContributor contributor = new UsgsContributor();
		contributor.getAffiliations().add(ccOne);
		ipdsUsgsContributorService = new IpdsUsgsContributorService(ipdsParser, ipdsWsRequester, mockCostCenterService, mockPersonContributorService);
		assertEquals(contributor, ipdsUsgsContributorService.updateAffiliations(contributor, ccOne));
		verify(mockPersonContributorService, never()).updateObject(any());
	}

}