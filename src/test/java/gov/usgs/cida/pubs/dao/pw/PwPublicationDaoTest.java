package gov.usgs.cida.pubs.dao.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

import org.junit.Test;

public class PwPublicationDaoTest extends BaseSpringDaoTest {

    @Test
    public void getByIdTest() {
        PwPublication pub = PwPublication.getDao().getById(4);
        assertNotNull(pub);
        //TODO - These should probably be combined once the full copyFromPw logic is working.
        assertPwPub4(pub);
        assertPwPub4Children(pub);
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
        assertEquals(Integer.valueOf(100), pub.getScale());
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
