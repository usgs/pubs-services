package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.Contact;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;

public class MpPublicationContributorDaoTest extends BaseSpringTest {

    private static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors");

    @Test
    public void addAndGetByIds() {
//        MpPublication newpubA = new MpPublication();
//        newpubA.setIndexId("indexid");
//        newpubA.setDisplayToPublicDate(new LocalDateTime(2012, 8, 23, 0, 0, 0));
//        PublicationType pubType = new PublicationType();
//        pubType.setId(18);
//        newpubA.setPublicationType(pubType);
//        PublicationSubtype pubSubtype = new PublicationSubtype();
//        pubSubtype.setId(5);
//        newpubA.setPublicationSubtype(pubSubtype);
//        PublicationSeries pubSeries = new PublicationSeries();
//        pubSeries.setId(334);
//        newpubA.setPublicationSeries(pubSeries);
//        newpubA.setSeriesNumber("Series Number");
//        newpubA.setSubseriesTitle("subseries");
//        newpubA.setChapter("chapter");
//        newpubA.setSubchapter("subchapter");
//        newpubA.setTitle("Title");
//        newpubA.setDocAbstract("Abstract Text");
//        newpubA.setLanguage("Language");
//        newpubA.setPublisher("Publisher");
//        newpubA.setPublisherLocation("Publisher Location");
//        newpubA.setDoiName("doiname");
//        newpubA.setIssn("inIssn");
//        newpubA.setIsbn("inIsbn");
//        newpubA.setCollaboration("collaboration");
//        newpubA.setUsgsCitation("usgscitation");
//        Contact contact = new Contact();
//        contact.setId(1);
//        newpubA.setContact(contact);
//        newpubA.setProductDescription("Prod Description");
//        newpubA.setStartPage("inStartPage");
//        newpubA.setEndPage("inEndPage");
//        newpubA.setNumberOfPages("5");
//        newpubA.setOnlineOnly("O");
//        newpubA.setAdditionalOnlineFiles("A");
//        newpubA.setTemporalStart(new LocalDate(2010,10,10));
//        newpubA.setTemporalEnd(new LocalDate(2012,12,12));
//        newpubA.setNotes("notes");
//        newpubA.setIpdsId("ipds_id");
//        newpubA.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
//        newpubA.setIpdsInternalId("12");
//        Integer pubId = MpPublication.getDao().getNewProdId();
//        newpubA.setId(pubId);
//        assertEquals(pubId, MpPublication.getDao().add(newpubA));
        Integer id = 1;
        MpPublicationContributor persistedA = MpPublicationContributor.getDao().getById(id);
        assertNotNull(persistedA);
        assertNotNull(persistedA.getId());
        assertEquals(id, persistedA.getId());
        assertEquals(id, persistedA.getPublicationId());
        assertNotNull(persistedA.getContributorType());
        assertEquals(id, persistedA.getContributorType().getId());
        assertEquals("author", persistedA.getContributorType().getName());
        assertEquals("Authors", persistedA.getContributorType().getTabName());
        assertNotNull(persistedA.getContributor());
        assertEquals(id, persistedA.getContributor().getId());
        assertEquals("ConFirst", persistedA.getContributor().getFirst());
        //TODO more property checking


//        assertDaoTestResults(MpPublication.class, newpubA, persistedA, IGNORE_PROPERTIES, true, true);
//
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("id", newpubA.getId());
//        List<MpPublication> pubs = MpPublication.getDao().getByMap(params);
//        assertTrue(pubs.size() > 0);
//        MpPublication persistedB = pubs.get(0);
//        assertNotNull(persistedB);
//        assertNotNull(persistedB.getId());
//        assertDaoTestResults(MpPublication.class, persistedA, persistedB, IGNORE_PROPERTIES, false, true);
//
//
//        persistedA.setIndexId("indexid2");
//        persistedA.setDisplayToPublicDate(new LocalDateTime(2012, 8, 23, 0, 0, 0));
//        PublicationType pubType2 = new PublicationType();
//        pubType2.setId(23);
//        persistedA.setPublicationType(pubType2);
//        PublicationSubtype pubSubtype2 = new PublicationSubtype();
//        pubSubtype2.setId(22);
//        persistedA.setPublicationSubtype(pubSubtype2);
//        PublicationSeries pubSeries2 = new PublicationSeries();
//        pubSeries2.setId(501);
//        persistedA.setPublicationSeries(pubSeries2);
//        persistedA.setSeriesNumber("Series Number2");
//        persistedA.setSubseriesTitle("subseries2");
//        persistedA.setChapter("chapter2");
//        persistedA.setSubchapter("subchapter2");
//        persistedA.setTitle("Title2");
//        persistedA.setDocAbstract("Abstract Text2");
//        persistedA.setLanguage("Language2");
//        persistedA.setPublisher("Publisher2");
//        persistedA.setPublisherLocation("Publisher Location2");
//        persistedA.setDoiName("doiname2");
//        persistedA.setIssn("inIssn2");
//        persistedA.setIsbn("inIsbn2");
//        persistedA.setCollaboration("collaboration2");
//        persistedA.setUsgsCitation("usgscitation2");
//        Contact contact2 = new Contact();
//        contact2.setId(2);
//        persistedA.setContact(contact2);
//        persistedA.setProductDescription("Prod Description2");
//        persistedA.setStartPage("inStartPage2");
//        persistedA.setEndPage("inEndPage2");
//        persistedA.setNumberOfPages("6");
//        persistedA.setOnlineOnly("2");
//        persistedA.setAdditionalOnlineFiles("2");
//        persistedA.setTemporalStart(new LocalDate(2010,10,10));
//        persistedA.setTemporalEnd(new LocalDate(2012,12,12));
//        persistedA.setNotes("notes2");
//        persistedA.setIpdsId("ipds_id2");
//        persistedA.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
//        persistedA.setIpdsInternalId("122");
//        MpPublication.getDao().update(persistedA);
//
//        MpPublication persistedC = MpPublication.getDao().getById(pubId);
//        assertNotNull(persistedC);
//        assertNotNull(persistedC.getId());
//        assertDaoTestResults(MpPublication.class, persistedA, persistedC, IGNORE_PROPERTIES, true, true);
//
//        MpPublication.getDao().delete(persistedC);
//        assertNull(MpPublication.getDao().getById(pubId));

        //TODO the following tests...
        //copyFromPw(Integer prodID)
        //publishToPw(Integer prodID)
    }
}
