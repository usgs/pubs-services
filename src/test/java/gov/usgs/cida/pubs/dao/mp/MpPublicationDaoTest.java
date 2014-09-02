package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.Contact;
//import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class MpPublicationDaoTest extends BaseSpringDaoTest {

    //TODO editors, authors, links, & CostCenters in test.
    public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors", "costCenters", "authors", "editors", "links");

    @Test
    public void addAndGetByIds() {
        Integer pubId = MpPublication.getDao().getNewProdId();
        MpPublication newpubA = addAPub(pubId);
        MpPublication persistedA = MpPublication.getDao().getById(pubId);
        assertNotNull(persistedA);
        assertNotNull(persistedA.getId());
        assertDaoTestResults(MpPublication.class, newpubA, persistedA, IGNORE_PROPERTIES, true, true);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", new int[] { pubId });
        List<MpPublication> pubs = MpPublication.getDao().getByMap(params);
        assertTrue(pubs.size() > 0);
        MpPublication persistedB = pubs.get(0);
        assertNotNull(persistedB);
        assertNotNull(persistedB.getId());
        assertDaoTestResults(MpPublication.class, persistedA, persistedB, IGNORE_PROPERTIES, false, true);

        persistedA = updatePubProperties(persistedA);
        MpPublication.getDao().update(persistedA);

        MpPublication persistedC = MpPublication.getDao().getById(pubId);
        assertNotNull(persistedC);
        assertNotNull(persistedC.getId());
        assertDaoTestResults(MpPublication.class, persistedA, persistedC, IGNORE_PROPERTIES, true, true);

        MpPublication.getDao().delete(persistedC);
        assertNull(MpPublication.getDao().getById(pubId));
    }

    @Test
    public void copyFromPwTest() {
        MpPublication.getDao().copyFromPw(4);
        Publication<MpPublication> mpPub = MpPublication.getDao().getById(4);
        PwPublicationDaoTest.assertPwPub4(mpPub);
    }

    //TODO the following tests...
    //publishToPw(Integer prodID)

    public static void assertMpPub1(Publication<?> pub) {
        assertEquals(1, pub.getId().intValue());
        assertEquals("sir20145083", pub.getIndexId());
        assertEquals("2014-07-14T17:27:36.000", pub.getDisplayToPublicDate().toString());
        assertEquals(18, pub.getPublicationType().getId().intValue());
        assertEquals(5, pub.getPublicationSubtype().getId().intValue());
        assertEquals(334, pub.getSeriesTitle().getId().intValue());
        assertEquals("2014-5083", pub.getSeriesNumber());
        assertEquals("Climate Change Adaption Series", pub.getSubseriesTitle());
        assertNull(pub.getChapter());
        assertNull(pub.getSubchapterNumber());
        assertEquals("Monitoring recharge in areas of seasonally frozen ground in the Columbia Plateau and Snake River Plain, Idaho, Oregon, and Washington", pub.getTitle());
        assertNull(pub.getDocAbstract());
        assertEquals("English", pub.getLanguage());
        assertEquals("U.S. Geological Survey", pub.getPublisher());
        assertEquals("Reston, VA", pub.getPublisherLocation());
        assertEquals("10.3133/sir20145083", pub.getDoi());
        assertNull(pub.getIssn());
        assertNull(pub.getIsbn());
        assertEquals("Written in collaboration with the National Snow and Ice Data Center", pub.getCollaboration());
        assertNull(pub.getUsgsCitation());
        assertNull(pub.getContact());
        assertNull(pub.getProductDescription());
        assertNull(pub.getStartPage());
        assertNull(pub.getEndPage());
        assertNull(pub.getNumberOfPages());
        assertNull(pub.getOnlineOnly());
        assertNull(pub.getAdditionalOnlineFiles());
        assertEquals("2014-07-14", pub.getTemporalStart().toString());
        assertEquals("2014-07-20", pub.getTemporalEnd().toString());
        assertNull(pub.getNotes());
        assertNull(pub.getIpdsId());
        assertNull(pub.getIpdsReviewProcessState());
        assertNull(pub.getIpdsInternalId());
        assertEquals("2014", pub.getPublicationYear());
        assertEquals("Some Journal", pub.getLargerWorkTitle());
        assertEquals(23, pub.getLargerWorkType().getId().intValue());
        assertEquals("Conference Title", pub.getConferenceTitle());
        assertEquals("A free form DATE", pub.getConferenceDate());
        assertEquals("A conference location", pub.getConferenceLocation());
    }

    public static void assertMpPub1Children(Publication<?> pub) {
        assertEquals(2, pub.getAuthors().size());
        assertEquals(2, pub.getEditors().size());
        assertEquals(2, pub.getCostCenters().size());
        assertEquals(2, pub.getLinks().size());
    }

    public static void assertMpPub2(Publication<?> pub) {
        assertEquals(2, pub.getId().intValue());
        assertEquals("2", pub.getIndexId());
        assertEquals("2014-07-22T20:06:10.000", pub.getDisplayToPublicDate().toString());
        assertEquals(18, pub.getPublicationType().getId().intValue());
        assertEquals(5, pub.getPublicationSubtype().getId().intValue());
        assertEquals(331, pub.getSeriesTitle().getId().intValue());
        assertEquals("series number", pub.getSeriesNumber());
        assertEquals("subseries title", pub.getSubseriesTitle());
        assertEquals("chapter", pub.getChapter());
        assertEquals("subchapter title", pub.getSubchapterNumber());
        assertEquals("title", pub.getTitle());
        assertEquals("the abstract", pub.getDocAbstract());
        assertEquals("language", pub.getLanguage());
        assertEquals("publisher", pub.getPublisher());
        assertEquals("publicsher location", pub.getPublisherLocation());
        assertEquals("doi", pub.getDoi());
        assertEquals("issn", pub.getIssn());
        assertEquals("isbn", pub.getIsbn());
        assertEquals("collaboration", pub.getCollaboration());
        assertEquals("usgs citation", pub.getUsgsCitation());
        assertEquals(1, pub.getContact().getId().intValue());
        assertEquals("product description", pub.getProductDescription());
        assertEquals("start", pub.getStartPage());
        assertEquals("end", pub.getEndPage());
        assertEquals("1", pub.getNumberOfPages());
        assertEquals("N", pub.getOnlineOnly());
        assertEquals("Y", pub.getAdditionalOnlineFiles());
        assertEquals("2014-07-14", pub.getTemporalStart().toString());
        assertEquals("2014-07-20", pub.getTemporalEnd().toString());
        assertEquals("some notes", pub.getNotes());
        assertEquals("ipdsid", pub.getIpdsId());
        assertEquals("reviewprocessstate", pub.getIpdsReviewProcessState());
        assertEquals("123", pub.getIpdsInternalId());

        assertNull(pub.getPublicationYear());
        assertNull(pub.getLargerWorkTitle());
        assertNull(pub.getLargerWorkType());
        assertNull(pub.getConferenceTitle());
        assertNull(pub.getConferenceDate());
        assertNull(pub.getPublicationYear());
    }

    public static void assertMpPub2Children(Publication<?> pub) {
        assertEquals(1, pub.getAuthors().size());
        assertEquals(1, pub.getEditors().size());
        assertEquals(1, pub.getCostCenters().size());
        assertEquals(1, pub.getLinks().size());
    }

    public static MpPublication addAPub(final Integer pubId) {
        MpPublication newPub = new MpPublication();
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
        Contact contact = new Contact();
        contact.setId(1);
        newPub.setContact(contact);
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
        newPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
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
        MpPublication.getDao().add(newPub);
        return newPub;
    }

    public static MpPublication updatePubProperties(final MpPublication pubToUpdate) {
        MpPublication updatedPub = pubToUpdate;
        updatedPub.setIndexId("indexid2" + updatedPub.getId());
        updatedPub.setDisplayToPublicDate(new LocalDateTime(2012, 8, 23, 0, 0, 0));
        PublicationType pubType = new PublicationType();
        pubType.setId(23);
        updatedPub.setPublicationType(pubType);
        PublicationSubtype pubSubtype = new PublicationSubtype();
        pubSubtype.setId(22);
        updatedPub.setPublicationSubtype(pubSubtype);
        PublicationSeries pubSeries = new PublicationSeries();
        pubSeries.setId(501);
        updatedPub.setSeriesTitle(pubSeries);
        updatedPub.setSeriesNumber("Series Number2");
        updatedPub.setSubseriesTitle("subseries2");
        updatedPub.setChapter("chapter2");
        updatedPub.setSubchapterNumber("subchapter2");
        updatedPub.setTitle("Title2");
        updatedPub.setDocAbstract("Abstract Text2");
        updatedPub.setLanguage("Language2");
        updatedPub.setPublisher("Publisher2");
        updatedPub.setPublisherLocation("Publisher Location2");
        updatedPub.setDoi("doiname2");
        updatedPub.setIssn("inIssn2");
        updatedPub.setIsbn("inIsbn2");
        updatedPub.setCollaboration("collaboration2");
        updatedPub.setUsgsCitation("usgscitation2");
        Contact contact = new Contact();
        contact.setId(2);
        updatedPub.setContact(contact);
        updatedPub.setProductDescription("Prod Description2");
        updatedPub.setStartPage("inStartPage2");
        updatedPub.setEndPage("inEndPage2");
        updatedPub.setNumberOfPages("6");
        updatedPub.setOnlineOnly("2");
        updatedPub.setAdditionalOnlineFiles("2");
        updatedPub.setTemporalStart(new LocalDate(2010,10,10));
        updatedPub.setTemporalEnd(new LocalDate(2012,12,12));
        updatedPub.setNotes("notes2");
        updatedPub.setIpdsId("ipds_i2" + updatedPub.getId());
        updatedPub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
        updatedPub.setIpdsInternalId("122");
        updatedPub.setPublicationYear("2001");
        updatedPub.setLargerWorkTitle("Larger Work Title");
        PublicationType largerWorkType = new PublicationType();
        largerWorkType.setId(PublicationType.ARTICLE);
        updatedPub.setLargerWorkType(largerWorkType);
        updatedPub.setConferenceDate("a new free form date");
        updatedPub.setConferenceTitle("A title");
        updatedPub.setConferenceLocation("a conference location");
        return updatedPub;
    }
}
