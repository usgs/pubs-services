package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.PublicationSeries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class PublicationSeriesDaoTest extends BaseSpringTest {

    public static final int pubSeriesCnt = 508;

    @Test
    public void getByIdInteger() {
        PublicationSeries pubSeries = PublicationSeries.getDao().getById(330);
        assertNotNull(pubSeries);
        assertEquals(330, pubSeries.getId().intValue());
        assertEquals("Open-File Report", pubSeries.getName());
        assertEquals("OFR", pubSeries.getCode());
        assertNull(pubSeries.getSeriesDoiName());
        assertEquals("0196-1497", pubSeries.getPrintIssn());
        assertEquals("2331-1258", pubSeries.getOnlineIssn());

        pubSeries = PublicationSeries.getDao().getById(341);
        assertNotNull(pubSeries);
        assertEquals(341, pubSeries.getId().intValue());
        assertEquals("Water Supply Paper", pubSeries.getName());
        assertEquals("WSP", pubSeries.getCode());
        assertNull(pubSeries.getSeriesDoiName());
        assertNull(pubSeries.getPrintIssn());
        assertNull(pubSeries.getOnlineIssn());
    }

    @Test
    public void getByIdString() {
        PublicationSeries pubSeries = PublicationSeries.getDao().getById("1");
        assertNotNull(pubSeries);
        assertEquals(1, pubSeries.getId().intValue());
        assertEquals("Administrative Report", pubSeries.getName());
        assertNull(pubSeries.getCode());
        assertNull(pubSeries.getSeriesDoiName());
        assertNull(pubSeries.getPrintIssn());
        assertNull(pubSeries.getOnlineIssn());
    }

    @Test
    public void getByMap() {
        List<PublicationSeries> pubSeries = PublicationSeries.getDao().getByMap(null);
        assertEquals(pubSeriesCnt, pubSeries.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", 133);
        pubSeries = PublicationSeries.getDao().getByMap(filters);
        assertNotNull(pubSeries);
        assertEquals(1, pubSeries.size());
        assertEquals(133, pubSeries.get(0).getId().intValue());
        assertEquals("Report", pubSeries.get(0).getName());
        assertNull(pubSeries.get(0).getCode());
        assertNull(pubSeries.get(0).getSeriesDoiName());
        assertNull(pubSeries.get(0).getPrintIssn());
        assertNull(pubSeries.get(0).getOnlineIssn());

        filters.clear();
        filters.put("publicationSubtypeId", 6);
        pubSeries = PublicationSeries.getDao().getByMap(filters);
        assertNotNull(pubSeries);
        assertEquals(50, pubSeries.size());

        filters.put("name", "to");
        pubSeries = PublicationSeries.getDao().getByMap(filters);
        assertEquals(2, pubSeries.size());

        filters.clear();
        filters.put("code", "MINERAL");
        pubSeries = PublicationSeries.getDao().getByMap(filters);
        assertEquals(1, pubSeries.size());
        assertEquals(323, pubSeries.get(0).getId().intValue());
        assertEquals("Mineral Commodities Summaries", pubSeries.get(0).getName());
        assertEquals("MINERAL", pubSeries.get(0).getCode());
        assertNull(pubSeries.get(0).getSeriesDoiName());
        assertEquals("0076-8952", pubSeries.get(0).getPrintIssn());
        assertNull(pubSeries.get(0).getOnlineIssn());

    }

    @Test
    public void notImplemented() {
        try {
            PublicationSeries.getDao().add(new PublicationSeries());
            fail("Was able to add.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("prodId", 1);
            PublicationSeries.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            PublicationSeries.getDao().update(new PublicationSeries());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            PublicationSeries.getDao().delete(new PublicationSeries());
            fail("Was able to delete.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }

        try {
            PublicationSeries.getDao().deleteById(1);
            fail("Was able to delete by it.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }
    }

}
