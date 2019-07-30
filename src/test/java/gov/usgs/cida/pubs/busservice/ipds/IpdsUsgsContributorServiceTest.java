package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={TestSpringConfig.class, IpdsParserService.class})
public class IpdsUsgsContributorServiceTest extends BaseTest {

	@Autowired
	@Qualifier("costCenterXml")
	public String costCenterXml;

	@MockBean
	protected IpdsWsRequester ipdsWsRequester;

	@Autowired
	protected IpdsParserService ipdsParser;

	@Autowired
	public String usgsContributorXml;

	@MockBean
	private IBusService<PersonContributor<?>> mockPersonContributorService;

	private IpdsUsgsContributorService ipdsUsgsContributorService;
	@MockBean
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
		when(ipdsWsRequester.getContributor("51", IpdsProcessTest.TEST_IPDS_CONTEXT)).thenReturn(contributor101Xml);
		when(ipdsWsRequester.getContributor("123", IpdsProcessTest.TEST_IPDS_CONTEXT)).thenReturn(usgsContributorXml);
		ipdsUsgsContributorService = new IpdsUsgsContributorService(ipdsParser, ipdsWsRequester, mockCostCenterService, mockPersonContributorService);

		reset(mockPersonContributorService, mockCostCenterService);
	}

	@Test
	public void createCostCenterTest() throws SAXException, IOException {
		CostCenter ccOne = new CostCenter();
		ccOne.setId(1);
		when(mockCostCenterService.getCostCenter(anyInt())).thenReturn(null);
		when(mockCostCenterService.createCostCenter(anyInt())).thenReturn(ccOne);
		Document d = ipdsParser.makeDocument("<root><d:CostCenter>1</d:CostCenter></root>");
		CostCenter costCenter = ipdsUsgsContributorService.getCostCenter(d.getDocumentElement());
		assertEquals(1, costCenter.getId().intValue());
		verify(mockCostCenterService).getCostCenter(anyInt());
		verify(mockCostCenterService).createCostCenter(anyInt());
	}


	@Test
	public void getCostCenterTest() throws SAXException, IOException {
		CostCenter ccOne = new CostCenter();
		ccOne.setId(1);
		when(mockCostCenterService.getCostCenter(anyInt())).thenReturn(ccOne);
		Document d = ipdsParser.makeDocument("<root><d:CostCenter>1</d:CostCenter></root>");
		CostCenter costCenter = ipdsUsgsContributorService.getCostCenter(d.getDocumentElement());
		assertEquals(1, costCenter.getId().intValue());
		verify(mockCostCenterService).getCostCenter(anyInt());
		verify(mockCostCenterService, never()).createCostCenter(anyInt());
	}

	@Test
	public void badCostCenterTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument("<root></root>");
		CostCenter costCenter = ipdsUsgsContributorService.getCostCenter(d.getDocumentElement());
		assertNull(costCenter);
		verify(mockCostCenterService, never()).getCostCenter(anyInt());
		verify(mockCostCenterService, never()).createCostCenter(anyInt());
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