package gov.usgs.cida.pubs.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LinkTypeDao.class})
public class LinkTypeDaoIT extends BaseIT {

	public static final int LINK_TYPES_CNT = 33;
	public static final int LINK_TYPES_R_CNT = 4;

	@Autowired
	LinkTypeDao linkTypeDao;

	@Test
	public void getByIdInteger() {
		LinkType linkType = linkTypeDao.getById(1);
		assertNotNull(linkType);
		assertLinkType1(linkType);
	}

	@Test
	public void getByIdString() {
		LinkType linkType = linkTypeDao.getById("2");
		assertNotNull(linkType);
		assertLinkType2(linkType);
	}

	@Test
	public void getByMap() {
		List<LinkType> linkTypes = linkTypeDao.getByMap(null);
		assertEquals(LINK_TYPES_CNT, linkTypes.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put(LinkTypeDao.ID_SEARCH, 1);
		linkTypes = linkTypeDao.getByMap(filters);
		assertEquals(1, linkTypes.size());
		assertLinkType1(linkTypes.get(0));

		filters.clear();
		filters.put(LinkTypeDao.TEXT_SEARCH, "r");
		linkTypes = linkTypeDao.getByMap(filters);
		assertEquals(LINK_TYPES_R_CNT, linkTypes.size());
		filters.put(LinkTypeDao.ID_SEARCH, 19);
		linkTypes = linkTypeDao.getByMap(filters);
		assertEquals(1, linkTypes.size());
		assertLinkType19(linkTypes.get(0));
	}

	@Test
	public void notImplemented() {
		try {
			linkTypeDao.add(new LinkType());
			fail("Was able to add.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			linkTypeDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			linkTypeDao.update(new LinkType());
			fail("Was able to update.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			linkTypeDao.delete(new LinkType());
			fail("Was able to delete.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			linkTypeDao.deleteById(1);
			fail("Was able to delete by it.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
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
