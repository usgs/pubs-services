package gov.usgs.cida.pubs.dao;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.List;

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
import gov.usgs.cida.pubs.domain.query.DeletedPublicationFilter;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, DeletedPublicationDao.class})
public class DeletedPublicationDaoIT extends BaseIT {

	@Autowired
	DeletedPublicationDao deletedPublicationDao;

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getAllNoPaging() {
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByFilter(null);
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
		DeletedPublicationFilter filter = new DeletedPublicationFilter(1, 2, null);
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByFilter(filter);
		assertNotNull(deletedPublications);
		assertEquals(2, deletedPublications.size());
		assertThat(deletedPublications, 
				contains(DeletedPublicationHelper.SIX_SIX_TWO,
						DeletedPublicationHelper.SIX_SIX_ONE));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getAllPageThree() {
		DeletedPublicationFilter filter = new DeletedPublicationFilter(3, 2, null);
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByFilter(filter);
		assertNotNull(deletedPublications);
		assertEquals(1, deletedPublications.size());
		assertThat(deletedPublications, 
				contains(DeletedPublicationHelper.SIX_SIX_FIVE));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getSinceNoPaging() {
		DeletedPublicationFilter filter = new DeletedPublicationFilter(null, null,
				LocalDate.of(2017, 12, 31));
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByFilter(filter);
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
		DeletedPublicationFilter filter = new DeletedPublicationFilter(1, 1,
				LocalDate.of(2017, 12, 31));
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByFilter(filter);
		assertNotNull(deletedPublications);
		assertEquals(1, deletedPublications.size());
		assertThat(deletedPublications, 
				contains(DeletedPublicationHelper.SIX_SIX_TWO));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getSincePageThree() {
		DeletedPublicationFilter filter = new DeletedPublicationFilter(3, 1,
				LocalDate.of(2017, 12, 31));
		List<DeletedPublication> deletedPublications = deletedPublicationDao.getByFilter(filter);
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
		DeletedPublicationFilter filter = new DeletedPublicationFilter(1, 2, null);
		assertEquals(Integer.valueOf(5), deletedPublicationDao.getObjectCount(filter));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getCountSinceNoPaging() {
		DeletedPublicationFilter filter = new DeletedPublicationFilter(null, null,
				LocalDate.of(2017, 12, 31));
		assertEquals(Integer.valueOf(3), deletedPublicationDao.getObjectCount(filter));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	public void getCountSincePaging() {
		DeletedPublicationFilter filter = new DeletedPublicationFilter(1, 1,
				LocalDate.of(2017, 12, 31));
		assertEquals(Integer.valueOf(3), deletedPublicationDao.getObjectCount(filter));
	}

	@Test
	@DatabaseSetup("classpath:/testData/deletedPublication.xml")
	@ExpectedDatabase(
			value="classpath:/testResult/deletedPublication/deletedPublication.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=DeletedPublicationHelper.TABLE_NAME,
			query=DeletedPublicationHelper.QUERY_TEXT)
	public void add() {
		deletedPublicationDao.add(DeletedPublicationHelper.SIX_SIX_SEVEN);
	}
}
