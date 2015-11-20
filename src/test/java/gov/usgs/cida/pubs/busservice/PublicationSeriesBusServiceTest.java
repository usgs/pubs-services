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
import org.junit.experimental.categories.Category;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDaoTest;
import gov.usgs.cida.pubs.domain.PublicationSeries;

@Category(IntegrationTest.class)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
@DatabaseTearDown("classpath:/testCleanup/clearAll.xml")
public class PublicationSeriesBusServiceTest extends BaseSpringTest {

	@Autowired
    public Validator validator;

	private PublicationSeriesBusService busService;

    @Before
    public void initTest() throws Exception {
        MockitoAnnotations.initMocks(this);
        busService = new PublicationSeriesBusService(validator);
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml")
    })
    public void getObjectTest() {
        busService.getObject(null);
        assertNull(busService.getObject(-1));
        assertNotNull(busService.getObject(1));
        PublicationSeries pubSeries = busService.getObject(1);
        PublicationSeriesDaoTest.assertPubSeries1(pubSeries);
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml")
    })
    public void getObjectsTest() {
        busService.getObjects(null);
        Collection<PublicationSeries> pubSeries = busService.getObjects(new HashMap<String, Object>());
        assertEquals(PublicationSeriesDaoTest.pubSeriesCnt, pubSeries.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", -1);
        pubSeries = busService.getObjects(filters);
        assertNotNull(pubSeries);
        assertEquals(0, pubSeries.size());

        filters.clear();
        filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, 5);
        pubSeries = busService.getObjects(filters);
        assertNotNull(pubSeries);
        assertEquals(8, pubSeries.size());
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml")
    })
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/add.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED, modifiers = IdModifier.class)
    public void createObjectTest() {
        //TODO both a good create and a create w/validation errors.
        //public MpPublication createObject(MpPublication object)
        busService.createObject(null);

        PublicationSeries pubSeries = busService.createObject(PublicationSeriesDaoTest.buildAPubSeries(null));
        id = pubSeries.getId();
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml")
    })
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/update.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void updateObjectTest() {
        busService.updateObject(null);
        busService.updateObject(new PublicationSeries());

        PublicationSeries pubSeries = PublicationSeriesDaoTest.update330Properties();
        busService.updateObject(pubSeries);
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml")
    })
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/delete.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
    public void deleteObjectTest() {
        //TODO a delete w/validation errors.
        //public ValidationResults deleteObject(MpPublication object)
        busService.deleteObject(null);
        busService.deleteObject(-1);
        
        busService.deleteObject(1);
        busService.deleteObject(333);
        busService.deleteObject(3803);
        busService.deleteObject(3804);
        busService.deleteObject(3805);
    }

}
