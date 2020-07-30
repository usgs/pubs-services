package gov.usgs.cida.pubs.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.webservice.MvcService;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, CorporateContributorDao.class})
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
@DatabaseSetup("classpath:/testData/contributor/")
public class CorporateContributorDaoIT extends BaseIT {

	@Autowired
	CorporateContributorDao corporateContributorDao;

	@Test
	public void getByIdInteger() {
		Contributor<?> contributor = corporateContributorDao.getById(2);
		assertEquals(2, contributor.getId().intValue());
		assertTrue(contributor instanceof CorporateContributor);
		CorporateContributor corpContributor = (CorporateContributor) contributor;
		assertEquals("US Geological Survey Ice Survey Team", corpContributor.getOrganization());
	}

	@Test
	public void getByIdString() {
		Contributor<?> contributor = corporateContributorDao.getById("2");
		assertEquals(2, contributor.getId().intValue());
		assertTrue(contributor instanceof CorporateContributor);
		CorporateContributor corpContributor = (CorporateContributor) contributor;
		assertEquals("US Geological Survey Ice Survey Team", corpContributor.getOrganization());
	}

	@Test
	public void getByMap() {
		List<Contributor<?>> contributors = corporateContributorDao.getByMap(null);
		assertEquals(ContributorDaoIT.CORPORATE_CONTRIBUTOR_CNT, contributors.size());
		assertAllAreCorporateContributors(contributors);

		Map<String, Object> filters = new HashMap<>();
		filters.put(CorporateContributorDao.ID_SEARCH, 2);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(2, contributors.get(0).getId().intValue());
		assertCorporateContributor(contributors.get(0));

		filters.clear();
		filters.put(CorporateContributorDao.ID_SEARCH, 60);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(60, contributors.get(0).getId().intValue());
		assertCorporateContributor(contributors.get(0));

		filters.clear();
		filters.put(CorporateContributorDao.TEXT_SEARCH, "us" + MvcService.TEXT_SEARCH_STARTS_WITH_SUFFIX);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(2, contributors.get(0).getId().intValue());
		assertCorporateContributor(contributors.get(0));

		filters.clear();
		filters.put("id", 2);
		filters.put("text", "us" + MvcService.TEXT_SEARCH_STARTS_WITH_SUFFIX);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(2, contributors.get(0).getId().intValue());
		assertCorporateContributor(contributors.get(0));
	}

	@Test
	public void getByMapContributorFilterTest() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(PersonContributorDao.PREFERRED, Boolean.TRUE);
		List<Contributor<?>> contributors = corporateContributorDao.getByMap(null);
		assertEquals(2, contributors.size());
		assertAllAreCorporateContributors(contributors);

		filters.clear();
		filters.put(PersonContributorDao.FAMILY, new String[]{"con"});
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(0, contributors.size());

		filters.clear();
		filters.put(PersonContributorDao.GIVEN, new String[]{"Given"});
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(0, contributors.size());

		filters.clear();
		filters.put(CorporateContributorDao.CORPORATION, Boolean.TRUE);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(ContributorDaoIT.CORPORATE_CONTRIBUTOR_CNT, contributors.size());
		assertAllAreCorporateContributors(contributors);

		filters.clear();
		filters.put(CorporateContributorDao.CORPORATION, Boolean.FALSE);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(0, contributors.size());

		filters.clear();
		filters.put(PersonContributorDao.USGS, Boolean.TRUE);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(0, contributors.size());

		filters.clear();
		filters.put(PersonContributorDao.USGS, Boolean.FALSE);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(ContributorDaoIT.CORPORATE_CONTRIBUTOR_CNT, contributors.size());
		assertAllAreCorporateContributors(contributors);

		filters.clear();
		filters.put(PersonContributorDao.EMAIL, new String[]{"test@corp.none.com"});
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(0, contributors.size());
	}

	@Test
	public void addUpdateDeleteTest() {
		CorporateContributor corporation = new CorporateContributor();
		corporation.setOrganization("organization");
		corporateContributorDao.add(corporation);
		CorporateContributor persistedCorp = (CorporateContributor) corporateContributorDao.getById(corporation.getId());
		assertDaoTestResults(CorporateContributor.class, corporation, persistedCorp, ContributorDaoIT.IGNORE_PROPERTIES_CORPORATION, true, true);

		corporation.setOrganization("organization2");
		corporateContributorDao.update(corporation);
		persistedCorp = (CorporateContributor) corporateContributorDao.getById(corporation.getId());
		assertDaoTestResults(CorporateContributor.class, corporation, persistedCorp, ContributorDaoIT.IGNORE_PROPERTIES_CORPORATION, true, true);

		corporateContributorDao.delete(corporation);
		assertNull(corporateContributorDao.getById(corporation.getId()));
	}

	@Test
	public void notImplemented() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			corporateContributorDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}
	}

	public static CorporateContributor buildACorp(final Integer corpId) {
		CorporateContributor newCorp = new CorporateContributor();
		newCorp.setId(corpId);
		return newCorp;
	}

	private void assertAllAreCorporateContributors(List<Contributor<?>> contributors) {
		for(Contributor<?> contributor : contributors) {
			assertCorporateContributor(contributor);
		}
	}

	private void assertCorporateContributor(Contributor<?> contributor) {
		assertTrue(contributor instanceof CorporateContributor);
		assertTrue(contributor.isCorporation());
		assertFalse(contributor.isUsgs());
	}
}
