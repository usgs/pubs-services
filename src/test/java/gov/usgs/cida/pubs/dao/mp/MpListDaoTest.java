package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.domain.mp.MpList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MpListDaoTest extends BaseSpringDaoTest {
	
	private static final List<String> IGNORE_PROPERTIES = Arrays.asList("id", "validationErrors");

	@Test
	public void getbyIdTests() {
		MpList list = MpList.getDao().getById(1);
		assertMpList1(list);
		list = MpList.getDao().getById("2");
		assertMpList2(list);
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
		filters.put("name", "pend");
		mpLists = MpList.getDao().getByMap(filters);
		assertNotNull(mpLists);
		assertEquals(2, mpLists.size());
	}

	@Test
	public void addUpdateDeleteTest() {
		MpList newList = new MpList();
		newList.setName("test name");
		newList.setDescription("test description");
		newList.setType("TEST_TYPE");
		MpList.getDao().add(newList);
		
		MpList persistedA = MpList.getDao().getById(newList.getId());
		assertNotNull(persistedA);
		assertNotNull(persistedA.getId());
		assertDaoTestResults(MpList.class, newList, persistedA, IGNORE_PROPERTIES, true, true);

		persistedA.setName("updatedName");
		persistedA.setDescription("updated description");
		persistedA.setType("UPDATED_TYPE");
		MpList.getDao().update(persistedA);

		MpList persistedC = MpList.getDao().getById(newList.getId());
		assertNotNull(persistedC);
		assertNotNull(persistedC.getId());
		assertDaoTestResults(MpList.class, persistedA, persistedC, IGNORE_PROPERTIES, true, true);

		MpList.getDao().delete(persistedC);
		assertNull(MpList.getDao().getById(newList.getId()));

		MpList.getDao().deleteById(2);
		assertNull(MpList.getDao().getById(2));
	}

	public static void assertMpList1(MpList list) {
		assertNotNull(list);
		assertEquals(1, list.getId().intValue());
		assertEquals("Need Approval", list.getName());
		assertEquals("Citations that need to be approved", list.getDescription());
		assertEquals("MP_SHARED_SUPER_NEED_APPROVAL", list.getType());
	}

	public static void assertMpList2(MpList list) {
		assertNotNull(list);
		assertEquals(2, list.getId().intValue());
		assertEquals("Approved", list.getName());
		assertEquals("Citations that have been approved, and will be loaded", list.getDescription());
		assertEquals("MP_SHARED_SUPER_APPROVED", list.getType());
	}

}
