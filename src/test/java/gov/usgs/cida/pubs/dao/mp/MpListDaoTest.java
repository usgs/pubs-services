package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpList.MpListType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MpListDaoTest extends BaseSpringTest {
	
	@Test
	public void getbyIdTests() {
		MpList list = MpList.getDao().getById(1);
		assertMpList1(list);
		list = MpList.getDao().getById("9");
		assertMpList9(list);
	}

	@Test
	public void getByMapTests() {
		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 1);
		List<MpList> mpLists = MpList.getDao().getByMap(filters);
		assertNotNull(mpLists);
		assertEquals(1, mpLists.size());
		assertMpList1((MpList) mpLists.get(0));

		filters.clear();
		filters.put("text", "ipds");
		mpLists = MpList.getDao().getByMap(filters);
		assertNotNull(mpLists);
		assertEquals(7, mpLists.size());

		filters.clear();
		filters.put("listType", MpListType.PUBS);
		mpLists = MpList.getDao().getByMap(filters);
		assertNotNull(mpLists);
		assertEquals(18, mpLists.size());

		filters.clear();
		filters.put("listType", MpListType.SPN);
		mpLists = MpList.getDao().getByMap(filters);
		assertNotNull(mpLists);
		assertEquals(1, mpLists.size());
	}

	@Test
	public void notImplemented() {
		try {
			MpList.getDao().add(new MpList());
			fail("Was able to add.");
		} catch (Exception e) {
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			MpList.getDao().getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			MpList.getDao().update(new MpList());
			fail("Was able to update.");
		} catch (Exception e) {
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			MpList.getDao().delete(new MpList());
			fail("Was able to delete.");
		} catch (Exception e) {
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			MpList.getDao().deleteById(1);
			fail("Was able to delete by it.");
		} catch (Exception e) {
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
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
		mpList.setInsertUsername(PubsConstants.ANONYMOUS_USER);
		mpList.setUpdateDate(LocalDateTime.now());
		mpList.setUpdateUsername(PubsConstants.ANONYMOUS_USER);
		return mpList;
	}
}