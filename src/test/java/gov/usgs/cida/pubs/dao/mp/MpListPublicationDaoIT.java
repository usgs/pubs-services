package gov.usgs.cida.pubs.dao.mp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, MpListPublicationDao.class, MpPublicationDao.class, MpListDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class MpListPublicationDaoIT extends BaseIT {

	public static final List<String> IGNORE_PROPERTIES = List.of("validationErrors", "valErrors");

	@Autowired
	MpListPublicationDao mpListPublicationDao;
	@Autowired
	MpListDao mpListDao;
	@Autowired
	MpPublicationDao mpPublicationDao;

	@Test
	public void getbyIdTests() {
		MpListPublication listPub = mpListPublicationDao.getById(1);
		assertMpListPublication1(listPub);
		listPub = mpListPublicationDao.getById("2");
		assertMpListPublication2(listPub);
	}

	@Test
	public void getByMapTests() {
		Map<String, Object> filters = new HashMap<>();
		filters.put("mpListId", 9);
		List<MpListPublication> listPubs = mpListPublicationDao.getByMap(filters);
		assertNotNull(listPubs);
		assertEquals(2, listPubs.size());

		filters.clear();
		filters.put("publicationId", 3);
		listPubs = mpListPublicationDao.getByMap(filters);
		assertNotNull(listPubs);
		assertEquals(2, listPubs.size());

		filters.put("mpListId", 9);
		listPubs = mpListPublicationDao.getByMap(filters);
		assertNotNull(listPubs);
		assertEquals(1, listPubs.size());
		assertMpListPublication2((MpListPublication) listPubs.get(0));
	}

	@Test
	public void addUpdateDeleteTest() {
		MpListPublication newListPub = new MpListPublication();
		newListPub.setMpPublication(mpPublicationDao.getById(1));
		newListPub.setMpList(mpListDao.getById(2));
		mpListPublicationDao.add(newListPub);
		
		MpListPublication persistedA = mpListPublicationDao.getById(newListPub.getId());
		assertNotNull(persistedA);
		assertNotNull(persistedA.getId());
		assertDaoTestResults(MpListPublication.class, newListPub, persistedA, IGNORE_PROPERTIES, true, true);

		persistedA.setMpPublication(mpPublicationDao.getById(3));
		persistedA.setMpList(mpListDao.getById(4));
		mpListPublicationDao.update(persistedA);

		MpListPublication persistedC = mpListPublicationDao.getById(newListPub.getId());
		assertNotNull(persistedC);
		assertNotNull(persistedC.getId());
		assertDaoTestResults(MpListPublication.class, persistedA, persistedC, IGNORE_PROPERTIES, true, true);

		mpListPublicationDao.delete(persistedC);
		assertNull(mpListPublicationDao.getById(newListPub.getId()));

		mpListPublicationDao.deleteById(2);
		assertNull(mpListPublicationDao.getById(2));

		mpListPublicationDao.deleteByParent(1);
		Map<String, Object> filters = new HashMap<>();
		filters.put("publicationId", 1);
		List<MpListPublication> listPubs = mpListPublicationDao.getByMap(filters);
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
