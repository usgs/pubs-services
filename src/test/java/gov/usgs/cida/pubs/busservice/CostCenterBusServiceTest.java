package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.dao.CostCenterDaoTest;
import gov.usgs.cida.pubs.domain.CostCenter;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
})
public class CostCenterBusServiceTest extends BaseSpringTest {

	@Autowired
	public Validator validator;

	private CostCenterBusService busService;

	@Before
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		busService = new CostCenterBusService(validator);
	}

	@Test
	public void createObjectTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("I'm a cost center in a test!");
		costCenter.setIpdsId(randomPositiveInt());
		busService.createObject(costCenter);
		assertNotNull(costCenter.getId());
		CostCenter persisted = CostCenter.getDao().getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persisted, CostCenterDaoTest.IGNORE_PROPERTIES, true, true);
	}

	@Test
	public void updateObjectTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("I'm a cost center in a test!");
		costCenter.setIpdsId(randomPositiveInt());
		busService.createObject(costCenter);
		assertNotNull(costCenter.getId());
		CostCenter persisted = CostCenter.getDao().getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persisted, CostCenterDaoTest.IGNORE_PROPERTIES, true, true);
		costCenter.setText("I'm an updated cost center in a test!");
		costCenter.setIpdsId(randomPositiveInt());
		busService.updateObject(costCenter);
		CostCenter updated = CostCenter.getDao().getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, updated, CostCenterDaoTest.IGNORE_PROPERTIES, true, true);
	}
	
	@Test
	public void deleteObjectTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("I'm a cost center in a test!");
		costCenter.setIpdsId(randomPositiveInt());
		busService.createObject(costCenter);
		assertNotNull(costCenter.getId());
		busService.deleteObject(costCenter.getId());
		assertNull(CostCenter.getDao().getById(costCenter.getId()));
	}
}