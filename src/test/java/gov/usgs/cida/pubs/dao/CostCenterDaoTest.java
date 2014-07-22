package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.CostCenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class CostCenterDaoTest extends BaseSpringTest {

    public static final int costCenterCnt = 170;

    @Test
    public void getByIdInteger() {
        CostCenter costCenter = CostCenter.getDao().getById(74);
        assertEquals(74, costCenter.getId().intValue());
        assertEquals("New Jersey Water Science Center", costCenter.getName());
    }

    @Test
    public void getByIdString() {
        CostCenter costCenter = CostCenter.getDao().getById("114");
        assertEquals(114, costCenter.getId().intValue());
        assertEquals("Eastern Geographic Science Center", costCenter.getName());
    }

    @Test
    public void getByMap() {
        List<CostCenter> costCenters = CostCenter.getDao().getByMap(null);
        assertEquals(costCenterCnt, costCenters.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", "74");
        costCenters = CostCenter.getDao().getByMap(filters);
        assertEquals(1, costCenters.size());
        assertEquals(74, costCenters.get(0).getId().intValue());
        assertEquals("New Jersey Water Science Center", costCenters.get(0).getName());

        filters.clear();
        filters.put("name", "ea");
        costCenters = CostCenter.getDao().getByMap(filters);
        assertEquals(9, costCenters.size());
    }

    @Test
    public void notImplemented() {
        try {
            CostCenter.getDao().add(new CostCenter());
            fail("Was able to add.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("prodId", 1);
            CostCenter.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            CostCenter.getDao().update(new CostCenter());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            CostCenter.getDao().delete(new CostCenter());
            fail("Was able to delete.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            CostCenter.getDao().deleteById(1);
            fail("Was able to delete by it.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }
    }

}
