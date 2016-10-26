package gov.usgs.cida.pubs.busservice.ipds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.AffiliationDaoTest;
import gov.usgs.cida.pubs.dao.ContributorDaoTest;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class IpdsOutsideContributorServiceTest extends BaseIpdsTest {
	
	@Autowired
	public String existingOutsideContributorXml;

	@Autowired
	public String newOutsideContributorUsgsAffiliationXml;
	
	@Autowired
	public String newOutsideContributorXml;
	
	@Autowired
	@Qualifier("outsideAffiliationBusService")
	private IBusService<OutsideAffiliation> outsideAffiliationBusService;

	@Autowired
	@Qualifier("personContributorBusService")
	private IBusService<PersonContributor<?>> personContributorBusService;

	private IpdsOutsideContributorService ipdsOutsideContributorService;

	@Before
	public void setup() {
		ipdsOutsideContributorService = new IpdsOutsideContributorService(ipdsParser, outsideAffiliationBusService, personContributorBusService);
	}

	@Test
	public void createOutsideContributorTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument(newOutsideContributorXml);
		OutsideContributor contributor = ipdsOutsideContributorService.createContributor(d.getDocumentElement());
		assertNotNull(contributor);
		assertNotNull(contributor.getId());
		assertEquals("ODoe", contributor.getFamily());
		assertEquals("Jane", contributor.getGiven());
		assertEquals("http://orcid.org/1234-1234-1234-1234", contributor.getOrcid());
		assertNull(contributor.getEmail());
		assertEquals("7", contributor.getAffiliations().toArray(new OutsideAffiliation[1])[0].getId().toString());
	}

	@Test
	public void getOutsideContributorTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument(existingOutsideContributorXml);
		OutsideContributor contributor = ipdsOutsideContributorService.getContributor(d.getDocumentElement());
		ContributorDaoTest.assertContributor3(contributor);
	}

	@Test
	public void getOutsideAffiliationTest() {
		Affiliation<?> affiliation = ipdsOutsideContributorService.getOutsideAffiliation("Outside Affiliation 1");
		AffiliationDaoTest.assertAffiliation5(affiliation);
		
		affiliation = ipdsOutsideContributorService.getOutsideAffiliation("Affiliation Cost Center 1");
		assertNull(affiliation);
	}

	@Test
	public void createOutsideAffiliationTest() {
		Affiliation<?> affiliation = ipdsOutsideContributorService.createOutsideAffiliation("Outside Test");
		assertNotNull(affiliation.getId());
		assertEquals("Outside Test", affiliation.getText());
		assertTrue(affiliation.isActive());
		assertFalse(affiliation.isUsgs());
	}
}