package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
public class AffiliationDaoTest extends BaseSpringDaoTest {

    public static final int affiliationCnt = 7;

    @Test
    public void getByIdInteger() {
        Affiliation<?> costCenter = Affiliation.getDao().getById(1);
        assertAffiliation1(costCenter);

        Affiliation<?> outsideAffiliation = Affiliation.getDao().getById(5);
        assertAffiliation5(outsideAffiliation);
    }

    @Test
    public void getByIdString() {
        Affiliation<?> costCenter = Affiliation.getDao().getById("1");
        assertAffiliation1(costCenter);

        Affiliation<?> outsideAffiliation = Affiliation.getDao().getById("5");
        assertAffiliation5(outsideAffiliation);
    }

    @Test
    public void getByMap() {
        List<Affiliation<?>> affiliations = Affiliation.getDao().getByMap(null);
        assertEquals(affiliationCnt, affiliations.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", "5");
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(1, affiliations.size());
        assertAffiliation5(affiliations.get(0));

        filters.clear();
        filters.put("text", "out");
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(3, affiliations.size());

        filters.clear();
        filters.put("active", false);
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(2, affiliations.size());

        filters.clear();
        filters.put("active", true);
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(5, affiliations.size());

        filters.clear();
        filters.put("usgs", false);
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(3, affiliations.size());

        filters.clear();
        filters.put("usgs", true);
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(4, affiliations.size());

        filters.put("id", "4");
        filters.put("text", "affil");
        filters.put("active", true);
        filters.put("ipdsId", "1");
        affiliations = Affiliation.getDao().getByMap(filters);
        assertEquals(1, affiliations.size());
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
            Affiliation.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Affiliation.getDao().update(new OutsideAffiliation());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Affiliation.getDao().delete(new OutsideAffiliation());
            fail("Was able to delete.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Affiliation.getDao().deleteById(1);
            fail("Was able to delete by it.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }
    }

    public static void assertAffiliation1(Affiliation<?> affiliation) {
        assertEquals(1, affiliation.getId().intValue());
        assertEquals("Affiliation Cost Center 1", affiliation.getText());
        assertTrue(affiliation.isActive());
        assertTrue(affiliation.isUsgs());
        assertTrue(affiliation instanceof CostCenter);
        assertEquals(4, ((CostCenter) affiliation).getIpdsId().intValue());
    }

    public static void assertAffiliation5(Affiliation<?> affiliation) {
        assertEquals(5, affiliation.getId().intValue());
        assertEquals("Outside Affiliation 1", affiliation.getText());
        assertTrue(affiliation.isActive());
        assertFalse(affiliation.isUsgs());
        assertTrue(affiliation instanceof OutsideAffiliation);
    }

}
