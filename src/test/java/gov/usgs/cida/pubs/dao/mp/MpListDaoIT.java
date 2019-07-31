package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpList.MpListType;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, MpListDao.class})
public class MpListDaoIT extends BaseIT {

	@Autowired
	MpListDao mpListDao;

	@Test
	public void getbyIdTests() {
		MpList list = mpListDao.getById(1);
		assertMpList1(list);
		list = mpListDao.getById("9");
		assertMpList9(list);
	}

	@Test
	public void getByMapTests() {
		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 1);
		List<MpList> mpLists = mpListDao.getByMap(filters);
		assertNotNull(mpLists);
		assertEquals(1, mpLists.size());
		assertMpList1((MpList) mpLists.get(0));

		filters.clear();
		filters.put("text", "ipds");
		mpLists = mpListDao.getByMap(filters);
		assertNotNull(mpLists);
		assertEquals(7, mpLists.size());

		filters.clear();
		filters.put("listType", MpListType.PUBS);
		mpLists = mpListDao.getByMap(filters);
		assertNotNull(mpLists);
		assertEquals(18, mpLists.size());

		filters.clear();
		filters.put("listType", MpListType.SPN);
		mpLists = mpListDao.getByMap(filters);
		assertNotNull(mpLists);
		assertEquals(1, mpLists.size());
	}

	@Test
	public void notImplemented() {
		try {
			mpListDao.add(new MpList());
			fail("Was able to add.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			mpListDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			mpListDao.update(new MpList());
			fail("Was able to update.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			mpListDao.delete(new MpList());
			fail("Was able to delete.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			mpListDao.deleteById(1);
			fail("Was able to delete by it.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}
	}

	public static void assertMpList1(MpList list) {
		assertNotNull(list);
		assertEquals(1, list.getId().intValue());
		assertEquals("Need Approval", list.getText());
		assertEquals("Citations that need to be approved", list.getDescription());
		assertEquals(MpListType.PUBS, list.getType());
		assertNull(list.getIpdsInternalId());
	}

	public static void assertMpList9(MpList list) {
		assertNotNull(list);
		assertEquals(9, list.getId().intValue());
		assertEquals("IPDS SPN Production", list.getText());
		assertEquals("IPDS Records that have entered SPN Production status", list.getDescription());
		assertEquals(MpListType.SPN, list.getType());
		assertNull(list.getIpdsInternalId());
	}

	public static MpList buildMpList(Integer id) {
		MpList mpList = new MpList();
		mpList.setId(id);
		mpList.setText("List " + id);
		mpList.setDescription("Description " + id);
		mpList.setType(MpListType.SPN);
		mpList.setIpdsInternalId(1);
		mpList.setInsertDate(LocalDateTime.now());
		mpList.setInsertUsername(PubsConstantsHelper.ANONYMOUS_USER);
		mpList.setUpdateDate(LocalDateTime.now());
		mpList.setUpdateUsername(PubsConstantsHelper.ANONYMOUS_USER);
		return mpList;
	}
}