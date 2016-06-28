package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.LinkType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class LinkTypeDaoTest extends BaseSpringTest {

    public static final int LINK_TYPES_CNT = 30;
    public static final int LINK_TYPES_R_CNT = 4;

    @Test
    public void getByIdInteger() {
        LinkType linkType = LinkType.getDao().getById(1);
        assertNotNull(linkType);
        assertLinkType1(linkType);
    }

	@Test
    public void getByIdString() {
        LinkType linkType = LinkType.getDao().getById("2");
        assertNotNull(linkType);
        assertLinkType2(linkType);
    }

    @Test
    public void getByMap() {
        List<LinkType> linkTypes = LinkType.getDao().getByMap(null);
        assertEquals(LINK_TYPES_CNT, linkTypes.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put(LinkTypeDao.ID_SEARCH, "1");
        linkTypes = LinkType.getDao().getByMap(filters);
        assertEquals(1, linkTypes.size());
        assertLinkType1(linkTypes.get(0));

        filters.clear();
        filters.put(LinkTypeDao.TEXT_SEARCH, "r");
        linkTypes = LinkType.getDao().getByMap(filters);
        assertEquals(LINK_TYPES_R_CNT, linkTypes.size());
        filters.put(LinkTypeDao.ID_SEARCH, "19");
        linkTypes = LinkType.getDao().getByMap(filters);
        assertEquals(1, linkTypes.size());
        assertLinkType19(linkTypes.get(0));
    }

    @Test
    public void notImplemented() {
        try {
            LinkType.getDao().add(new LinkType());
            fail("Was able to add.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(PublicationDao.PROD_ID, 1);
            LinkType.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            LinkType.getDao().update(new LinkType());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            LinkType.getDao().delete(new LinkType());
            fail("Was able to delete.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            LinkType.getDao().deleteById(1);
            fail("Was able to delete by it.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    public static void assertLinkType1(LinkType linkType) {
		assertEquals(1, linkType.getId().intValue());
		assertEquals("Abstract", linkType.getText());
	}

    public static void assertLinkType2(LinkType linkType) {
		assertEquals(2, linkType.getId().intValue());
		assertEquals("Additional Report Piece", linkType.getText());
	}

    public static void assertLinkType19(LinkType linkType) {
		assertEquals(19, linkType.getId().intValue());
		assertEquals("Raw Data", linkType.getText());
	}

}
