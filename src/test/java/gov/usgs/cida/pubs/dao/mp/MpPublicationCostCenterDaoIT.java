package gov.usgs.cida.pubs.dao.mp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.FullPubsDatabaseSetup;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationCostCenter;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, MpPublicationCostCenterDao.class, MpPublicationDao.class, PwPublicationDao.class})
@FullPubsDatabaseSetup
public class MpPublicationCostCenterDaoIT extends BaseIT {

	@Autowired
	MpPublicationCostCenterDao mpPublicationCostCenterDao;
	@Autowired
	MpPublicationDao mpPublicationDao;
	@Autowired
	PwPublicationDao pwPublicationDao;

	@Test
	public void addGetbyIdDeleteByIdTest() {
		Integer id = addMpPublicationCostCenter();
		assertNotNull(id);
		MpPublicationCostCenter mpcc = mpPublicationCostCenterDao.getById(id);
		assertNotNull(mpcc);
		assertEquals(id, mpcc.getId());
		assertEquals(1, mpcc.getPublicationId().intValue());
		assertEquals(3, mpcc.getCostCenter().getId().intValue());
		mpPublicationCostCenterDao.deleteById(id);
		assertNull(mpPublicationCostCenterDao.getById(id));
	}

	@Test
	public void getByMapAndDelete() {
		Integer id = addMpPublicationCostCenter();
		Map<String, Object> filters = new HashMap<>();
		filters.put("id", id);
		Collection<MpPublicationCostCenter> mpccs = mpPublicationCostCenterDao.getByMap(filters);
		assertNotNull(mpccs);
		assertEquals(1, mpccs.size());

		filters.clear();
		filters.put("publicationId", 1);
		mpccs = mpPublicationCostCenterDao.getByMap(filters);
		assertNotNull(mpccs);
		assertEquals(3, mpccs.size());

		filters.clear();
		filters.put("costCenterId", 3);
		mpccs = mpPublicationCostCenterDao.getByMap(filters);
		assertNotNull(mpccs);
		assertEquals(1, mpccs.size());

		filters.put("id", id);
		filters.put("publicationId", 1);
		mpccs = mpPublicationCostCenterDao.getByMap(filters);
		assertNotNull(mpccs);
		assertEquals(1, mpccs.size());

		mpPublicationCostCenterDao.delete(mpPublicationCostCenterDao.getById(id));
		assertNull(mpPublicationCostCenterDao.getById(id));

		mpPublicationCostCenterDao.deleteByParent(2);
		filters.put("publicationId", 1);
		mpccs = mpPublicationCostCenterDao.getByMap(filters);
		assertTrue(mpccs.isEmpty());
	}

	@Test
	public void updateTest() {
		MpPublicationCostCenter mpcc = mpPublicationCostCenterDao.getById(addMpPublicationCostCenter());
		CostCenter cc = new CostCenter();
		cc.setId(4);
		mpcc.setCostCenter(cc);
		//We don't update the publicationID...
		mpcc.setPublicationId(4);
		mpPublicationCostCenterDao.update(mpcc);
		MpPublicationCostCenter mpcc2 = mpPublicationCostCenterDao.getById(mpcc.getId());
		assertNotNull(mpcc);
		assertEquals(mpcc.getId(), mpcc2.getId());
		assertEquals(1, mpcc2.getPublicationId().intValue());
		assertEquals(4, mpcc2.getCostCenter().getId().intValue());
	}

	@Test
	public void copyFromPwTest() {
		mpPublicationDao.copyFromPw(4);
		mpPublicationCostCenterDao.copyFromPw(4);
		MpPublicationCostCenter mpcc = mpPublicationCostCenterDao.getById(10);
		assertNotNull(mpcc);
		assertEquals(10, mpcc.getId().intValue());
		assertEquals(4, mpcc.getPublicationId().intValue());
		assertEquals(2, mpcc.getCostCenter().getId().intValue());
	}

