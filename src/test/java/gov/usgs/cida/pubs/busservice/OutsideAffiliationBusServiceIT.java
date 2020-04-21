package gov.usgs.cida.pubs.busservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDao;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDaoIT;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LocalValidatorFactoryBean.class,
			OutsideAffiliation.class, OutsideAffiliationDao.class, AffiliationDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
})
public class OutsideAffiliationBusServiceIT extends BaseIT {

	@Autowired
	public Validator validator;

	private OutsideAffiliationBusService busService;

	@BeforeEach
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		busService = new OutsideAffiliationBusService(validator);
	}

	@Test
	public void createObjectTest() {
		OutsideAffiliation outsideAffiliation = new OutsideAffiliation();
		outsideAffiliation.setText("I'm a Outside Affiliation in a test!");
		busService.createObject(outsideAffiliation);
		assertNotNull(outsideAffiliation.getId());
		OutsideAffiliation persisted = OutsideAffiliation.getDao().getById(outsideAffiliation.getId());
		assertDaoTestResults(OutsideAffiliation.class, outsideAffiliation, persisted, OutsideAffiliationDaoIT.IGNORE_PROPERTIES, true, true);
	}

	@Test
	public void updateObjectTest() {
		OutsideAffiliation outsideAffiliation = new OutsideAffiliation();
		outsideAffiliation.setText("I'm a Outside Affiliation in a test!");
		busService.createObject(outsideAffiliation);
		assertNotNull(outsideAffiliation.getId());
		OutsideAffiliation persisted = OutsideAffiliation.getDao().getById(outsideAffiliation.getId());
		assertDaoTestResults(OutsideAffiliation.class, outsideAffiliation, persisted, OutsideAffiliationDaoIT.IGNORE_PROPERTIES, true, true);
		outsideAffiliation.setText("I'm an updated Outside Affiliation in a test!");
		busService.updateObject(outsideAffiliation);
		OutsideAffiliation updated = OutsideAffiliation.getDao().getById(outsideAffiliation.getId());
		assertDaoTestResults(OutsideAffiliation.class, outsideAffiliation, updated, OutsideAffiliationDaoIT.IGNORE_PROPERTIES, true, true);
	}
	
	@Test
	public void deleteObjectTest() {
		OutsideAffiliation outsideAffiliation = new OutsideAffiliation();
		outsideAffiliation.setText("I'm a Outside Affiliation in a test!");
		busService.createObject(outsideAffiliation);
		assertNotNull(outsideAffiliation.getId());
		busService.deleteObject(outsideAffiliation.getId());
		assertNull(OutsideAffiliation.getDao().getById(outsideAffiliation.getId()));
	}
}