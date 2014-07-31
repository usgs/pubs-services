package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
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
public class MpPublicationDaoTest extends BaseSpringTest {

    //TODO editors, authors, links, & CostCenters in test.
    private static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors", "costCenters", "authors", "editors", "links");

    @Test
    public void addAndGetByIds() {
        MpPublication newpubA = new MpPublication();
        newpubA.setIndexId("indexid");
        newpubA.setDisplayToPublicDate(new LocalDateTime(2012, 8, 23, 0, 0, 0));
        PublicationType pubType = new PublicationType();
        pubType.setId(PublicationType.REPORT);
        newpubA.setPublicationType(pubType);
        PublicationSubtype pubSubtype = new PublicationSubtype();
        pubSubtype.setId(5);
        newpubA.setPublicationSubtype(pubSubtype);
        PublicationSeries pubSeries = new PublicationSeries();
        pubSeries.setId(PublicationSeries.SIR);
        newpubA.setPublicationSeries(pubSeries);
        newpubA.setSeriesNumber("Series Number");
        newpubA.setSubseriesTitle("subseries");
        newpubA.setChapter("chapter");
        newpubA.setSubchapter("subchapter");
        newpubA.setTitle("Title");
        newpubA.setDocAbstract("Abstract Text");
        newpubA.setLanguage("Language");
        newpubA.setPublisher("Publisher");
        newpubA.setPublisherLocation("Publisher Location");
        newpubA.setDoiName("doiname");
        newpubA.setIssn("inIssn");
        newpubA.setIsbn("inIsbn");
        newpubA.setCollaboration("collaboration");
        newpubA.setUsgsCitation("usgscitation");
        Contact contact = new Contact();
        contact.setId(1);
        newpubA.setContact(contact);
        newpubA.setProductDescription("Prod Description");
        newpubA.setStartPage("inStartPage");
        newpubA.setEndPage("inEndPage");
        newpubA.setNumberOfPages("5");
        newpubA.setOnlineOnly("O");
        newpubA.setAdditionalOnlineFiles("A");
        newpubA.setTemporalStart(new LocalDate(2010,10,10));
        newpubA.setTemporalEnd(new LocalDate(2012,12,12));
        newpubA.setNotes("notes");
        newpubA.setIpdsId("ipds_id");
        newpubA.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
        newpubA.setIpdsInternalId("12");
        Integer pubId = MpPublication.getDao().getNewProdId();
        newpubA.setId(pubId);
        assertEquals(pubId, MpPublication.getDao().add(newpubA));
        MpPublication persistedA = MpPublication.getDao().getById(pubId);
        assertNotNull(persistedA);
        assertNotNull(persistedA.getId());
        assertDaoTestResults(MpPublication.class, newpubA, persistedA, IGNORE_PROPERTIES, true, true);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", newpubA.getId());
        List<MpPublication> pubs = MpPublication.getDao().getByMap(params);
        assertTrue(pubs.size() > 0);
        MpPublication persistedB = pubs.get(0);
        assertNotNull(persistedB);
        assertNotNull(persistedB.getId());
        assertDaoTestResults(MpPublication.class, persistedA, persistedB, IGNORE_PROPERTIES, false, true);


        persistedA.setIndexId("indexid2");
        persistedA.setDisplayToPublicDate(new LocalDateTime(2012, 8, 23, 0, 0, 0));
        PublicationType pubType2 = new PublicationType();
        pubType2.setId(23);
        persistedA.setPublicationType(pubType2);
        PublicationSubtype pubSubtype2 = new PublicationSubtype();
        pubSubtype2.setId(22);
        persistedA.setPublicationSubtype(pubSubtype2);
        PublicationSeries pubSeries2 = new PublicationSeries();
        pubSeries2.setId(501);
        persistedA.setPublicationSeries(pubSeries2);
        persistedA.setSeriesNumber("Series Number2");
        persistedA.setSubseriesTitle("subseries2");
        persistedA.setChapter("chapter2");
        persistedA.setSubchapter("subchapter2");
        persistedA.setTitle("Title2");
        persistedA.setDocAbstract("Abstract Text2");
        persistedA.setLanguage("Language2");
        persistedA.setPublisher("Publisher2");
        persistedA.setPublisherLocation("Publisher Location2");
        persistedA.setDoiName("doiname2");
        persistedA.setIssn("inIssn2");
        persistedA.setIsbn("inIsbn2");
        persistedA.setCollaboration("collaboration2");
        persistedA.setUsgsCitation("usgscitation2");
        Contact contact2 = new Contact();
        contact2.setId(2);
        persistedA.setContact(contact2);
        persistedA.setProductDescription("Prod Description2");
        persistedA.setStartPage("inStartPage2");
        persistedA.setEndPage("inEndPage2");
        persistedA.setNumberOfPages("6");
        persistedA.setOnlineOnly("2");
        persistedA.setAdditionalOnlineFiles("2");
        persistedA.setTemporalStart(new LocalDate(2010,10,10));
        persistedA.setTemporalEnd(new LocalDate(2012,12,12));
        persistedA.setNotes("notes2");
        persistedA.setIpdsId("ipds_id2");
        persistedA.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
        persistedA.setIpdsInternalId("122");
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

    public static void assertMpPub2(Publication<?> pub) {
        assertEquals(2, pub.getId().intValue());
        assertEquals("2", pub.getIndexId());
        assertEquals("2014-07-22T20:06:10.000", pub.getDisplayToPublicDate().toString());
        assertEquals(18, pub.getPublicationType().getId().intValue());
        assertEquals(5, pub.getPublicationSubtype().getId().intValue());
        assertEquals(331, pub.getPublicationSeries().getId().intValue());
        assertEquals("series number", pub.getSeriesNumber());
        assertEquals("subseries title", pub.getSubseriesTitle());
        assertEquals("chapter", pub.getChapter());
        assertEquals("subchapter title", pub.getSubchapter());
        assertEquals("title", pub.getTitle());
        assertEquals("abstract", pub.getDocAbstract());
        assertEquals("language", pub.getLanguage());
        assertEquals("publisher", pub.getPublisher());
        assertEquals("publicsher location", pub.getPublisherLocation());
        assertEquals("doi", pub.getDoiName());
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
        assertEquals("notes", pub.getNotes());
        assertEquals("ipdsid", pub.getIpdsId());
    }

    public static void assertMpPub2Children(Publication<?> pub) {
        assertEquals(1, pub.getAuthors().size());
        assertEquals(1, pub.getEditors().size());
        assertEquals(1, pub.getCostCenters().size());
        assertEquals(1, pub.getLinks().size());
    }

}
