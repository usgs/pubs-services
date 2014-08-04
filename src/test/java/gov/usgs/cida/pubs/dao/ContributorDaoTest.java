package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.Contributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ContributorDaoTest extends BaseSpringTest {

    public static final int contributorCnt = 2;

    @Test
    public void getByIdInteger() {
        Contributor contributor = Contributor.getDao().getById(1);
        assertEquals(1, contributor.getId().intValue());
        assertEquals("ConFirst", contributor.getFirst());
        assertEquals("ConGiven", contributor.getGiven());
        assertEquals("ConSuffix", contributor.getSuffix());
        assertEquals("con@usgs.gov", contributor.getEmail());
        assertEquals(22, contributor.getAffiliation().getId().intValue());
        assertNull(contributor.getLiteral());
    }

    @Test
    public void getByIdString() {
        Contributor contributor = Contributor.getDao().getById("2");
        assertEquals(2, contributor.getId().intValue());
        assertNull(contributor.getFirst());
        assertNull(contributor.getGiven());
        assertNull(contributor.getSuffix());
        assertNull(contributor.getEmail());
        assertNull(contributor.getAffiliation());
        assertEquals("US Geological Survey Ice Survey Team", contributor.getLiteral());
    }

    @Test
    public void getByMap() {
        List<Contributor> contributors = Contributor.getDao().getByMap(null);
        assertEquals(contributorCnt, contributors.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", "1");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());
        assertEquals("ConFirst", contributors.get(0).getFirst());

        filters.clear();
        filters.put("personName", "con");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("corporationName", "us");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("category", "person");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());
        filters.put("personName", "con");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("category", "corporation");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());
        filters.put("corporationName", "us");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());

    }

    @Test
    public void notImplemented() {
        try {
            Contributor.getDao().add(new Contributor());
            fail("Was able to add.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("prodId", 1);
            Contributor.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Contributor.getDao().update(new Contributor());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Contributor.getDao().delete(new Contributor());
            fail("Was able to delete.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Contributor.getDao().deleteById(1);
            fail("Was able to delete by it.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }
    }


}
