package gov.usgs.cida.pubs.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, PublishingServiceCenterDao.class})
public class PublishingServiceCenterDaoIT extends BaseIT {

	public static final int PSC_CNT = 15;
	public static final int PSC_R_CNT = 3;

	@Autowired
	PublishingServiceCenterDao publishingServiceCenterDao;

	@Test
	public void getByIdInteger() {
		Integer nil = null;
		assertNull(publishingServiceCenterDao.getById(nil));
		PublishingServiceCenter psc = publishingServiceCenterDao.getById(1);
		assertNotNull(psc);
		assertPsc1(psc);
	}

	@Test
	public void getByIdString() {
		String nil = null;
		assertNull(publishingServiceCenterDao.getById(nil));
		PublishingServiceCenter psc = publishingServiceCenterDao.getById("2");
		assertNotNull(psc);
		assertPsc2(psc);
	}

	@Test
	public void getByMap() {
		List<PublishingServiceCenter> pscs = publishingServiceCenterDao.getByMap(null);
		assertEquals(PSC_CNT, pscs.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put(PublishingServiceCenterDao.ID_SEARCH, 1);
		pscs = publishingServiceCenterDao.getByMap(filters);
		assertEquals(1, pscs.size());
		assertPsc1(pscs.get(0));

		filters.clear();
		filters.put(PublishingServiceCenterDao.TEXT_SEARCH, "r");
		pscs = publishingServiceCenterDao.getByMap(filters);
		assertEquals(PSC_R_CNT, pscs.size());
		filters.put(PublishingServiceCenterDao.ID_SEARCH, 8);
		pscs = publishingServiceCenterDao.getByMap(filters);
		assertEquals(1, pscs.size());
		assertPsc8(pscs.get(0));
	}

	@Test
	public void notImplemented() {
		try {
			publishingServiceCenterDao.add(new PublishingServiceCenter());
			fail("Was able to add.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			publishingServiceCenterDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			publishingServiceCenterDao.update(new PublishingServiceCenter());
			fail("Was able to update.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			publishingServiceCenterDao.delete(new PublishingServiceCenter());
			fail("Was able to delete.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			publishingServiceCenterDao.deleteById(1);
			fail("Was able to delete by it.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
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
