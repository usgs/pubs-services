package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.Publication;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class PublicationDaoTest extends BaseSpringDaoTest {

    @Test
    public void getByIdTest() {
        //From warehouse
        Publication<?> pub4 = Publication.getPublicationDao().getById(4);
        PwPublicationDaoTest.assertPwPub4(pub4);

        //From mypubs
        Publication<?> pub2 = Publication.getPublicationDao().getById(2);
        MpPublicationDaoTest.assertPub2(pub2);
    }

    @Test
    public void getByMapTest() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("id", new int[] { 2 });
        Collection<Publication<?>> pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(2, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

        filters.clear();
        filters.put("indexId", new int[] { 4 });
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(4, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

        filters.clear();
        filters.put("ipdsId", new String[] {"IP-056327"});
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(3, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

        filters.clear();
        filters.put("id", new int[] { 4 });
        filters.put("indexId", new int[] { 4 });
        filters.put("ipdsId", new String[] {"ipds_id"});
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(4, ((Publication<?>)pubs.toArray()[0]).getId().intValue());
        
        filters.clear();
        filters.put("searchTerms", Arrays.asList("a").toArray());
        pubs = Publication.getPublicationDao().getByMap(filters);
        //TODO asserts
        
        //This only checks that the final query is syntactically correct, not that it is logically correct!
    	pubs = Publication.getPublicationDao().getByMap(buildAllParms());
    }

    @Test
    public void getObjectCountTest() {
        Map<String, Object> filters = new HashMap<>();
        Integer cnt = Publication.getPublicationDao().getObjectCount(null);
        assertEquals(6, cnt.intValue());

        filters.put("ipdsId", new String[] { "ipds_id" });
        cnt = Publication.getPublicationDao().getObjectCount(filters);
        assertEquals(1, cnt.intValue());
    }
    
    public static Map<String, Object> buildAllParms() {
    	Map<String, Object> rtn = new HashMap<>();
    	rtn.put("title", new String[]{"title1", "title2"});
    	rtn.put("abstract", new String[]{"abstract1", "abstractp"});
    	rtn.put("id", new String[]{"1", "2"});
    	rtn.put("indexId", new String[]{"indexId1", "indexId2"});
    	rtn.put("ipdsId", new String[]{"ipdsId1", "ipdsId2"});
    	rtn.put("year", new String[]{"year1", "year2"});
    	rtn.put("reportSeries", new String[]{"reportSeries1", "reportSeries2"});
    	rtn.put("reportNumber", new String[]{"reportNumber1", "reportNumber2"});
    	rtn.put("searchTerms", new String[]{"searchTerms1", "searchTerms2"});
    	rtn.put("listId", new String[]{"listId1", "listId2"});
    	rtn.put("contributor", "contributor1% and contributor2%");
    	rtn.put("contributingOffice", new String[]{"contributingOffice1", "contributingOffice2"});
    	
    	rtn.put("yearStart", "yearStart");
    	rtn.put("yearEnd", "yearEnd");
    	rtn.put("IPDS_ID", "IP-1234893");

    	rtn.put("pubXDays", "1");
    	rtn.put("pubDateLow", "2010-10-10");
    	rtn.put("pubDateHigh", "2012-12-12");
    	rtn.put("modXDays", "3");
    	rtn.put("modDateLow", "2010-10-10");
    	rtn.put("modDateHigh", "2012-12-12");
    	
    	return rtn;
    }

    @Test
    public void filterByIndexIdTest() {
        List<Publication<?>> pubs = Publication.getPublicationDao().filterByIndexId("sir");
        assertEquals(2, pubs.size());
        boolean got1 = false;
        boolean got6 = false;
        for (Publication<?> pub : pubs) {
        	if (1 == pub.getId()) {
        		got1 = true;
        	} else if (6 == pub.getId()) {
        		got6 = true;
        	} else {
        		fail("unexpected pub:" + pub.getId());
        	}
        }
        assertTrue(got1);
        assertTrue(got6);
    }

}
