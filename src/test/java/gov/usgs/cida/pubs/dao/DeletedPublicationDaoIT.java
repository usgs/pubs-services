package gov.usgs.cida.pubs.dao;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.domain.DeletedPublication;
import gov.usgs.cida.pubs.domain.DeletedPublicationHelper;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
classes={DbTestConfig.class, DeletedPublicationDao.class})
public class DeletedPublicationDaoIT extends BaseIT {

	@Autowired
	DeletedPublicationDao deletedPublicationDao;

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getAllNoPaging() {
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByMap(null);
		assertNotNull(deletedPublications);
		assertEquals(5, deletedPublications.size());
		assertThat(deletedPublications, 
				contains(DeletedPublicationHelper.SIX_SIX_TWO,
						DeletedPublicationHelper.SIX_SIX_ONE,
						DeletedPublicationHelper.SIX_SIX_THREE,
						DeletedPublicationHelper.SIX_SIX_FOUR,
						DeletedPublicationHelper.SIX_SIX_FIVE));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getAllPageOne() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(BaseDao.PAGE_SIZE, 2);
		filters.put(BaseDao.PAGE_ROW_START, 0);
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByMap(filters);
		assertNotNull(deletedPublications);
		assertEquals(2, deletedPublications.size());
		assertThat(deletedPublications, 
				contains(DeletedPublicationHelper.SIX_SIX_TWO,
						DeletedPublicationHelper.SIX_SIX_ONE));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getAllPageThree() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(BaseDao.PAGE_SIZE, 2);
		filters.put(BaseDao.PAGE_ROW_START, 4);
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByMap(filters);
		assertNotNull(deletedPublications);
		assertEquals(1, deletedPublications.size());
		assertThat(deletedPublications, 
				contains(DeletedPublicationHelper.SIX_SIX_FIVE));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getSinceNoPaging() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(DeletedPublicationDao.DELETED_SINCE, LocalDateTime.of(2017, 12, 31, 8, 10, 15));
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByMap(filters);
		assertNotNull(deletedPublications);
		assertEquals(3, deletedPublications.size());
		assertThat(deletedPublications, 
				contains(DeletedPublicationHelper.SIX_SIX_TWO,
						DeletedPublicationHelper.SIX_SIX_ONE,
						DeletedPublicationHelper.SIX_SIX_THREE));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getSincePageOne() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(DeletedPublicationDao.DELETED_SINCE, LocalDateTime.of(2017, 12, 31, 8, 10, 15));
		filters.put(BaseDao.PAGE_SIZE, 1);
		filters.put(BaseDao.PAGE_ROW_START, 0);
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByMap(filters);
		assertNotNull(deletedPublications);
		assertEquals(1, deletedPublications.size());
		assertThat(deletedPublications, 
				contains(DeletedPublicationHelper.SIX_SIX_TWO));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getSincePageThree() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(DeletedPublicationDao.DELETED_SINCE, LocalDateTime.of(2017, 12, 31, 8, 10, 15));
		filters.put(BaseDao.PAGE_SIZE, 1);
		filters.put(BaseDao.PAGE_ROW_START, 2);
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByMap(filters);
		assertNotNull(deletedPublications);
		assertEquals(1, deletedPublications.size());
		assertThat(deletedPublications, 
				contains(DeletedPublicationHelper.SIX_SIX_THREE));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getCountAllNoPaging() {
		assertEquals(Integer.valueOf(5), deletedPublicationDao.getObjectCount(null));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getCountAllPaging() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(BaseDao.PAGE_SIZE, 2);
		filters.put(BaseDao.PAGE_ROW_START, 0);
		assertEquals(Integer.valueOf(5), deletedPublicationDao.getObjectCount(filters));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getCountSinceNoPaging() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(DeletedPublicationDao.DELETED_SINCE, LocalDateTime.of(2017, 12, 31, 8, 10, 15));
		assertEquals(Integer.valueOf(3), deletedPublicationDao.getObjectCount(filters));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getCountSincePaging() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(DeletedPublicationDao.DELETED_SINCE, LocalDateTime.of(2017, 12, 31, 8, 10, 15));
		filters.put(BaseDao.PAGE_SIZE, 1);
		filters.put(BaseDao.PAGE_ROW_START, 2);
		assertEquals(Integer.valueOf(3), deletedPublicationDao.getObjectCount(filters));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	@ExpectedDatabase(
			value="xxxxxx",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=DeletedPublicationHelper.TABLE_NAME,
			query=DeletedPublicationHelper.QUERY_TEXT)
	public void add() {
		deletedPublicationDao.add(DeletedPublicationHelper.SIX_SIX_SEVEN);
	}
}
