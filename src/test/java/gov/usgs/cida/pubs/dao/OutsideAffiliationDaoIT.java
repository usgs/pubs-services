package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, OutsideAffiliationDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class OutsideAffiliationDaoIT extends BaseIT {

	public static final int OUTSIDE_AFFILIATES_CNT = 3;

	public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors", "ipdsId");

	@Autowired
	OutsideAffiliationDao outsideAffiliationDao;

	@Test
	public void getByIdInteger() {
		OutsideAffiliation outsideAffiliation = outsideAffiliationDao.getById(5);
		AffiliationDaoIT.assertAffiliation5(outsideAffiliation);
	}

	@Test
	public void getByIdString() {
		OutsideAffiliation outsideAffiliation = outsideAffiliationDao.getById("5");
		AffiliationDaoIT.assertAffiliation5(outsideAffiliation);
	}

	@Test
	public void getByMap() {
		List<OutsideAffiliation> outsideAffiliations = outsideAffiliationDao.getByMap(null);
		assertEquals(OUTSIDE_AFFILIATES_CNT, outsideAffiliations.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put(OutsideAffiliationDao.ID_SEARCH, 5);
		outsideAffiliations = outsideAffiliationDao.getByMap(filters);
		assertEquals(1, outsideAffiliations.size());
		AffiliationDaoIT.assertAffiliation5(outsideAffiliations.get(0));

		filters.clear();
		filters.put(OutsideAffiliationDao.TEXT_SEARCH, "out");
		outsideAffiliations = outsideAffiliationDao.getByMap(filters);
		assertEquals(2, outsideAffiliations.size());

		filters.clear();
		filters.put(OutsideAffiliationDao.ACTIVE_SEARCH, false);
		outsideAffiliations = outsideAffiliationDao.getByMap(filters);
		assertEquals(1, outsideAffiliations.size());

		filters.clear();
		filters.put(OutsideAffiliationDao.ACTIVE_SEARCH, true);
		outsideAffiliations = outsideAffiliationDao.getByMap(filters);
		assertEquals(2, outsideAffiliations.size());

		filters.clear();
		filters.put(OutsideAffiliationDao.USGS_SEARCH, true);
		outsideAffiliations = outsideAffiliationDao.getByMap(filters);
		assertEquals(0, outsideAffiliations.size());

		filters.clear();
		filters.put(OutsideAffiliationDao.USGS_SEARCH, false);
		outsideAffiliations = outsideAffiliationDao.getByMap(filters);
		assertEquals(3, outsideAffiliations.size());

		filters.put(OutsideAffiliationDao.ID_SEARCH, 5);
		filters.put(OutsideAffiliationDao.TEXT_SEARCH, "out");
		filters.put(OutsideAffiliationDao.ACTIVE_SEARCH, true);
		outsideAffiliations = outsideAffiliationDao.getByMap(filters);
		assertEquals(1, outsideAffiliations.size());
	}

	@Test
	public void addUpdateTest() {
		OutsideAffiliation affiliation = new OutsideAffiliation();
		affiliation.setText("outside org 1");
		outsideAffiliationDao.add(affiliation);
		OutsideAffiliation persistedAffiliation = outsideAffiliationDao.getById(affiliation.getId());
		assertDaoTestResults(OutsideAffiliation.class, affiliation, persistedAffiliation, IGNORE_PROPERTIES, true, true);

		affiliation.setText("outside org 2");
		outsideAffiliationDao.update(affiliation);
		persistedAffiliation = outsideAffiliationDao.getById(affiliation.getId());
		assertDaoTestResults(OutsideAffiliation.class, affiliation, persistedAffiliation, IGNORE_PROPERTIES, true, true);
	}

	@Test
	public void deleteTest() {
		OutsideAffiliation affiliation = new OutsideAffiliation();
		affiliation.setText("outside org 1");
		outsideAffiliationDao.add(affiliation);
		OutsideAffiliation persistedAffiliation = outsideAffiliationDao.getById(affiliation.getId());
		assertDaoTestResults(OutsideAffiliation.class, affiliation, persistedAffiliation, IGNORE_PROPERTIES, true, true);

		outsideAffiliationDao.delete(affiliation);
		assertNull(outsideAffiliationDao.getById(affiliation.getId()));
	}

	@Test
	public void deleteByIdTest() {
		OutsideAffiliation affiliation = new OutsideAffiliation();
		affiliation.setText("outside org 1");
		outsideAffiliationDao.add(affiliation);
		OutsideAffiliation persistedAffiliation = outsideAffiliationDao.getById(affiliation.getId());
		assertDaoTestResults(OutsideAffiliation.class, affiliation, persistedAffiliation, IGNORE_PROPERTIES, true, true);

		outsideAffiliationDao.deleteById(affiliation.getId());
		assertNull(outsideAffiliationDao.getById(affiliation.getId()));
	}

	@Test
	public void notImplemented() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			outsideAffiliationDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
		}
	}
}
