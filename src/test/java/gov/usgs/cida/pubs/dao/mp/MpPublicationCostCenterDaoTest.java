package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationCostCenter;

import java.util.Collection;
import java.util.HashMap;
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
public class MpPublicationCostCenterDaoTest extends BaseSpringTest {


    @Test
    public void addGetbyIdDeleteByIdTest() {
        Integer id = addMpPublicationCostCenter();
        assertNotNull(id);
        MpPublicationCostCenter mpcc = MpPublicationCostCenter.getDao().getById(id);
        assertNotNull(mpcc);
        assertEquals(id, mpcc.getId());
        assertEquals(1, mpcc.getPublicationId().intValue());
        assertEquals(3, mpcc.getCostCenter().getId().intValue());
        MpPublicationCostCenter.getDao().deleteById(id);
        assertNull(MpPublicationCostCenter.getDao().getById(id));
    }

    @Test
    public void getByMapAndDelete() {
        Integer id = addMpPublicationCostCenter();
        Map<String, Object> filters = new HashMap<>();
        filters.put("id", id);
        Collection<MpPublicationCostCenter> mpccs = MpPublicationCostCenter.getDao().getByMap(filters);
        assertNotNull(mpccs);
        assertEquals(1, mpccs.size());

        filters.clear();
        filters.put("publicationId", 1);
        mpccs = MpPublicationCostCenter.getDao().getByMap(filters);
        assertNotNull(mpccs);
        assertEquals(3, mpccs.size());

        filters.clear();
        filters.put("costCenterId", 3);
        mpccs = MpPublicationCostCenter.getDao().getByMap(filters);
        assertNotNull(mpccs);
        assertEquals(1, mpccs.size());

        filters.put("id", id);
        filters.put("publicationId", 1);
        mpccs = MpPublicationCostCenter.getDao().getByMap(filters);
        assertNotNull(mpccs);
        assertEquals(1, mpccs.size());

        MpPublicationCostCenter.getDao().delete(MpPublicationCostCenter.getDao().getById(id));
        assertNull(MpPublicationCostCenter.getDao().getById(id));

        MpPublicationCostCenter.getDao().deleteByParent(2);
		filters.put("publicationId", 1);
		mpccs = MpPublicationCostCenter.getDao().getByMap(filters);
		assertTrue(mpccs.isEmpty());
    }

    @Test
    public void updateTest() {
        MpPublicationCostCenter mpcc = MpPublicationCostCenter.getDao().getById(addMpPublicationCostCenter());
        CostCenter cc = new CostCenter();
        cc.setId(4);
        mpcc.setCostCenter(cc);
        //We don't update the publicationID...
        mpcc.setPublicationId(4);
        MpPublicationCostCenter.getDao().update(mpcc);
        MpPublicationCostCenter mpcc2 = MpPublicationCostCenter.getDao().getById(mpcc.getId());
        assertNotNull(mpcc);
        assertEquals(mpcc.getId(), mpcc2.getId());
        assertEquals(1, mpcc2.getPublicationId().intValue());
        assertEquals(4, mpcc2.getCostCenter().getId().intValue());
    }

    @Test
    public void copyFromPwTest() {
        MpPublication.getDao().copyFromPw(4);
        MpPublicationCostCenter.getDao().copyFromPw(4);
        MpPublicationCostCenter mpcc = MpPublicationCostCenter.getDao().getById(10);
        assertNotNull(mpcc);
        assertEquals(10, mpcc.getId().intValue());
        assertEquals(4, mpcc.getPublicationId().intValue());
        assertEquals(2, mpcc.getCostCenter().getId().intValue());
    }

    @Test
    public void publishToPwTest() {
    	MpPublicationCostCenter.getDao().publishToPw(null);
    	MpPublicationCostCenter.getDao().publishToPw(-1);
    	MpPublication.getDao().publishToPw(1);
    	
    	//this one should be a straight add.
    	MpPublicationCostCenter.getDao().publishToPw(1);
    	PwPublication pub = PwPublication.getDao().getById(1);
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
    	MpPublication.getDao().copyFromPw(4);
    	MpPublicationCostCenter.getDao().copyFromPw(4);
    	MpPublication.getDao().publishToPw(4);
    	MpPublicationCostCenter.getDao().deleteById(10);
    	MpPublicationCostCenter mpPCC = new MpPublicationCostCenter();
    	CostCenter cc = new CostCenter();
    	cc.setId(3);
    	mpPCC.setCostCenter(cc);
    	mpPCC.setPublicationId(4);
    	MpPublicationCostCenter.getDao().add(mpPCC);
    	MpPublicationCostCenter.getDao().publishToPw(4);
    	pub = PwPublication.getDao().getById(4);
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
        return MpPublicationCostCenter.getDao().add(mpcc);
    }

}
