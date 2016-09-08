package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDaoTest;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;

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
public class OutsideAffiliationBusServiceTest extends BaseSpringTest {

	@Autowired
	public Validator validator;

	private OutsideAffiliationBusService busService;

	@Before
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
		assertDaoTestResults(OutsideAffiliation.class, outsideAffiliation, persisted, OutsideAffiliationDaoTest.IGNORE_PROPERTIES, true, true);
	}

	@Test
	public void updateObjectTest() {
		OutsideAffiliation outsideAffiliation = new OutsideAffiliation();
		outsideAffiliation.setText("I'm a Outside Affiliation in a test!");
		busService.createObject(outsideAffiliation);
		assertNotNull(outsideAffiliation.getId());
		OutsideAffiliation persisted = OutsideAffiliation.getDao().getById(outsideAffiliation.getId());
		assertDaoTestResults(OutsideAffiliation.class, outsideAffiliation, persisted, OutsideAffiliationDaoTest.IGNORE_PROPERTIES, true, true);
		outsideAffiliation.setText("I'm an updated Outside Affiliation in a test!");
		busService.updateObject(outsideAffiliation);
		OutsideAffiliation updated = OutsideAffiliation.getDao().getById(outsideAffiliation.getId());
		assertDaoTestResults(OutsideAffiliation.class, outsideAffiliation, updated, OutsideAffiliationDaoTest.IGNORE_PROPERTIES, true, true);
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