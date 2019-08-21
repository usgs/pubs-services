package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.webservice.MvcService;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, ContributorDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/affiliation.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml")
})
public class ContributorDaoIT extends BaseIT {

	public static final int CONTRIBUTOR_CNT = 13;
	public static final int PERSON_CONTRIBUTOR_CNT = 12;
	public static final int CORPORATE_CONTRIBUTOR_CNT = 1;

	public static final List<String> IGNORE_PROPERTIES_PERSON = Arrays.asList("validationErrors", "valErrors", "organization", "affiliations");
	public static final List<String> IGNORE_PROPERTIES_CORPORATION = Arrays.asList("validationErrors", "valErrors", "family", 
			"given", "suffix", "email", "affiliation");

	@Autowired
	ContributorDao contributorDao;

	@Test
	public void getByIdInteger() {
		//USGS Contributor
		Contributor<?> contributor = contributorDao.getById(1);
		assertContributor1(contributor);

		//Non-USGS Contributor
		contributor = contributorDao.getById(3);
		assertContributor3(contributor);

		//Corporate Contributor
		contributor = contributorDao.getById(2);
		assertEquals(2, contributor.getId().intValue());
		assertTrue(contributor instanceof CorporateContributor);
		CorporateContributor corpContributor = (CorporateContributor) contributor;
		assertEquals("US Geological Survey Ice Survey Team", corpContributor.getOrganization());
	}

	@Test
	public void getByIdString() {
		//USGS Contributor
		Contributor<?> contributor = contributorDao.getById("1");
		assertContributor1(contributor);

		//Non-USGS Contributor
		contributor = contributorDao.getById("3");
		assertContributor3(contributor);

		//Corporate Contributor
		contributor = contributorDao.getById("2");
		assertEquals(2, contributor.getId().intValue());
		assertTrue(contributor instanceof CorporateContributor);
		CorporateContributor corpContributor = (CorporateContributor) contributor;
		assertEquals("US Geological Survey Ice Survey Team", corpContributor.getOrganization());
	}

