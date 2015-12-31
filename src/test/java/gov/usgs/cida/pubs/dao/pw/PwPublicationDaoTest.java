package gov.usgs.cida.pubs.dao.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.dao.PublicationDaoTest;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

@Category(IntegrationTest.class)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class PwPublicationDaoTest extends BaseSpringTest {

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void getByIdTest() {
        PwPublication pub = PwPublication.getDao().getById(4);
        assertNotNull(pub);
        assertPwPub4(pub);
        assertPwPub4Children(pub);
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void getByIpdsIdTest() {
        PwPublication pub = PwPublication.getDao().getByIpdsId("ipds_id");
        assertNotNull(pub);
        assertPwPub4(pub);
        assertPwPub4Children(pub);
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void getByMapTest() {
    	//This test uses the VPD. If it fails because record counts are off:
    	// - No rows returned probably means the publication_index_00 table does not have the correct data in it.
    	//   see the <changeSet author="drsteini" id="testPublicationIndex" context="citrans" runOnChange="true"> in schema-pubs
    	// - Too many rows returned probably means the VPD got hosed.
    	//   see the changeLogVpd.xml file in schema-pubs
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("q", "title");
        List<PwPublication> pubs = PwPublication.getDao().getByMap(filters);
        assertNotNull(pubs);
        assertEquals(1, pubs.size());
        assertPwPub4(pubs.get(0));
        assertPwPub4Children(pubs.get(0));
        
        String[] polygon = {"-122.3876953125","37.80869897600677","-122.3876953125","36.75979104322286","-123.55224609375","36.75979104322286",
        		            "-123.55224609375","37.80869897600677","-122.3876953125","37.80869897600677"};
    	filters.put("g", polygon);
        pubs = PwPublication.getDao().getByMap(filters);
        
        
        //TODO add in real filter tests
    }
    
    @Test
   	@DatabaseSetup("classpath:/testData/publicationOrderBy.xml")
    public void getByMapOrderByTest() {
    	Map<String, Object> filters = new HashMap<>();
        List<PwPublication> pubs = PwPublication.getDao().getByMap(filters);
        assertEquals(24, pubs.size());
        assertEquals(340, pubs.get(0).getId().intValue());
        assertEquals(100, pubs.get(1).getId().intValue());
        assertEquals(360, pubs.get(2).getId().intValue());
        assertEquals(120, pubs.get(3).getId().intValue());
        assertEquals(380, pubs.get(4).getId().intValue());
        assertEquals(140, pubs.get(5).getId().intValue());
        assertEquals(400, pubs.get(6).getId().intValue());
        assertEquals(160, pubs.get(7).getId().intValue());
        assertEquals(420, pubs.get(8).getId().intValue());
        assertEquals(180, pubs.get(9).getId().intValue());
        assertEquals(440, pubs.get(10).getId().intValue());
        assertEquals(200, pubs.get(11).getId().intValue());
        assertEquals(460, pubs.get(12).getId().intValue());
        assertEquals(220, pubs.get(13).getId().intValue());
        assertEquals(480, pubs.get(14).getId().intValue());
        assertEquals(240, pubs.get(15).getId().intValue());
        assertEquals(500, pubs.get(16).getId().intValue());
        assertEquals(260, pubs.get(17).getId().intValue());
        assertEquals(520, pubs.get(18).getId().intValue());
        assertEquals(280, pubs.get(19).getId().intValue());
        assertEquals(540, pubs.get(20).getId().intValue());
        assertEquals(300, pubs.get(21).getId().intValue());
        assertEquals(560, pubs.get(22).getId().intValue());
        assertEquals(320, pubs.get(23).getId().intValue());
        
        filters.put("orderBy", "title");
        pubs = PwPublication.getDao().getByMap(filters);
        assertEquals(24, pubs.size());
        assertEquals(100, pubs.get(0).getId().intValue());
        assertEquals(140, pubs.get(1).getId().intValue());
        assertEquals(180, pubs.get(2).getId().intValue());
        assertEquals(220, pubs.get(3).getId().intValue());
        assertEquals(260, pubs.get(4).getId().intValue());
        assertEquals(300, pubs.get(5).getId().intValue());
        assertEquals(340, pubs.get(6).getId().intValue());
        assertEquals(380, pubs.get(7).getId().intValue());
        assertEquals(420, pubs.get(8).getId().intValue());
        assertEquals(460, pubs.get(9).getId().intValue());
        assertEquals(500, pubs.get(10).getId().intValue());
        assertEquals(540, pubs.get(11).getId().intValue());
        assertEquals(560, pubs.get(12).getId().intValue());
        assertEquals(520, pubs.get(13).getId().intValue());
        assertEquals(480, pubs.get(14).getId().intValue());
        assertEquals(440, pubs.get(15).getId().intValue());
        assertEquals(400, pubs.get(16).getId().intValue());
        assertEquals(360, pubs.get(17).getId().intValue());
        assertEquals(320, pubs.get(18).getId().intValue());
        assertEquals(280, pubs.get(19).getId().intValue());
        assertEquals(240, pubs.get(20).getId().intValue());
        assertEquals(200, pubs.get(21).getId().intValue());
        assertEquals(160, pubs.get(22).getId().intValue());
        assertEquals(120, pubs.get(23).getId().intValue());
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void getObjectCountTest() {
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("q", "title");
        Integer cnt = PwPublication.getDao().getObjectCount(filters);
        assertEquals(1, cnt.intValue());
        
        //TODO add in real filter tests
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void getByIndexIdTest() {
    	//This test uses the VPD. If it fails because record counts are off:
    	// - Not getting 4 probably means the publication_index_00 table does not have the correct data in it.
    	//   see the <changeSet author="drsteini" id="testPublicationIndex" context="citrans" runOnChange="true"> in schema-pubs
    	// - Getting 5 via getByIndexId means the VPD got hosed.
    	//   see the changeLogVpd.xml file in schema-pubs
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
        assertEquals("2014-07-22T17:09:24", pub.getDisplayToPublicDate().toString());
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
        assertEquals("contact for the pub4", pub.getContact());
        assertEquals("edition4", pub.getEdition());
        assertEquals("comments on this4", pub.getComments());
        assertEquals("contents, table of4", pub.getTableOfContents());
        assertEquals(5, pub.getPublishingServiceCenter().getId().intValue());
        assertEquals("2001-01-01", pub.getPublishedDate().toString());
        assertEquals(5, pub.getIsPartOf().getId().intValue());
        assertEquals(6, pub.getSupersededBy().getId().intValue());
        assertEquals("2004-04-04", pub.getRevisedDate().toString());
    }

    public static void assertPwPub4Children(Publication<?> pub) {
        assertEquals(2, pub.getContributors().size());
        assertEquals(1, pub.getCostCenters().size());
        assertEquals(1, pub.getLinks().size());
    }

    public static PwPublication buildAPub(final Integer pubId) {
    	return (PwPublication) PublicationDaoTest.buildAPub(new PwPublication(), pubId);
    }
    
}
