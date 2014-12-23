package gov.usgs.cida.pubs.dao.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
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
        
        //TODO add in real filter tests
    }

    @Test
    public void getObjectCountTest() {
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("q", "title");
        Integer cnt = PwPublication.getDao().getObjectCount(filters);
        assertEquals(1, cnt.intValue());
        
        //TODO add in real filter tests
    }

    @Test
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
        assertEquals("date 1/1/1", pub.getPublishedDateStatement());
    }

    public static void assertPwPub4Children(Publication<?> pub) {
        assertEquals(2, pub.getContributors().size());
        assertEquals(1, pub.getCostCenters().size());
        assertEquals(1, pub.getLinks().size());
    }

    public static PwPublication buildAPub(final Integer pubId) {
    	PwPublication newPub = new PwPublication();
        newPub.setIndexId("indexid" + pubId);
        newPub.setDisplayToPublicDate(new LocalDateTime(2012, 8, 23, 0, 0, 0));
        PublicationType pubType = new PublicationType();
        pubType.setId(PublicationType.REPORT);
        newPub.setPublicationType(pubType);
        PublicationSubtype pubSubtype = new PublicationSubtype();
        pubSubtype.setId(5);
        newPub.setPublicationSubtype(pubSubtype);
        PublicationSeries pubSeries = new PublicationSeries();
        pubSeries.setId(PublicationSeries.SIR);
        newPub.setSeriesTitle(pubSeries);
        newPub.setSeriesNumber("Series Number");
        newPub.setSubseriesTitle("subseries");
        newPub.setChapter("chapter");
        newPub.setSubchapterNumber("subchapter");
        newPub.setTitle("Title");
        newPub.setDocAbstract("Abstract Text");
        newPub.setLanguage("Language");
        newPub.setPublisher("Publisher");
        newPub.setPublisherLocation("Publisher Location");
        newPub.setDoi("doiname");
        newPub.setIssn("inIssn");
        newPub.setIsbn("inIsbn");
        newPub.setCollaboration("collaboration");
        newPub.setUsgsCitation("usgscitation");
        newPub.setProductDescription("Prod Description");
        newPub.setStartPage("inStartPage");
        newPub.setEndPage("inEndPage");
        newPub.setNumberOfPages("5");
        newPub.setOnlineOnly("O");
        newPub.setAdditionalOnlineFiles("A");
        newPub.setTemporalStart(new LocalDate(2010,10,10));
        newPub.setTemporalEnd(new LocalDate(2012,12,12));
        newPub.setNotes("notes");
        newPub.setIpdsId("ipds_id" + pubId);
        newPub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
        newPub.setIpdsInternalId("12");
        newPub.setId(pubId);
        newPub.setPublicationYear("2001");
        newPub.setLargerWorkTitle("Larger Work Title");
        PublicationType largerWorkType = new PublicationType();
        largerWorkType.setId(PublicationType.ARTICLE);
        newPub.setLargerWorkType(largerWorkType);
        newPub.setConferenceDate("a new free form date");
        newPub.setConferenceTitle("A title");
        newPub.setConferenceLocation("a conference location");
        PublicationSubtype largerWorkSubype = new PublicationSubtype();
        largerWorkSubype.setId(23);
        newPub.setLargerWorkSubtype(largerWorkSubype);
        newPub.setScale("100");
        newPub.setProjection("EPSG:3857");
        newPub.setDatum("NAD83");
        newPub.setCountry("USA");
        newPub.setState("WI");
        newPub.setCounty("DANE");
        newPub.setCity("MIDDLETON");
        newPub.setOtherGeospatial("On the moon");
        newPub.setGeographicExtents("{ \"json\": \"extents\" }");
        newPub.setVolume("VOL12");
        newPub.setIssue("ISIV");
        newPub.setContact("My Contact Info");
        newPub.setEdition("Edition X");
        newPub.setComments("just a little comment");
        newPub.setTableOfContents("tbl contents");
        PublishingServiceCenter publishingServiceCenter = new PublishingServiceCenter();
        publishingServiceCenter.setId(6);
        newPub.setPublishingServiceCenter(publishingServiceCenter);
        newPub.setPublishedDateStatement("date 2/2/2");

        return newPub;
    }
    
}
