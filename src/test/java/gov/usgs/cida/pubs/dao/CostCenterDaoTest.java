package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.CostCenter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class CostCenterDaoTest extends BaseSpringTest {

	public static final int COST_CENTER_CNT = 5;

	public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors");

	@Test
	public void getByIdInteger() {
		CostCenter costCenter = CostCenter.getDao().getById(1);
		AffiliationDaoTest.assertAffiliation1(costCenter);
	}

	@Test
	public void getByIdString() {
		CostCenter costCenter = CostCenter.getDao().getById("1");
		AffiliationDaoTest.assertAffiliation1(costCenter);
	}

	@Test
	public void getByMap() {
		List<CostCenter> costCenters = CostCenter.getDao().getByMap(null);
		assertEquals(COST_CENTER_CNT, costCenters.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put(CostCenterDao.ID_SEARCH, 1);
		costCenters = CostCenter.getDao().getByMap(filters);
		assertEquals(1, costCenters.size());
		AffiliationDaoTest.assertAffiliation1(costCenters.get(0));

		filters.clear();
		filters.put(CostCenterDao.TEXT_SEARCH, "affil");
		costCenters = CostCenter.getDao().getByMap(filters);
		assertEquals(2, costCenters.size());

		filters.clear();
		filters.put(CostCenterDao.ACTIVE_SEARCH, false);
		costCenters = CostCenter.getDao().getByMap(filters);
		assertEquals(1, costCenters.size());

		filters.clear();
		filters.put(CostCenterDao.ACTIVE_SEARCH, true);
		costCenters = CostCenter.getDao().getByMap(filters);
		assertEquals(4, costCenters.size());

		filters.clear();
		filters.put(CostCenterDao.USGS_SEARCH, false);
		costCenters = CostCenter.getDao().getByMap(filters);
		assertEquals(0, costCenters.size());

		filters.clear();
		filters.put(CostCenterDao.USGS_SEARCH, true);
		costCenters = CostCenter.getDao().getByMap(filters);
		assertEquals(5, costCenters.size());

		filters.put(CostCenterDao.ID_SEARCH, 1);
		filters.put(CostCenterDao.TEXT_SEARCH, "affil");
		filters.put(CostCenterDao.ACTIVE_SEARCH, true);
		filters.put(PublicationDao.IPDS_ID, 4);
		costCenters = CostCenter.getDao().getByMap(filters);
		assertEquals(1, costCenters.size());
	}

	@Test
	public void addUpdateTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("cost center 1");
		costCenter.setIpdsId(randomPositiveInt());
		CostCenter.getDao().add(costCenter);
		CostCenter persistedAffiliation = (CostCenter) CostCenter.getDao().getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persistedAffiliation, IGNORE_PROPERTIES, true, true);

		costCenter.setText("cost center 2");
		costCenter.setIpdsId(randomPositiveInt()+4);
		CostCenter.getDao().update(costCenter);
		persistedAffiliation = (CostCenter) CostCenter.getDao().getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persistedAffiliation, IGNORE_PROPERTIES, true, true);
	}

	@Test
	public void deleteTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("outside org 1");
		costCenter.setIpdsId(randomPositiveInt());
		CostCenter.getDao().add(costCenter);
		CostCenter persistedAffiliation = CostCenter.getDao().getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persistedAffiliation, IGNORE_PROPERTIES, true, true);

		CostCenter.getDao().delete(costCenter);
		assertNull(CostCenter.getDao().getById(costCenter.getId()));
	}

	@Test
	public void deleteByIdTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("outside org 1");
		costCenter.setIpdsId(randomPositiveInt());
		CostCenter.getDao().add(costCenter);
		CostCenter persistedAffiliation = CostCenter.getDao().getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persistedAffiliation, IGNORE_PROPERTIES, true, true);

		CostCenter.getDao().deleteById(costCenter.getId());
		assertNull(CostCenter.getDao().getById(costCenter.getId()));
	}

	@Test
	public void notImplemented() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			CostCenter.getDao().getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
		}
	}
}