package gov.usgs.cida.pubs.dao.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

import org.junit.Test;

public class PwPublicationDaoTest extends BaseSpringDaoTest {

    @Test
    public void getByIdTest() {
        PwPublication pub = PwPublication.getDao().getById(4);
        assertNotNull(pub);
        assertPwPub4(pub);
        assertPwPub4Children(pub);
    }

    @Test
    public void getByMapTest() {
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("searchTerms", new String[]{"title"});
        List<PwPublication> pubs = PwPublication.getDao().getByMap(filters);
        assertNotNull(pubs);
        assertEquals(1, pubs.size());
        assertPwPub4(pubs.get(0));
        assertPwPub4Children(pubs.get(0));
        
        //TODO add in real filter tests
    }

    @Test
    public void getObjectCountTest() {
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("searchTerms", new String[]{"title"});
        Integer cnt = PwPublication.getDao().getObjectCount(filters);
        assertEquals(1, cnt.intValue());
        
        //TODO add in real filter tests
    }

    @Test
    public void getByIndexIdTest() {
    	//We can get 4
        PwPublication pub = PwPublication.getDao().getByIndexId("4");
        assertNotNull(pub);
        assertPwPub4(pub);
        assertPwPub4Children(pub);
        
        //5 is not ready to display
        pub = PwPublication.getDao().getByIndexId("9");
        assertNull(pub);
        //but it really does exist
        assertNotNull(PwPublication.getDao().getById(5));
    }

    public static void assertPwPub4(Publication<?> pub) {
        assertEquals(4, pub.getId().intValue());
        assertEquals("4", pub.getIndexId());
        assertEquals("2014-07-22T17:09:24.000", pub.getDisplayToPublicDate().toString());
        assertEquals(5, pub.getPublicationType().getId().intValue());
        assertEquals(18, pub.getPublicationSubtype().getId().intValue());
        assertEquals(332, pub.getSeriesTitle().getId().intValue());
        assertEquals("series number", pub.getSeriesNumber());
        assertEquals("subseries title", pub.getSubseriesTitle());
        assertEquals("chapter", pub.getChapter());
        assertEquals("subchapter", pub.getSubchapterNumber());
        assertEquals("title", pub.getTitle());
        assertEquals("abstract", pub.getDocAbstract());
        assertEquals("language", pub.getLanguage());
        assertEquals("publisher", pub.getPublisher());
        assertEquals("publisher loc", pub.getPublisherLocation());
        assertEquals("doi", pub.getDoi());
        assertEquals("issn", pub.getIssn());
        assertEquals("isbn", pub.getIsbn());
        assertEquals("collaborator", pub.getCollaboration());
        assertEquals("usgs citation", pub.getUsgsCitation());
        assertEquals(1, pub.getContact().getId().intValue());
        assertEquals("product description", pub.getProductDescription());
        assertEquals("start", pub.getStartPage());
        assertEquals("end", pub.getEndPage());
        assertEquals("12", pub.getNumberOfPages());
        assertEquals("N", pub.getOnlineOnly());
        assertEquals("Y", pub.getAdditionalOnlineFiles());
        assertEquals("2014-07-22", pub.getTemporalStart().toString());
        assertEquals("2014-07-23", pub.getTemporalEnd().toString());
        assertEquals("notes", pub.getNotes());
        assertEquals("ipds_id", pub.getIpdsId());
        assertEquals("100", pub.getScale());
        assertEquals("EPSG:3857", pub.getProjection());
        assertEquals("NAD83", pub.getDatum());
        assertEquals("USA", pub.getCountry());
        assertEquals("WI", pub.getState());
        assertEquals("DANE", pub.getCounty());
        assertEquals("MIDDLETON", pub.getCity());
        assertEquals("On the moon", pub.getOtherGeospatial());
        assertEquals("{ \"json\": \"extents\" }", pub.getGeographicExtents());
    }

    public static void assertPwPub4Children(Publication<?> pub) {
        assertEquals(1, pub.getAuthors().size());
        assertEquals(1, pub.getEditors().size());
        assertEquals(1, pub.getCostCenters().size());
        assertEquals(1, pub.getLinks().size());
    }

}
