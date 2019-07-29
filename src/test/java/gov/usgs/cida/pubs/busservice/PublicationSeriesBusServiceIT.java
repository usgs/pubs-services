package gov.usgs.cida.pubs.busservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDaoIT;
import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSeriesTest;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LocalValidatorFactoryBean.class, PublicationSeries.class, PublicationSeriesDao.class,
			PublicationSubtype.class, PublicationSubtypeDao.class, Publication.class, PublicationDao.class})
public class PublicationSeriesBusServiceIT extends BaseIT {

	@Autowired
	public Validator validator;

	private PublicationSeriesBusService busService;

	@Before
	public void initTest() throws Exception {
		busService = new PublicationSeriesBusService(validator);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	public void getObjectTest() {
		busService.getObject(null);
		assertNull(busService.getObject(-1));
		assertNotNull(busService.getObject(1));
		PublicationSeries pubSeries = busService.getObject(1);
		PublicationSeriesDaoIT.assertPubSeries1(pubSeries);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	public void getObjectsTest() {
		busService.getObjects(null);
		Collection<PublicationSeries> pubSeries = busService.getObjects(new HashMap<String, Object>());
		assertEquals(PublicationSeriesDaoIT.pubSeriesCnt, pubSeries.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put("id", -1);
		pubSeries = busService.getObjects(filters);
		assertNotNull(pubSeries);
		assertEquals(0, pubSeries.size());

		filters.clear();
		filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, 5);
		pubSeries = busService.getObjects(filters);
		assertNotNull(pubSeries);
		assertEquals(9, pubSeries.size());
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml")
	})
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/add.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED, modifiers = IdModifier.class)
	public void createObjectTest() {
		busService.createObject(null);

		PublicationSeries pubSeries = busService.createObject(PublicationSeriesTest.buildAPubSeries(null));
		id = pubSeries.getId();
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/update.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void updateObjectTest() {
		busService.updateObject(null);
		busService.updateObject(new PublicationSeries());

		PublicationSeries pubSeries = PublicationSeriesDaoIT.update330Properties();
		busService.updateObject(pubSeries);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/delete.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteObjectTest() {
		busService.deleteObject(null);
		busService.deleteObject(-1);
		
		busService.deleteObject(1);
		busService.deleteObject(333);
		busService.deleteObject(3803);
		busService.deleteObject(3804);
		busService.deleteObject(3805);
	}

}