	@Test
	public void publishToPwTest() {
		mpPublicationCostCenterDao.publishToPw(null);
		mpPublicationCostCenterDao.publishToPw(-1);
		mpPublicationDao.publishToPw(1);
		
		//this one should be a straight add.
		mpPublicationCostCenterDao.publishToPw(1);
		PwPublication pub = pwPublicationDao.getById(1);
		assertEquals(2, pub.getCostCenters().size());
		for (PublicationCostCenter<?> costCenter : pub.getCostCenters()) {
			if (1 == costCenter.getId()) {
				assertPwCostCenter1(costCenter);
			} else if (2 == costCenter.getId()) {
				assertPwCostCenter2(costCenter);
			} else {
				fail("Got a bad contributor:" + costCenter.getId());
			}
		}
		
		//this one should be a merge.
		mpPublicationDao.copyFromPw(4);
		mpPublicationCostCenterDao.copyFromPw(4);
		mpPublicationDao.publishToPw(4);
		mpPublicationCostCenterDao.deleteById(10);
		MpPublicationCostCenter mpPCC = new MpPublicationCostCenter();
		CostCenter cc = new CostCenter();
		cc.setId(3);
		mpPCC.setCostCenter(cc);
		mpPCC.setPublicationId(4);
		mpPublicationCostCenterDao.add(mpPCC);
		mpPublicationCostCenterDao.publishToPw(4);
		pub = pwPublicationDao.getById(4);
		assertEquals(1, pub.getCostCenters().size());
		for (PublicationCostCenter<?> costCenter : pub.getCostCenters()) {
			assertPwCostCenterXX(mpPCC.getId(), costCenter);
		}
	}

	public static void assertPwCostCenter1(PublicationCostCenter<?> costCenter) {
		assertTrue(costCenter instanceof PwPublicationCostCenter);
		assertCostCenter1(costCenter);
	}

	public static void assertMpCostCenter1(PublicationCostCenter<?> costCenter) {
		assertTrue(costCenter instanceof MpPublicationCostCenter);
		assertCostCenter1(costCenter);
	}

	public static void assertCostCenter1(PublicationCostCenter<?> costCenter) {
		assertNotNull(costCenter);
		assertEquals(1, costCenter.getId().intValue());
		assertEquals(1, costCenter.getPublicationId().intValue());
		assertNotNull(costCenter.getCostCenter());
		assertEquals(1, costCenter.getCostCenter().getId().intValue());
	}
	
	public static void assertPwCostCenter2(PublicationCostCenter<?> costCenter) {
		assertTrue(costCenter instanceof PwPublicationCostCenter);
		assertCostCenter2(costCenter);
	}

	public static void assertMpCostCenter2(PublicationCostCenter<?> costCenter) {
		assertTrue(costCenter instanceof MpPublicationCostCenter);
		assertCostCenter2(costCenter);
	}

	public static void assertCostCenter2(PublicationCostCenter<?> costCenter) {
		assertNotNull(costCenter);
		assertEquals(2, costCenter.getId().intValue());
		assertEquals(1, costCenter.getPublicationId().intValue());
		assertNotNull(costCenter.getCostCenter());
		assertEquals(2, costCenter.getCostCenter().getId().intValue());
	}
	
	public static void assertPwCostCenterXX(Integer id, PublicationCostCenter<?> costCenter) {
		assertTrue(costCenter instanceof PwPublicationCostCenter);
		assertCostCenterXX(id, costCenter);
	}

	public static void assertMpCostCenterXX(Integer id, PublicationCostCenter<?> costCenter) {
		assertTrue(costCenter instanceof MpPublicationCostCenter);
		assertCostCenterXX(id, costCenter);
	}

	public static void assertCostCenterXX(Integer id, PublicationCostCenter<?> costCenter) {
		assertNotNull(costCenter);
		assertEquals(id, costCenter.getId());
		assertEquals(4, costCenter.getPublicationId().intValue());
		assertNotNull(costCenter.getCostCenter());
		assertEquals(3, costCenter.getCostCenter().getId().intValue());
	}
	
	public Integer addMpPublicationCostCenter() {
		MpPublicationCostCenter mpcc = new MpPublicationCostCenter();
		mpcc.setPublicationId(1);
		CostCenter cc = new CostCenter();
		cc.setId(3);
		mpcc.setCostCenter(cc);
		return mpPublicationCostCenterDao.add(mpcc);
	}

}
