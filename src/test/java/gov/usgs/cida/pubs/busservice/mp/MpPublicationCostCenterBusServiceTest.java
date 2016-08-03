package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
public class MpPublicationCostCenterBusServiceTest extends BaseSpringTest {

	@Autowired
	public Validator validator;

	private MpPublicationCostCenterBusService busService;

	@Before
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		busService = new MpPublicationCostCenterBusService(validator);
	}

	@Test
	public void mergeAndDeleteTest() {
		MpPublication mpPub = MpPublication.getDao().getById(2);
		Integer id = MpPublication.getDao().getNewProdId();
		mpPub.setId(id);
		mpPub.setIndexId(String.valueOf(id));
		mpPub.setIpdsId("ipds_" + id);
		MpPublication.getDao().add(mpPub);

		//update with no cost centers either side
		busService.merge(id, null);
		Map<String, Object> filters = new HashMap<>();
		filters.put("publicationId", id);
		assertEquals(0, MpPublicationCostCenter.getDao().getByMap(filters).size());
		busService.merge(id, new ArrayList<PublicationCostCenter<?>>());
		assertEquals(0, MpPublicationCostCenter.getDao().getByMap(filters).size());

		//Add some cost centers
		Collection<PublicationCostCenter<?>> mpccs = new ArrayList<>();
		CostCenter cc1 = (CostCenter) CostCenter.getDao().getById(1);
		MpPublicationCostCenter mpcc1 = new MpPublicationCostCenter();
		mpcc1.setCostCenter(cc1);
		mpccs.add(mpcc1);
		CostCenter cc2 = (CostCenter) CostCenter.getDao().getById(2);
		MpPublicationCostCenter mpcc2 = new MpPublicationCostCenter();
		mpcc2.setCostCenter(cc2);
		mpccs.add(mpcc2);
		busService.merge(id, mpccs);
		Collection<MpPublicationCostCenter> addedccs = MpPublicationCostCenter.getDao().getByMap(filters);
		assertEquals(2, addedccs.size());
		boolean gotOne = false;
		boolean gotTwo = false;
		for (MpPublicationCostCenter ccs : addedccs) {
			assertEquals(id, ccs.getPublicationId());
			if (1 == ccs.getCostCenter().getId()) {
				gotOne = true;
			} else if (2 == ccs.getCostCenter().getId()) {
				gotTwo = true;
			}
		}
		assertTrue(gotOne);
		assertTrue(gotTwo);

		//Now add one, take one away (and leave one alone).
		mpccs = new ArrayList<>();
		mpccs.add(mpcc2);
		CostCenter cc3= (CostCenter) CostCenter.getDao().getById(3);
		MpPublicationCostCenter mpcc3 = new MpPublicationCostCenter();
		mpcc3.setCostCenter(cc3);
		mpccs.add(mpcc3);
		busService.merge(id, mpccs);
		Collection<MpPublicationCostCenter> updccs = MpPublicationCostCenter.getDao().getByMap(filters);
		assertEquals(2, updccs.size());
		gotOne = false;
		gotTwo = false;
		boolean gotThree = false;
		for (MpPublicationCostCenter ccs : updccs) {
			assertEquals(id, ccs.getPublicationId());
			if (1 == ccs.getCostCenter().getId()) {
				gotOne = true;
			} else if (2 == ccs.getCostCenter().getId()) {
				gotTwo = true;
			} else if (3 == ccs.getCostCenter().getId()) {
				gotThree = true;
			}
		}
		assertFalse(gotOne);
		assertTrue(gotTwo);
		assertTrue(gotThree);

		//Now do a straight delete without the id.
		MpPublicationCostCenter mpcc = new MpPublicationCostCenter();
		mpcc.setCostCenter(cc3);
		mpcc.setPublicationId(id);
		busService.deleteObject(mpcc);
		updccs = MpPublicationCostCenter.getDao().getByMap(filters);
		assertEquals(1, updccs.size());
		gotOne = false;
		gotTwo = false;
		gotThree = false;
		for (MpPublicationCostCenter ccs : updccs) {
			assertEquals(id, ccs.getPublicationId());
			if (1 == ccs.getCostCenter().getId()) {
				gotOne = true;
			} else if (2 == ccs.getCostCenter().getId()) {
				gotTwo = true;
			} else if (3 == ccs.getCostCenter().getId()) {
				gotThree = true;
			}
		}
		assertFalse(gotOne);
		assertTrue(gotTwo);
		assertFalse(gotThree);
	}

}
