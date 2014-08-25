package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MpPublicationCostCenterDaoTest extends BaseSpringDaoTest {


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

    public Integer addMpPublicationCostCenter() {
        MpPublicationCostCenter mpcc = new MpPublicationCostCenter();
        mpcc.setPublicationId(1);
        CostCenter cc = new CostCenter();
        cc.setId(3);
        mpcc.setCostCenter(cc);
        return MpPublicationCostCenter.getDao().add(mpcc);
    }

}
