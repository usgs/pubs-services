package gov.usgs.cida.pubs.busservice.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.busservice.PersonContributorBusService;
import gov.usgs.cida.pubs.dao.ContributorDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, OutsideContributor.class, PersonContributorDao.class, ContributorDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/affiliation.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml")
})
public class ExtPublicationContributorServiceIT extends BaseIT {

	@MockBean
	public PersonContributorBusService personContributorBusService;

	@MockBean
	private ExtAffiliationBusService extAffiliationBusService;

	private ExtPublicationContributorService extPublicationContributorService;

	@BeforeEach
	public void initTest() throws Exception {
		extPublicationContributorService = new ExtPublicationContributorService(extAffiliationBusService, personContributorBusService);
	}

	@Test
	public void getByName() {
		OutsideContributor contributor = extPublicationContributorService.getByName("ConFamily", "ConGiven");
		assertNull(contributor);

		contributor = extPublicationContributorService.getByName("outerfamily", "outergiven");
		assertNotNull(contributor);
		assertNotNull(contributor.getId(), "Expected id to be set");
		assertEquals(3, contributor.getId().intValue());
	}

	@Test
	public void getUsgsContributorByOrcid() {
		UsgsContributor contributor = extPublicationContributorService.getUsgsContributorByOrcid("0000-0000-0000-0001");
		assertNull(contributor);

		contributor = extPublicationContributorService.getUsgsContributorByOrcid("https://orcid.org/0000-0000-0000-0004");
		assertNotNull(contributor);
		assertNotNull(contributor.getId(), "Expected id to be set");
		assertEquals(4, contributor.getId().intValue());
		assertTrue(contributor.isPreferred());
		assertTrue(contributor.isUsgs());
		assertFalse(contributor.isCorporation());
		assertEquals("4Family", contributor.getFamily());
		assertEquals("4Given", contributor.getGiven());
		assertEquals("family4@usgs.gov", contributor.getEmail());
	}

	@Test
	public void getOutsideContributorByOrcid() {
		OutsideContributor contributor = extPublicationContributorService.getOutsideContributorByOrcid("0000-0000-0000-0004");
		assertNull(contributor);

		contributor = extPublicationContributorService.getOutsideContributorByOrcid("0000-0000-0000-0001");
		assertNotNull(contributor);
		assertNotNull(contributor.getId(), "Expected id to be set");
		assertEquals(3, contributor.getId().intValue());
		assertFalse(contributor.isPreferred());
		assertFalse(contributor.isUsgs());
		assertFalse(contributor.isCorporation());
		assertEquals("outerfamily", contributor.getFamily());
		assertEquals("outerGiven", contributor.getGiven());
		assertEquals("outer@gmail.com", contributor.getEmail());
	}

}