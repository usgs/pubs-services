package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.domain.ContributorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ContributorTypeDaoTest extends BaseSpringDaoTest {

    public static final int contributorTypeCnt = 2;

    @Test
    public void getByIdInteger() {
        ContributorType contributorType = ContributorType.getDao().getById(1);
        assertEquals(1, contributorType.getId().intValue());
        assertEquals("Authors", contributorType.getName());
    }

    @Test
    public void getByIdString() {
        ContributorType contributorType = ContributorType.getDao().getById("2");
        assertEquals(2, contributorType.getId().intValue());
        assertEquals("Editors", contributorType.getName());
    }

    @Test
    public void getByMap() {
        List<ContributorType> contributorTypes = ContributorType.getDao().getByMap(null);
        assertEquals(contributorTypeCnt, contributorTypes.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", "1");
        contributorTypes = ContributorType.getDao().getByMap(filters);
        assertEquals(1, contributorTypes.size());
        assertEquals(1, contributorTypes.get(0).getId().intValue());
        assertEquals("Authors", contributorTypes.get(0).getName());

        filters.clear();
        filters.put("name", "ed");
        contributorTypes = ContributorType.getDao().getByMap(filters);
        assertEquals(1, contributorTypes.size());

    }

    @Test
    public void notImplemented() {
        try {
            ContributorType.getDao().add(new ContributorType());
            fail("Was able to add.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("prodId", 1);
            ContributorType.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            ContributorType.getDao().update(new ContributorType());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            ContributorType.getDao().delete(new ContributorType());
            fail("Was able to delete.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            ContributorType.getDao().deleteById(1);
            fail("Was able to delete by it.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }
    }

}
