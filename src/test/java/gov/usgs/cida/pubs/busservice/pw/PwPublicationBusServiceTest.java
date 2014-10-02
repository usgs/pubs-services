package gov.usgs.cida.pubs.busservice.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class PwPublicationBusServiceTest extends BaseSpringDaoTest {

    private PwPublicationBusService busService;

    @Before
    public void initTest() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        busService = new PwPublicationBusService();
    }

    @Test
    public void getObjectTest() {
        busService.getObject(null);
        assertNull(busService.getObject(-1));
        PwPublication pub = busService.getObject(4);
        assertNotNull(pub);
        PwPublicationDaoTest.assertPwPub4(pub);
        PwPublicationDaoTest.assertPwPub4Children(pub);
    }

    @Test
    public void getObjectsTest() {
    	Map<String, Object> filters = new HashMap<>();
        busService.getObjects(null);
        busService.getObjects(filters);

    	filters.put("searchTerms", new String[]{"title"});
        List<PwPublication> pubs = busService.getObjects(filters);
        assertNotNull(pubs);
        assertEquals(1, pubs.size());
        PwPublicationDaoTest.assertPwPub4(pubs.get(0));
        PwPublicationDaoTest.assertPwPub4Children(pubs.get(0));
    }

    
    @Test
    public void getObjectCountTest() {
    	Map<String, Object> filters = new HashMap<>();
        busService.getObjects(null);
        busService.getObjects(filters);

        filters.put("searchTerms", new String[]{"title"});
        Integer cnt = busService.getObjectCount(filters);
        assertEquals(1, cnt.intValue());
        
        //TODO add in real filter tests
    }

    @Test
    public void getByIndexIdTest() {
        busService.getObject(null);
        assertNull(busService.getByIndexId("-1"));

        //We can get 4
        PwPublication pub = busService.getByIndexId("4");
        assertNotNull(pub);
        PwPublicationDaoTest.assertPwPub4(pub);
        PwPublicationDaoTest.assertPwPub4Children(pub);
        
        //5 is not ready to display
        pub = busService.getByIndexId("9");
        assertNull(pub);
        //but it really does exist
        assertNotNull(busService.getObject(5));
    }

}
