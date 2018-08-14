package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.webservice.MvcService;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, CorporateContributorDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/affiliation.xml"),
	@DatabaseSetup("classpath:/testData/contributor.xml")
})
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

		Map<String, Object> filters = new HashMap<>();
		filters.put(CorporateContributorDao.ID_SEARCH, 2);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(2, contributors.get(0).getId().intValue());

		filters.clear();
		filters.put(CorporateContributorDao.TEXT_SEARCH, "us" + MvcService.TEXT_SEARCH_STARTS_WITH_SUFFIX);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(2, contributors.get(0).getId().intValue());

		filters.clear();
		filters.put("id", 2);
		filters.put("text", "us" + MvcService.TEXT_SEARCH_STARTS_WITH_SUFFIX);
		contributors = corporateContributorDao.getByMap(filters);
		assertEquals(1, contributors.size());
		assertEquals(2, contributors.get(0).getId().intValue());
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
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
		}
	}

	public static CorporateContributor buildACorp(final Integer corpId) {
		CorporateContributor newCorp = new CorporateContributor();
		newCorp.setId(corpId);
		return newCorp;
	}
}
