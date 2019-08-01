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
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, CostCenterDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class CostCenterDaoIT extends BaseIT {

	public static final int COST_CENTER_CNT = 5;

	public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors");

	@Autowired
	CostCenterDao costCenterDao;

	@Test
	public void getByIdInteger() {
		CostCenter costCenter = costCenterDao.getById(1);
		AffiliationDaoIT.assertAffiliation1(costCenter);
	}

	@Test
	public void getByIdString() {
		CostCenter costCenter = costCenterDao.getById("1");
		AffiliationDaoIT.assertAffiliation1(costCenter);
	}

	@Test
	public void getByMap() {
		List<CostCenter> costCenters = costCenterDao.getByMap(null);
		assertEquals(COST_CENTER_CNT, costCenters.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put(CostCenterDao.ID_SEARCH, 1);
		costCenters = costCenterDao.getByMap(filters);
		assertEquals(1, costCenters.size());
		AffiliationDaoIT.assertAffiliation1(costCenters.get(0));

		filters.clear();
		filters.put(CostCenterDao.TEXT_SEARCH, "affil");
		costCenters = costCenterDao.getByMap(filters);
		assertEquals(2, costCenters.size());

		filters.clear();
		filters.put(CostCenterDao.ACTIVE_SEARCH, false);
		costCenters = costCenterDao.getByMap(filters);
		assertEquals(1, costCenters.size());

		filters.clear();
		filters.put(CostCenterDao.ACTIVE_SEARCH, true);
		costCenters = costCenterDao.getByMap(filters);
		assertEquals(4, costCenters.size());

		filters.clear();
		filters.put(CostCenterDao.USGS_SEARCH, false);
		costCenters = costCenterDao.getByMap(filters);
		assertEquals(0, costCenters.size());

		filters.clear();
		filters.put(CostCenterDao.USGS_SEARCH, true);
		costCenters = costCenterDao.getByMap(filters);
		assertEquals(5, costCenters.size());

		filters.put(CostCenterDao.ID_SEARCH, 1);
		filters.put(CostCenterDao.TEXT_SEARCH, "affil");
		filters.put(CostCenterDao.ACTIVE_SEARCH, true);
		filters.put(PublicationDao.IPDS_ID, 4);
		costCenters = costCenterDao.getByMap(filters);
		assertEquals(1, costCenters.size());
	}

	@Test
	public void addUpdateTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("cost center 1");
		costCenter.setIpdsId(randomPositiveInt());
		costCenterDao.add(costCenter);
		CostCenter persistedAffiliation = (CostCenter) costCenterDao.getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persistedAffiliation, IGNORE_PROPERTIES, true, true);

		costCenter.setText("cost center 2");
		costCenter.setIpdsId(randomPositiveInt()+4);
		costCenterDao.update(costCenter);
		persistedAffiliation = (CostCenter) costCenterDao.getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persistedAffiliation, IGNORE_PROPERTIES, true, true);
	}

	@Test
	public void deleteTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("outside org 1");
		costCenter.setIpdsId(randomPositiveInt());
		costCenterDao.add(costCenter);
		CostCenter persistedAffiliation = costCenterDao.getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persistedAffiliation, IGNORE_PROPERTIES, true, true);

		costCenterDao.delete(costCenter);
		assertNull(costCenterDao.getById(costCenter.getId()));
	}

	@Test
	public void deleteByIdTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("outside org 1");
		costCenter.setIpdsId(randomPositiveInt());
		costCenterDao.add(costCenter);
		CostCenter persistedAffiliation = costCenterDao.getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persistedAffiliation, IGNORE_PROPERTIES, true, true);

		costCenterDao.deleteById(costCenter.getId());
		assertNull(costCenterDao.getById(costCenter.getId()));
	}

	@Test
	public void notImplemented() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			costCenterDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}
	}
}