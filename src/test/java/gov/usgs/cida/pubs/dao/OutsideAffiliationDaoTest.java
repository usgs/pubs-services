package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class OutsideAffiliationDaoTest extends BaseSpringTest {

    public static final int outsideAffiliationCnt = 2;

    @Test
    public void getByIdInteger() {
        Affiliation<?> outsideAffiliation = OutsideAffiliation.getDao().getById(182);
        assertEquals(182, outsideAffiliation.getId().intValue());
        assertEquals("The Outer Limits", outsideAffiliation.getName());
    }

    @Test
    public void getByIdString() {
        Affiliation<?> outsideAffiliation = OutsideAffiliation.getDao().getById("182");
        assertEquals(182, outsideAffiliation.getId().intValue());
        assertEquals("The Outer Limits", outsideAffiliation.getName());
    }

    @Test
    public void getByMap() {
        List<Affiliation<?>> outsideAffiliations = OutsideAffiliation.getDao().getByMap(null);
        assertEquals(outsideAffiliationCnt, outsideAffiliations.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", "182");
        outsideAffiliations = OutsideAffiliation.getDao().getByMap(filters);
        assertEquals(1, outsideAffiliations.size());
        assertEquals(182, outsideAffiliations.get(0).getId().intValue());
        assertEquals("The Outer Limits", outsideAffiliations.get(0).getName());

        filters.clear();
        filters.put("name", "th");
        outsideAffiliations = OutsideAffiliation.getDao().getByMap(filters);
        assertEquals(1, outsideAffiliations.size());
        filters.put("active", false);
        outsideAffiliations = OutsideAffiliation.getDao().getByMap(filters);
        assertEquals(0, outsideAffiliations.size());

        filters.put("active", true);
        outsideAffiliations = OutsideAffiliation.getDao().getByMap(filters);
        assertEquals(1, outsideAffiliations.size());

        filters.put("usgs", false);
        outsideAffiliations = OutsideAffiliation.getDao().getByMap(filters);
        assertEquals(1, outsideAffiliations.size());

        filters.put("usgs", true);
        outsideAffiliations = OutsideAffiliation.getDao().getByMap(filters);
        assertEquals(0, outsideAffiliations.size());

    }

    @Test
    public void notImplemented() {
        try {
            OutsideAffiliation.getDao().add(new OutsideAffiliation());
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
