package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSeriesTest;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.unique.UniqueKeyValidatorForPublicationSeriesTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class PublicationSeriesBusServiceTest extends BaseSpringTest {

	@Autowired
	public Validator validator;

	private PublicationSeriesBusService busService;
	private PublicationSubtype subtype;
	private Publication<?> pub;
	private PublicationSeries pubSeries;

	@Mock
	protected PublicationSubtypeDao publicationSubtypeDao;
	@Mock
	protected PublicationSeriesDao publicationSeriesDao;
	@Mock
	protected PublicationDao publicationDao;

	@Before
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		busService = new PublicationSeriesBusService(validator);

		pubSeries = PublicationSeriesTest.buildAPubSeries(null);
		pubSeries.setPublicationSeriesDao(publicationSeriesDao);

		subtype = new PublicationSubtype();
		subtype.setId(999);
		subtype.setPublicationSubtypeDao(publicationSubtypeDao);

		pub = new Publication<>();
		pub.setPublicationDao(publicationDao);
	}

	@Test
	public void createObjectNullTest() {
		PublicationSeries newPubSeries = busService.createObject(null);
		assertNull(newPubSeries);

		verify(publicationSubtypeDao, never()).getById(any(Integer.class));
		verify(publicationSeriesDao, never()).uniqueCheck(any(PublicationSeries.class));
		verify(publicationDao, never()).getObjectCount(anyMapOf(String.class, Object.class));
		verify(publicationSeriesDao, never()).add(any(PublicationSeries.class));
		verify(publicationSeriesDao, never()).getById(any(Integer.class));
	}

	@Test
	public void createObjectTest() {
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(new PublicationSubtype());
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(new HashMap<BigDecimal, Map<String, Object>>());

		when(publicationSeriesDao.add(any(PublicationSeries.class))).thenReturn(1);
		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(pubSeries);

		PublicationSeries newPubSeries = busService.createObject(pubSeries);
		assertEquals(pubSeries, newPubSeries);

		//Should be called by ParentExistsValidatorForPublicationSeries
		verify(publicationSubtypeDao).getById(any(Integer.class));
		//Should be called by UniqueKeyValidatorForPublicationSeries
		verify(publicationSeriesDao).uniqueCheck(any(PublicationSeries.class));
		//Should not be called - not doing a delete validation
		verify(publicationDao, never()).getObjectCount(anyMapOf(String.class, Object.class));
		//Should be called in PublicationSeriesBusService
		verify(publicationSeriesDao).add(any(PublicationSeries.class));
		//Should be called in PublicationSeriesBusService
		verify(publicationSeriesDao).getById(any(Integer.class));
	}

	@Test
	public void createObjectErrorsTest() {
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(null);
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(UniqueKeyValidatorForPublicationSeriesTest.allDup());

		when(publicationSeriesDao.add(any(PublicationSeries.class))).thenReturn(1);
		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(pubSeries);

		PublicationSeries newPubSeries = busService.createObject(pubSeries);
		assertEquals(6, newPubSeries.getValidationErrors().getValidationErrors().size());

		//Should be called by ParentExistsValidatorForPublicationSeries
		verify(publicationSubtypeDao).getById(any(Integer.class));
		//Should be called by UniqueKeyValidatorForPublicationSeries
		verify(publicationSeriesDao).uniqueCheck(any(PublicationSeries.class));
		//Should not be called - not doing a delete validation
		verify(publicationDao, never()).getObjectCount(anyMapOf(String.class, Object.class));
		//Should be not called in PublicationSeriesBusService when validation errors
		verify(publicationSeriesDao, never()).add(any(PublicationSeries.class));
		//Should be not called in PublicationSeriesBusService when validation errors
		verify(publicationSeriesDao, never()).getById(any(Integer.class));
	}

	@Test
	public void deleteObjectNullTest() {
		ValidationResults vr = busService.deleteObject(null);
		assertNull(vr);

		verify(publicationSeriesDao, never()).getById(any(Integer.class));
		verify(publicationSubtypeDao, never()).getById(any(Integer.class));
		verify(publicationSeriesDao, never()).uniqueCheck(any(PublicationSeries.class));
		verify(publicationDao, never()).getObjectCount(anyMapOf(String.class, Object.class));
		verify(publicationSeriesDao, never()).delete(any(PublicationSeries.class));
	}

	@Test
	public void deleteObjectTest() {
		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(pubSeries);
		when(publicationDao.getObjectCount(anyMapOf(String.class, Object.class))).thenReturn(0);
		pubSeries.setId(1);

		ValidationResults vr = busService.deleteObject(1);
		assertEquals(0, vr.getValidationErrors().size());

		//Should be called in PublicationSeriesBusService
		verify(publicationSeriesDao).getById(any(Integer.class));
		//Should not be called on a delete
		verify(publicationSubtypeDao, never()).getById(any(Integer.class));
		//Should not be called on a delete
		verify(publicationSeriesDao, never()).uniqueCheck(any(PublicationSeries.class));
		//Should be called in NoChildrenValidatorForPublicationSeries
		verify(publicationDao).getObjectCount(anyMapOf(String.class, Object.class));
		//Should be called in PublicationSeriesBusService
		verify(publicationSeriesDao).delete(any(PublicationSeries.class));
	}

	@Test
	public void deleteMissingObjectTest() {
		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(null);

		ValidationResults vr = busService.deleteObject(1);
		assertEquals(0, vr.getValidationErrors().size());

		//Should be called in PublicationSeriesBusService
		verify(publicationSeriesDao).getById(any(Integer.class));
		//Should not be called on a delete
		verify(publicationSubtypeDao, never()).getById(any(Integer.class));
		//Should not be called on a delete
		verify(publicationSeriesDao, never()).uniqueCheck(any(PublicationSeries.class));
		//Do not get here when object not found for delete
		verify(publicationDao, never()).getObjectCount(anyMapOf(String.class, Object.class));
		//Do not get here when object not found for delete
		verify(publicationSeriesDao, never()).delete(any(PublicationSeries.class));
	}

	@Test
	public void deleteObjectErrorsTest() {
		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(pubSeries);
		when(publicationDao.getObjectCount(anyMapOf(String.class, Object.class))).thenReturn(12);

		ValidationResults vr = busService.deleteObject(1);
		assertEquals(1, vr.getValidationErrors().size());

		//Should be called in PublicationSeriesBusService
		verify(publicationSeriesDao).getById(any(Integer.class));
		//Should not be called on a delete
		verify(publicationSubtypeDao, never()).getById(any(Integer.class));
		//Should not be called on a delete
		verify(publicationSeriesDao, never()).uniqueCheck(any(PublicationSeries.class));
		//Should be called in NoChildrenValidatorForPublicationSeries
		verify(publicationDao).getObjectCount(anyMapOf(String.class, Object.class));
		//Should not be called when validation errors
		verify(publicationSeriesDao, never()).delete(any(PublicationSeries.class));
	}

	@Test
	public void getObjectNullTest() {
		PublicationSeries readPubSeries = busService.getObject(null);
		assertNull(readPubSeries);

		verify(publicationSeriesDao, never()).getById(any(Integer.class));
	}

	@Test
	public void getObjectTest() {
		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(pubSeries);

		PublicationSeries readPubSeries = busService.getObject(1);
		assertEquals(pubSeries, readPubSeries);

		verify(publicationSeriesDao).getById(any(Integer.class));
	}

	@Test
	public void getObjectsTest() {
		when(publicationSeriesDao.getByMap(anyMapOf(String.class, Object.class))).thenReturn(new ArrayList<PublicationSeries>());

		List<PublicationSeries> listPubSeries = busService.getObjects(null);
		assertEquals(0, listPubSeries.size());

		verify(publicationSeriesDao).getByMap(anyMapOf(String.class, Object.class));
	}

	@Test
	public void getObjectCountTest() {
		when(publicationSeriesDao.getObjectCount(anyMapOf(String.class, Object.class))).thenReturn(10);

		assertEquals(10, busService.getObjectCount(null).intValue());

		verify(publicationSeriesDao).getObjectCount(anyMapOf(String.class, Object.class));
	}

	@Test
	public void updateObjectNullTest() {
		PublicationSeries updPubSeries = busService.updateObject(null);
		assertNull(updPubSeries);

		verify(publicationSubtypeDao, never()).getById(any(Integer.class));
		verify(publicationSeriesDao, never()).uniqueCheck(any(PublicationSeries.class));
		verify(publicationDao, never()).getObjectCount(anyMapOf(String.class, Object.class));
		verify(publicationSeriesDao, never()).update(any(PublicationSeries.class));
		verify(publicationSeriesDao, never()).getById(any(Integer.class));

		updPubSeries = busService.updateObject(pubSeries);
		assertEquals(pubSeries, updPubSeries);

		verify(publicationSubtypeDao, never()).getById(any(Integer.class));
		verify(publicationSeriesDao, never()).uniqueCheck(any(PublicationSeries.class));
		verify(publicationDao, never()).getObjectCount(anyMapOf(String.class, Object.class));
		verify(publicationSeriesDao, never()).update(any(PublicationSeries.class));
		verify(publicationSeriesDao, never()).getById(any(Integer.class));
	}

	@Test
	public void updateObjectTest() {
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(new PublicationSubtype());
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(new HashMap<BigDecimal, Map<String, Object>>());

		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(pubSeries);
		pubSeries.setId(1);

		PublicationSeries updPubSeries = busService.updateObject(pubSeries);
		assertEquals(0, updPubSeries.getValidationErrors().getValidationErrors().size());
		assertEquals(pubSeries, updPubSeries);

		//Should be called by ParentExistsValidatorForPublicationSeries
		verify(publicationSubtypeDao).getById(any(Integer.class));
		//Should be called by UniqueKeyValidatorForPublicationSeries
		verify(publicationSeriesDao).uniqueCheck(any(PublicationSeries.class));
		//Should not be called - not doing a delete validation
		verify(publicationDao, never()).getObjectCount(anyMapOf(String.class, Object.class));
		//Should be called in PublicationSeriesBusService
		verify(publicationSeriesDao).update(any(PublicationSeries.class));
		//Should be called in PublicationSeriesBusService
		verify(publicationSeriesDao).getById(any(Integer.class));
	}

	@Test
	public void updateObjectErrorsTest() {
		when(publicationSubtypeDao.getById(any(Integer.class))).thenReturn(null);
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(UniqueKeyValidatorForPublicationSeriesTest.allDup());

		when(publicationSeriesDao.getById(any(Integer.class))).thenReturn(pubSeries);
		pubSeries.setId(1);

		PublicationSeries updPubSeries = busService.updateObject(pubSeries);
		assertEquals(6, updPubSeries.getValidationErrors().getValidationErrors().size());

		//Should be called by ParentExistsValidatorForPublicationSeries
		verify(publicationSubtypeDao).getById(any(Integer.class));
		//Should be called by UniqueKeyValidatorForPublicationSeries
		verify(publicationSeriesDao).uniqueCheck(any(PublicationSeries.class));
		//Should not be called - not doing a delete validation
		verify(publicationDao, never()).getObjectCount(anyMapOf(String.class, Object.class));
		//Should be not called in PublicationSeriesBusService when validation errors
		verify(publicationSeriesDao, never()).update(any(PublicationSeries.class));
		//Should be not called in PublicationSeriesBusService when validation errors
		verify(publicationSeriesDao, never()).getById(any(Integer.class));
	}

}
