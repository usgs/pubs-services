package gov.usgs.cida.pubs.busservice.ipds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.busservice.OutsideAffiliationBusService;
import gov.usgs.cida.pubs.busservice.PersonContributorBusService;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.dao.AffiliationDaoIT;
import gov.usgs.cida.pubs.dao.ContributorDao;
import gov.usgs.cida.pubs.dao.ContributorDaoIT;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.springinit.TestSpringConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, TestSpringConfig.class, LocalValidatorFactoryBean.class,
			IpdsParserService.class, OutsideAffiliationBusService.class, PersonContributorBusService.class,
			IpdsOutsideContributorService.class, Affiliation.class, AffiliationDao.class,
			PersonContributor.class, Contributor.class, PersonContributorDao.class, ContributorDao.class,
			OutsideAffiliation.class, OutsideAffiliationDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class IpdsOutsideContributorServiceIT extends BaseIpdsTest {

	@Autowired
	public String existingOutsideAuthor;

	@Autowired
	public String newOutsideContributorUsgsAffiliationXml;

	@Autowired
	public String newOutsideAuthor;

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
		Document d = ipdsParser.makeDocument(newOutsideAuthor);
		OutsideContributor contributor = ipdsOutsideContributorService.createContributor(d.getDocumentElement());
		assertJaneODoe(contributor);
	}

	@Test
	public void getOutsideContributorTest() throws SAXException, IOException {
		Document d = ipdsParser.makeDocument(existingOutsideAuthor);
		OutsideContributor contributor = ipdsOutsideContributorService.getContributor(d.getDocumentElement());
		ContributorDaoIT.assertContributor3(contributor);
	}

	@Test
	public void getOutsideAffiliationTest() {
		Affiliation<?> affiliation = ipdsOutsideContributorService.getOutsideAffiliation("Outside Affiliation 1");
		AffiliationDaoIT.assertAffiliation5(affiliation);

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

	public static void assertJaneODoe(Contributor<?> contrib) {
		assertNotNull(contrib);
		assertNotNull(contrib.getId());
		assertTrue(contrib instanceof OutsideContributor);
		OutsideContributor contributor = (OutsideContributor) contrib;
		assertEquals("ODoe", contributor.getFamily());
		assertEquals("Jane", contributor.getGiven());
		assertEquals("http://orcid.org/1234-1234-1234-1234", contributor.getOrcid());
		assertNull(contributor.getEmail());
		assertEquals("7", contributor.getAffiliations().toArray(new OutsideAffiliation[1])[0].getId().toString());
	}

	public static void assertJillODoe(Contributor<?> contrib) {
		assertNotNull(contrib);
		assertNotNull(contrib.getId());
		assertTrue(contrib instanceof OutsideContributor);
		OutsideContributor contributor = (OutsideContributor) contrib;
		assertEquals("ODoe", contributor.getFamily());
		assertEquals("Jill", contributor.getGiven());
		assertEquals("http://orcid.org/1234-1234-1234-1234", contributor.getOrcid());
		assertNull(contributor.getEmail());
		assertEquals("7", contributor.getAffiliations().toArray(new OutsideAffiliation[1])[0].getId().toString());
	}

}
