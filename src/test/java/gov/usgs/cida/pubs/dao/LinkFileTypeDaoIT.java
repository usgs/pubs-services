package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LinkFileTypeDao.class})
public class LinkFileTypeDaoIT extends BaseIT {

	public static final int LINK_FILE_TYPES_CNT = 8;
	public static final int LINK_FILE_TYPES_S_CNT = 1;

	@Autowired
	LinkFileTypeDao linkFileTypeDao;

	@Test
	public void getByIdInteger() {
		LinkFileType linkFileType = linkFileTypeDao.getById(1);
		assertNotNull(linkFileType);
		assertLinkFileType1(linkFileType);
	}

	@Test
	public void getByIdString() {
		LinkFileType linkFileType = linkFileTypeDao.getById("2");
		assertNotNull(linkFileType);
		assertLinkFileType2(linkFileType);
	}

	@Test
	public void getByMap() {
		List<LinkFileType> linkFileTypes = linkFileTypeDao.getByMap(null);
		assertEquals(LINK_FILE_TYPES_CNT, linkFileTypes.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put(LinkFileTypeDao.ID_SEARCH, 1);
		linkFileTypes = linkFileTypeDao.getByMap(filters);
		assertEquals(1, linkFileTypes.size());
		assertLinkFileType1(linkFileTypes.get(0));

		filters.clear();
		filters.put(LinkFileTypeDao.TEXT_SEARCH, "s");
		linkFileTypes = linkFileTypeDao.getByMap(filters);
		assertEquals(LINK_FILE_TYPES_S_CNT, linkFileTypes.size());
		filters.put(LinkFileTypeDao.ID_SEARCH, 4);
		linkFileTypes = linkFileTypeDao.getByMap(filters);
		assertEquals(1, linkFileTypes.size());
		assertLinkFileType4(linkFileTypes.get(0));
	}

	@Test
	public void notImplemented() {
		try {
			linkFileTypeDao.add(new LinkFileType());
			fail("Was able to add.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			linkFileTypeDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			linkFileTypeDao.update(new LinkFileType());
			fail("Was able to update.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			linkFileTypeDao.delete(new LinkFileType());
			fail("Was able to delete.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			linkFileTypeDao.deleteById(1);
			fail("Was able to delete by it.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
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
