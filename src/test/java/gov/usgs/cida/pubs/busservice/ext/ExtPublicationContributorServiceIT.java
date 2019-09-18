package gov.usgs.cida.pubs.busservice.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.busservice.CostCenterBusService;
import gov.usgs.cida.pubs.busservice.OutsideAffiliationBusService;
import gov.usgs.cida.pubs.busservice.PersonContributorBusService;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LocalValidatorFactoryBean.class, CostCenterBusService.class, OutsideAffiliationBusService.class,
			PersonContributorBusService.class, ExtPublicationContributorService.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/affiliation.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml")
})
public class ExtPublicationContributorServiceIT extends BaseIT {
	@Autowired
	public Validator validator;

	@MockBean
	public CostCenterBusService costCenterBusService;

	@MockBean
	public OutsideAffiliationBusService outsideAffiliationBusService;

	@MockBean
	public PersonContributorBusService personContributorBusService;

	@MockBean
	private ExtAffiliationBusService extAffiliationBusService;

	private ExtPublicationContributorService extPublicationContributorService;

	@Before
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		extPublicationContributorService = new ExtPublicationContributorService(extAffiliationBusService, personContributorBusService);
	}

	@Test
	public void getByName() {
		OutsideContributor contributor = extPublicationContributorService.getByName("ConFamily", "ConGiven");
		assertNull(contributor);

		contributor = extPublicationContributorService.getByName("outerfamily", "outergiven");
		assertNotNull(contributor);
		assertNotNull("Expected id to be set", contributor.getId());
		assertEquals(3, contributor.getId().intValue());
	}

	@Test
	public void getUsgsContributorByOrcid() {
		UsgsContributor contributor = extPublicationContributorService.getUsgsContributorByOrcid("0000-0000-0000-0001");
		assertNull(contributor);

		contributor = extPublicationContributorService.getUsgsContributorByOrcid("https://orcid.org/0000-0000-0000-0004");
		assertNotNull(contributor);
		assertNotNull("Expected id to be set", contributor.getId());
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
		assertNotNull("Expected id to be set", contributor.getId());
		assertEquals(3, contributor.getId().intValue());
		assertFalse(contributor.isPreferred());
		assertFalse(contributor.isUsgs());
		assertFalse(contributor.isCorporation());
		assertEquals("outerfamily", contributor.getFamily());
		assertEquals("outerGiven", contributor.getGiven());
		assertEquals("outer@gmail.com", contributor.getEmail());
	}

}