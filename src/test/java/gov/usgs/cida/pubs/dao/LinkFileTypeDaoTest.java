package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.LinkFileType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class LinkFileTypeDaoTest extends BaseSpringTest {

    public static final int LINK_FILE_TYPES_CNT = 7;
    public static final int LINK_FILE_TYPES_S_CNT = 1;

    @Test
    public void getByIdInteger() {
        LinkFileType linkFileType = LinkFileType.getDao().getById(1);
        assertNotNull(linkFileType);
        assertLinkFileType1(linkFileType);
    }

	@Test
    public void getByIdString() {
        LinkFileType linkFileType = LinkFileType.getDao().getById("2");
        assertNotNull(linkFileType);
        assertLinkFileType2(linkFileType);
    }

    @Test
    public void getByMap() {
        List<LinkFileType> linkFileTypes = LinkFileType.getDao().getByMap(null);
        assertEquals(LINK_FILE_TYPES_CNT, linkFileTypes.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put(LinkFileTypeDao.ID_SEARCH, "1");
        linkFileTypes = LinkFileType.getDao().getByMap(filters);
        assertEquals(1, linkFileTypes.size());
        assertLinkFileType1(linkFileTypes.get(0));

        filters.clear();
        filters.put(LinkFileTypeDao.TEXT_SEARCH, "s");
        linkFileTypes = LinkFileType.getDao().getByMap(filters);
        assertEquals(LINK_FILE_TYPES_S_CNT, linkFileTypes.size());
        filters.put(LinkFileTypeDao.ID_SEARCH, "4");
        linkFileTypes = LinkFileType.getDao().getByMap(filters);
        assertEquals(1, linkFileTypes.size());
        assertLinkFileType4(linkFileTypes.get(0));
    }

    @Test
    public void notImplemented() {
        try {
            LinkFileType.getDao().add(new LinkFileType());
            fail("Was able to add.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("prodId", 1);
            LinkFileType.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            LinkFileType.getDao().update(new LinkFileType());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            LinkFileType.getDao().delete(new LinkFileType());
            fail("Was able to delete.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }

        try {
            LinkFileType.getDao().deleteById(1);
            fail("Was able to delete by it.");
        } catch (Exception e) {
            assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
        }
    }

    public static void assertLinkFileType1(LinkFileType linkFileType) {
		assertEquals(1, linkFileType.getId().intValue());
		assertEquals("pdf", linkFileType.getText());
	}

    public static void assertLinkFileType2(LinkFileType linkFileType) {
		assertEquals(2, linkFileType.getId().intValue());
		assertEquals("txt", linkFileType.getText());
	}

    public static void assertLinkFileType4(LinkFileType linkFileType) {
		assertEquals(4, linkFileType.getId().intValue());
		assertEquals("shapefile", linkFileType.getText());
	}

}
