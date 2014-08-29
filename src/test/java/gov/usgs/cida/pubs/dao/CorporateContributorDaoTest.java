package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CorporateContributorDaoTest extends BaseSpringDaoTest {

    private static final int corporateContributorCnt = 1;

    @Test
    public void getByIdInteger() {
        Contributor<?> contributor = CorporateContributor.getDao().getById(2);
        assertEquals(2, contributor.getId().intValue());
        assertTrue(contributor instanceof CorporateContributor);
        CorporateContributor corpContributor = (CorporateContributor) contributor;
        assertEquals("US Geological Survey Ice Survey Team", corpContributor.getOrganization());
    }

    @Test
    public void getByIdString() {
        Contributor<?> contributor = CorporateContributor.getDao().getById("2");
        assertEquals(2, contributor.getId().intValue());
        assertTrue(contributor instanceof CorporateContributor);
        CorporateContributor corpContributor = (CorporateContributor) contributor;
        assertEquals("US Geological Survey Ice Survey Team", corpContributor.getOrganization());
    }

    @Test
    public void getByMap() {
        List<Contributor<?>> contributors = CorporateContributor.getDao().getByMap(null);
        assertEquals(corporateContributorCnt, contributors.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", "2");
        contributors = CorporateContributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("name", "us");
        contributors = CorporateContributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("ipdsContributorId", 2);
        contributors = CorporateContributor.getDao().getByMap(filters);
        assertEquals(0, contributors.size());

        filters.clear();
        filters.put("id", "2");
        filters.put("name", "us");
        contributors = CorporateContributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());
        filters.put("ipdsContributorId", 2);
        contributors = CorporateContributor.getDao().getByMap(filters);
        assertEquals(0, contributors.size());
    }

    @Test
    public void addUpdateDeleteTest() {
        CorporateContributor corporation = new CorporateContributor();
        corporation.setOrganization("organization");
        CorporateContributor.getDao().add(corporation);
        CorporateContributor persistedCorp = (CorporateContributor) CorporateContributor.getDao().getById(corporation.getId());
        assertDaoTestResults(CorporateContributor.class, corporation, persistedCorp, ContributorDaoTest.IGNORE_PROPERTIES_CORPORATION, true, true);

        corporation.setOrganization("organization2");
        CorporateContributor.getDao().update(corporation);
        persistedCorp = (CorporateContributor) CorporateContributor.getDao().getById(corporation.getId());
        assertDaoTestResults(CorporateContributor.class, corporation, persistedCorp, ContributorDaoTest.IGNORE_PROPERTIES_CORPORATION, true, true);

        CorporateContributor.getDao().delete(corporation);
        assertNull(CorporateContributor.getDao().getById(corporation.getId()));
    }

    @Test
    public void notImplemented() {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("prodId", 1);
            CorporateContributor.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }
    }

}