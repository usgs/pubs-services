package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertNotNull;
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
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
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
	public void addObjectTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("I'm a cost center in a test!");
		costCenter.setIpdsId(15);
		busService.createObject(costCenter);
		assertNotNull(costCenter.getId());
		CostCenter persisted = (CostCenter) CostCenter.getDao().getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persisted, CostCenterDaoTest.IGNORE_PROPERTIES, true, true);
	}
}