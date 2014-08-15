package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class AffiliationDaoTest extends BaseSpringTest {

    public static final int affiliationCnt = 172;

    @Test
    public void getByIdInteger() {
        Affiliation<?> affiliation = Affiliation.getDao().getById(182);
        assertEquals(182, affiliation.getId().intValue());
        assertEquals("The Outer Limits", affiliation.getName());

        Affiliation<?> costCenter = CostCenter.getDao().getById(74);
        assertEquals(74, costCenter.getId().intValue());
        assertEquals("New Jersey Water Science Center", costCenter.getName());
    }

    @Test
    public void getByIdString() {
        Affiliation<?> affiliation = Affiliation.getDao().getById("182");
        assertEquals(182, affiliation.getId().intValue());
        assertEquals("The Outer Limits", affiliation.getName());

        Affiliation<?> costCenter = CostCenter.getDao().getById("74");
        assertEquals(74, costCenter.getId().intValue());
        assertEquals("New Jersey Water Science Center", costCenter.getName());
    }

    @Test
    public void getByMap() {
        List<Affiliation<?>> affiliations = Affiliation.getDao().getByMap(null);
        assertEquals(affiliationCnt, affiliations.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", "182");
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(1, affiliations.size());
        assertEquals(182, affiliations.get(0).getId().intValue());
        assertEquals("The Outer Limits", affiliations.get(0).getName());

        filters.clear();
        filters.put("name", "t");
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(3, affiliations.size());
        filters.put("active", false);
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(0, affiliations.size());

        filters.put("active", true);
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(3, affiliations.size());

        filters.put("usgs", false);
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(1, affiliations.size());

        filters.put("usgs", true);
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(2, affiliations.size());

    }

    @Test
    public void notImplemented() {
        try {
            Affiliation.getDao().add(new OutsideAffiliation());
            fail("Was able to add.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("prodId", 1);
            OutsideAffiliation.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            OutsideAffiliation.getDao().update(new OutsideAffiliation());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            OutsideAffiliation.getDao().delete(new OutsideAffiliation());
            fail("Was able to delete.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            OutsideAffiliation.getDao().deleteById(1);
            fail("Was able to delete by it.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }
    }

}