	@Test
	public void getByMap() {
		List<Contributor<?>> contributors = contributorDao.getByMap(null);
		assertEquals(CONTRIBUTOR_CNT, contributors.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put(ContributorDao.ID_SEARCH, 1);
		contributors = contributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(1, contributors.get(0).getId().intValue());

		filters.clear();
		filters.put(ContributorDao.TEXT_SEARCH, "con" + MvcService.TEXT_SEARCH_STARTS_WITH_SUFFIX);
		contributors = contributorDao.getByMap(filters);
		assertEquals(3, contributors.size());
		boolean got1 = false;
		boolean got11 = false;
		boolean got21 = false;
		for (Contributor<?> contributor : contributors) {
			if (1 == contributor.getId()) {
				got1 = true;
			} else if (11 == contributor.getId()) {
				got11 = true;
			} else if (21 == contributor.getId()) {
				got21 = true;
			} else {
				fail("Got wrong contributor" + contributor.getId());
			}
		}
		assertTrue("Got 1", got1);
		assertTrue("Got 11", got11);
		assertTrue("Got 21", got21);

		filters.clear();
		filters.put(ContributorDao.TEXT_SEARCH, "us" + MvcService.TEXT_SEARCH_STARTS_WITH_SUFFIX + MvcService.TEXT_SEARCH_AND + "ge" + MvcService.TEXT_SEARCH_STARTS_WITH_SUFFIX);
		contributors = contributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(2, contributors.get(0).getId().intValue());

		filters.clear();
		filters.put(PersonContributorDao.GIVEN, "con");
		contributors = contributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(1, contributors.get(0).getId().intValue());

		filters.clear();
		filters.put(PersonContributorDao.FAMILY, "con");
		contributors = contributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(1, contributors.get(0).getId().intValue());
		filters.put("preferred", false);
		assertTrue(contributorDao.getByMap(filters).isEmpty());

		filters.clear();
		filters.put("corporation", false);
		contributors = contributorDao.getByMap(filters);
		assertEquals(PERSON_CONTRIBUTOR_CNT, contributors.size());
		assertEquals(51, contributors.get(0).getId().intValue());
		assertEquals(54, contributors.get(1).getId().intValue());
		assertEquals(11, contributors.get(2).getId().intValue());
		assertEquals(21, contributors.get(3).getId().intValue());
		assertEquals(14, contributors.get(4).getId().intValue());
		assertEquals(24, contributors.get(5).getId().intValue());
		assertEquals(34, contributors.get(6).getId().intValue());
		assertEquals(44, contributors.get(7).getId().intValue());
		assertEquals(4, contributors.get(8).getId().intValue());
		assertEquals(1, contributors.get(9).getId().intValue());
		assertEquals(5, contributors.get(10).getId().intValue());
		assertEquals(3, contributors.get(11).getId().intValue());

		filters.put(ContributorDao.TEXT_SEARCH, "oute" + MvcService.TEXT_SEARCH_STARTS_WITH_SUFFIX);
		contributors = contributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(3, contributors.get(0).getId().intValue());
		filters.put(PersonContributorDao.FAMILY, "out");
		contributors = contributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		filters.put(PersonContributorDao.GIVEN, "out");
		contributors = contributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		filters.put(ContributorDao.ID_SEARCH, 3);
		contributors = contributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		filters.put("preferred", true);
		assertTrue(contributorDao.getByMap(filters).isEmpty());

		filters.clear();
		filters.put(PersonContributorDao.ORCID, new String[]{"http://orcid.org/0000-0000-0000-0004"});
		contributors = contributorDao.getByMap(filters);
		assertEquals(5, contributors.size());
		assertEquals(14, contributors.get(0).getId().intValue());
		assertEquals(24, contributors.get(1).getId().intValue());
		assertEquals(34, contributors.get(2).getId().intValue());
		assertEquals(44, contributors.get(3).getId().intValue());
		assertEquals(4, contributors.get(4).getId().intValue());

		filters.clear();
		filters.put("corporation", true);
		contributors = contributorDao.getByMap(filters);
		assertEquals(CORPORATE_CONTRIBUTOR_CNT, contributors.size());
		assertEquals(2, contributors.get(0).getId().intValue());
		filters.put(ContributorDao.TEXT_SEARCH, "us:*");
		contributors = contributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(2, contributors.get(0).getId().intValue());
	}

	@Test
	public void getByMapPreferred() {
		Map<String, Object> filters = new HashMap<>();
		filters.put("preferred", true);
		List<Contributor<?>> contributors = contributorDao.getByMap(filters);
		assertEquals(5, contributors.size());
	}

	@Test
	public void notImplemented() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			contributorDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}
		try {
			contributorDao.add(new CorporateContributor());
			fail("Was able to add.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}
		try {
			contributorDao.update(new CorporateContributor());
			fail("Was able to update.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}
	}

	public static void assertContributor1(Contributor<?> contributor) {
		assertEquals(1, contributor.getId().intValue());
		assertTrue(contributor instanceof UsgsContributor);
		UsgsContributor usgsContributor = (UsgsContributor) contributor;
		assertEquals("ConFamily", usgsContributor.getFamily());
		assertEquals("ConGiven", usgsContributor.getGiven());
		assertEquals("ConSuffix", usgsContributor.getSuffix());
		assertEquals("con@usgs.gov", usgsContributor.getEmail());
		assertNull(usgsContributor.getOrcid());
		assertTrue(usgsContributor.isUsgs());
		assertFalse(usgsContributor.isCorporation());
		assertTrue(usgsContributor.isPreferred());
	}

	public static void assertContributor3(Contributor<?> contributor) {
		assertEquals(3, contributor.getId().intValue());
		assertTrue(contributor instanceof OutsideContributor);
		OutsideContributor outsideContributor = (OutsideContributor) contributor;
		assertEquals("outerfamily", outsideContributor.getFamily());
		assertEquals("outerGiven", outsideContributor.getGiven());
		assertEquals("outerSuffix", outsideContributor.getSuffix());
		assertEquals("outer@gmail.com", outsideContributor.getEmail());
		assertEquals("http://orcid.org/0000-0000-0000-0001", outsideContributor.getOrcid());
		assertFalse(outsideContributor.isUsgs());
		assertFalse(outsideContributor.isCorporation());
		assertFalse(outsideContributor.isPreferred());
	}

	public static void assertContributor4(Contributor<?> contributor) {
		assertEquals(4, contributor.getId().intValue());
		assertTrue(contributor instanceof UsgsContributor);
		UsgsContributor usgsContributor = (UsgsContributor) contributor;
		assertEquals("4Family", usgsContributor.getFamily());
		assertEquals("4Given", usgsContributor.getGiven());
		assertEquals("4Suffix", usgsContributor.getSuffix());
		assertEquals("con4@usgs.gov", usgsContributor.getEmail());
		assertEquals("http://orcid.org/0000-0000-0000-0004", usgsContributor.getOrcid());
		assertTrue(usgsContributor.isUsgs());
		assertFalse(usgsContributor.isCorporation());
		assertTrue(usgsContributor.isPreferred());
	}
}
