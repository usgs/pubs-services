package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testData/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class MpListPublicationDaoTest extends BaseSpringTest {

	public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors");

	@Test
	public void getbyIdTests() {
		MpListPublication listPub = MpListPublication.getDao().getById(1);
		assertMpListPublication1(listPub);
		listPub = MpListPublication.getDao().getById("2");
		assertMpListPublication2(listPub);
	}

	@Test
	public void getByMapTests() {
		Map<String, Object> filters = new HashMap<>();
		filters.put("mpListId", 9);
		List<MpListPublication> listPubs = MpListPublication.getDao().getByMap(filters);
		assertNotNull(listPubs);
		assertEquals(2, listPubs.size());

		filters.clear();
		filters.put("publicationId", 3);
		listPubs = MpListPublication.getDao().getByMap(filters);
		assertNotNull(listPubs);
		assertEquals(2, listPubs.size());

		filters.put("mpListId", 9);
		listPubs = MpListPublication.getDao().getByMap(filters);
		assertNotNull(listPubs);
		assertEquals(1, listPubs.size());
		assertMpListPublication2((MpListPublication) listPubs.get(0));
	}

	@Test
	public void addUpdateDeleteTest() {
		MpListPublication newListPub = new MpListPublication();
		newListPub.setMpPublication(MpPublication.getDao().getById(1));
		newListPub.setMpList(MpList.getDao().getById(2));
		MpListPublication.getDao().add(newListPub);
		
		MpListPublication persistedA = MpListPublication.getDao().getById(newListPub.getId());
		assertNotNull(persistedA);
		assertNotNull(persistedA.getId());
		assertDaoTestResults(MpListPublication.class, newListPub, persistedA, IGNORE_PROPERTIES, true, true);

		persistedA.setMpPublication(MpPublication.getDao().getById(3));
		persistedA.setMpList(MpList.getDao().getById(4));
		MpListPublication.getDao().update(persistedA);

		MpListPublication persistedC = MpListPublication.getDao().getById(newListPub.getId());
		assertNotNull(persistedC);
		assertNotNull(persistedC.getId());
		assertDaoTestResults(MpListPublication.class, persistedA, persistedC, IGNORE_PROPERTIES, true, true);

		MpListPublication.getDao().delete(persistedC);
		assertNull(MpListPublication.getDao().getById(newListPub.getId()));

		MpListPublication.getDao().deleteById(2);
		assertNull(MpListPublication.getDao().getById(2));

		MpListPublication.getDao().deleteByParent(1);
		Map<String, Object> filters = new HashMap<>();
		filters.put("publicationId", 1);
		List<MpListPublication> listPubs = MpListPublication.getDao().getByMap(filters);
		assertTrue(listPubs.isEmpty());
	}

	public static void assertMpListPublication1(MpListPublication listPub) {
		assertNotNull(listPub);
		assertEquals(1, listPub.getId().intValue());
		assertEquals(2, listPub.getMpPublication().getId().intValue());
		assertEquals(320, listPub.getMpList().getId().intValue());
	}

	public static void assertMpListPublication2(MpListPublication listPub) {
		assertNotNull(listPub);
		assertEquals(2, listPub.getId().intValue());
		assertEquals(3, listPub.getMpPublication().getId().intValue());
		assertEquals(9, listPub.getMpList().getId().intValue());
	}

}
