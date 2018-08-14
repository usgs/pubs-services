package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.dao.ContributorDao;
import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.dao.CostCenterDaoIT;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LocalValidatorFactoryBean.class,
			Affiliation.class, AffiliationDao.class, CostCenter.class, CostCenterDao.class,
			PersonContributor.class, PersonContributorDao.class, ContributorDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
})
public class CostCenterBusServiceIT extends BaseIT {

	@Autowired
	public Validator validator;
	@Autowired
	CostCenterDao costCenterDao;

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
		CostCenter persisted = costCenterDao.getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persisted, CostCenterDaoIT.IGNORE_PROPERTIES, true, true);
	}

	@Test
	public void updateObjectTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("I'm a cost center in a test!");
		costCenter.setIpdsId(randomPositiveInt());
		busService.createObject(costCenter);
		assertNotNull(costCenter.getId());
		CostCenter persisted = costCenterDao.getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, persisted, CostCenterDaoIT.IGNORE_PROPERTIES, true, true);
		costCenter.setText("I'm an updated cost center in a test!");
		costCenter.setIpdsId(randomPositiveInt());
		busService.updateObject(costCenter);
		CostCenter updated = costCenterDao.getById(costCenter.getId());
		assertDaoTestResults(CostCenter.class, costCenter, updated, CostCenterDaoIT.IGNORE_PROPERTIES, true, true);
	}
	
	@Test
	public void deleteObjectTest() {
		CostCenter costCenter = new CostCenter();
		costCenter.setText("I'm a cost center in a test!");
		costCenter.setIpdsId(randomPositiveInt());
		busService.createObject(costCenter);
		assertNotNull(costCenter.getId());
		busService.deleteObject(costCenter.getId());
		assertNull(costCenterDao.getById(costCenter.getId()));
	}
}