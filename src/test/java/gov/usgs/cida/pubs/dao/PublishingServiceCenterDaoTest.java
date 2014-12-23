package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class PublishingServiceCenterDaoTest extends BaseSpringTest {

    public static final int PSC_CNT = 14;
    public static final int PSC_R_CNT = 3;

    @Test
    public void getByIdInteger() {
    	Integer nil = null;
    	PublishingServiceCenter.getDao().getById(nil);
        PublishingServiceCenter psc = PublishingServiceCenter.getDao().getById(1);
        assertNotNull(psc);
        assertPsc1(psc);
    }

	@Test
    public void getByIdString() {
	   	String nil = null;
    	PublishingServiceCenter.getDao().getById(nil);
        PublishingServiceCenter psc = PublishingServiceCenter.getDao().getById("2");
        assertNotNull(psc);
        assertPsc2(psc);
    }

	@Test
    public void getByIpdsValue() {
		PublishingServiceCenter.getDao().getByIpdsId(null);
        PublishingServiceCenter psc = PublishingServiceCenter.getDao().getByIpdsId(2);
        assertNotNull(psc);
        assertPsc2(psc);
    }

    @Test
    public void getByMap() {
        List<PublishingServiceCenter> pscs = PublishingServiceCenter.getDao().getByMap(null);
        assertEquals(PSC_CNT, pscs.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put(PublishingServiceCenterDao.ID_SEARCH, "1");
        pscs = PublishingServiceCenter.getDao().getByMap(filters);
        assertEquals(1, pscs.size());
        assertPsc1(pscs.get(0));

        filters.clear();
        filters.put(PublishingServiceCenterDao.TEXT_SEARCH, "r");
        pscs = PublishingServiceCenter.getDao().getByMap(filters);
        assertEquals(PSC_R_CNT, pscs.size());
        filters.put(PublishingServiceCenterDao.ID_SEARCH, "8");
        pscs = PublishingServiceCenter.getDao().getByMap(filters);
        assertEquals(1, pscs.size());
        assertPsc8(pscs.get(0));
    }

    @Test
    public void notImplemented() {
        try {
            PublishingServiceCenter.getDao().add(new PublishingServiceCenter());
            fail("Was able to add.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("prodId", 1);
            PublishingServiceCenter.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            PublishingServiceCenter.getDao().update(new PublishingServiceCenter());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            PublishingServiceCenter.getDao().delete(new PublishingServiceCenter());
            fail("Was able to delete.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            PublishingServiceCenter.getDao().deleteById(1);
            fail("Was able to delete by it.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    public static void assertPsc1(PublishingServiceCenter psc) {
		assertEquals(1, psc.getId().intValue());
		assertEquals("Sacramento PSC", psc.getText());
	}

    public static void assertPsc2(PublishingServiceCenter psc) {
		assertEquals(2, psc.getId().intValue());
		assertEquals("Denver PSC", psc.getText());
	}

    public static void assertPsc8(PublishingServiceCenter psc) {
		assertEquals(8, psc.getId().intValue());
		assertEquals("Raleigh PSC", psc.getText());
	}

}
