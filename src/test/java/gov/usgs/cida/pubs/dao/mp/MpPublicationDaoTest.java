package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationTest;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;

@Category(IntegrationTest.class)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class MpPublicationDaoTest extends BaseSpringTest {

    //TODO contributors, links, & CostCenters in test.
    public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors", "costCenters", "contributors", "contributorsToMap", "links", "interactions", "sourceDatabase", "published");

    public static final String MPPUB1_INDEXID = "sir20145083";
    public static final String MPPUB1_LOCKEDBY = "drsteini";
    
    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
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
        Integer cnt = MpPublication.getDao().getObjectCount(params);
        assertEquals(cnt.intValue(), pubs.size());
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
   	@DatabaseSetup("classpath:/testData/mpPublicationOrderBy.xml")
    public void getByMapTest() {
    	Map<String, Object> filters = new HashMap<>();
        List<MpPublication> pubs = MpPublication.getDao().getByMap(filters);
        assertEquals(24, pubs.size());
        assertEquals(830, pubs.get(0).getId().intValue());
        assertEquals(810, pubs.get(1).getId().intValue());
        assertEquals(790, pubs.get(2).getId().intValue());
        assertEquals(770, pubs.get(3).getId().intValue());
        assertEquals(750, pubs.get(4).getId().intValue());
        assertEquals(730, pubs.get(5).getId().intValue());
        assertEquals(710, pubs.get(6).getId().intValue());
        assertEquals(690, pubs.get(7).getId().intValue());
        assertEquals(670, pubs.get(8).getId().intValue());
        assertEquals(650, pubs.get(9).getId().intValue());
        assertEquals(630, pubs.get(10).getId().intValue());
        assertEquals(610, pubs.get(11).getId().intValue());
        assertEquals(340, pubs.get(12).getId().intValue());
        assertEquals(100, pubs.get(13).getId().intValue());
        assertEquals(380, pubs.get(14).getId().intValue());
        assertEquals(140, pubs.get(15).getId().intValue());
        assertEquals(420, pubs.get(16).getId().intValue());
        assertEquals(180, pubs.get(17).getId().intValue());
        assertEquals(460, pubs.get(18).getId().intValue());
        assertEquals(220, pubs.get(19).getId().intValue());
        assertEquals(500, pubs.get(20).getId().intValue());
        assertEquals(260, pubs.get(21).getId().intValue());
        assertEquals(540, pubs.get(22).getId().intValue());
        assertEquals(300, pubs.get(23).getId().intValue());

        filters.put("orderBy", "title");
        pubs = MpPublication.getDao().getByMap(filters);
        assertEquals(24, pubs.size());
        assertEquals(100, pubs.get(0).getId().intValue());
        assertEquals(610, pubs.get(1).getId().intValue());
        assertEquals(650, pubs.get(2).getId().intValue());
        assertEquals(690, pubs.get(3).getId().intValue());
        assertEquals(730, pubs.get(4).getId().intValue());
        assertEquals(770, pubs.get(5).getId().intValue());
        assertEquals(810, pubs.get(6).getId().intValue());
        assertEquals(830, pubs.get(7).getId().intValue());
        assertEquals(790, pubs.get(8).getId().intValue());
        assertEquals(750, pubs.get(9).getId().intValue());
        assertEquals(710, pubs.get(10).getId().intValue());
        assertEquals(670, pubs.get(11).getId().intValue());
        assertEquals(630, pubs.get(12).getId().intValue());
        assertEquals(140, pubs.get(13).getId().intValue());
        assertEquals(180, pubs.get(14).getId().intValue());
        assertEquals(220, pubs.get(15).getId().intValue());
        assertEquals(260, pubs.get(16).getId().intValue());
        assertEquals(300, pubs.get(17).getId().intValue());
        assertEquals(340, pubs.get(18).getId().intValue());
        assertEquals(380, pubs.get(19).getId().intValue());
        assertEquals(420, pubs.get(20).getId().intValue());
        assertEquals(460, pubs.get(21).getId().intValue());
        assertEquals(500, pubs.get(22).getId().intValue());
        assertEquals(540, pubs.get(23).getId().intValue());
    }
    
    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void copyFromPwTest() {
        MpPublication.getDao().copyFromPw(4);
        Publication<MpPublication> mpPub = MpPublication.getDao().getById(4);
        PwPublicationTest.assertPwPub4(mpPub);
        assertTrue(((MpPublication) mpPub).isPublished());
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void lockPubTest() {
    	MpPublication.getDao().lockPub(3);
    	MpPublication mpPub = MpPublication.getDao().getById(3);
    	assertEquals(PubsConstants.ANONYMOUS_USER, mpPub.getLockUsername());
    }
    
    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void releaseLocksUserTest() {
    	MpPublication mpPub = addAPub(MpPublication.getDao().getNewProdId());
    	MpPublication.getDao().releaseLocksUser(PubsConstants.ANONYMOUS_USER);
    	mpPub = MpPublication.getDao().getById(mpPub.getId());
    	assertNull(mpPub.getLockUsername());
    	
    	//this one was also anonymous
    	mpPub = MpPublication.getDao().getById(2);
    	assertNull(mpPub.getLockUsername());
    	
    	//this should still be locked
    	mpPub = MpPublication.getDao().getById(1);
    	assertEquals("drsteini", mpPub.getLockUsername());

    	MpPublication.getDao().releaseLocksUser("drsteini");
    	mpPub = MpPublication.getDao().getById(1);
    	assertNull(mpPub.getLockUsername());
    }
    
    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void releaseLocksPubTest() {
    	MpPublication mpPub = addAPub(MpPublication.getDao().getNewProdId());
    	MpPublication.getDao().releaseLocksPub(mpPub.getId());
    	mpPub = MpPublication.getDao().getById(mpPub.getId());
    	assertNull(mpPub.getLockUsername());
    	
    	mpPub = MpPublication.getDao().getById(2);
    	assertEquals(PubsConstants.ANONYMOUS_USER,mpPub.getLockUsername());
    	
    	mpPub = MpPublication.getDao().getById(1);
    	assertEquals("drsteini", mpPub.getLockUsername());

    	MpPublication.getDao().releaseLocksPub(1);
    	mpPub = MpPublication.getDao().getById(1);
    	assertNull(mpPub.getLockUsername());
    }
    
    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void publishToPwTest() {
    	MpPublication.getDao().publishToPw(null);
    	MpPublication.getDao().publishToPw(-1);
    	
    	//this one should be a straight add.
    	MpPublication.getDao().publishToPw(1);
    	assertPwPub1(PwPublication.getDao().getById(1));

    	//this one should be a merge.
    	MpPublication.getDao().publishToPw(4);
    	PwPublicationTest.assertPwPub4(PwPublication.getDao().getById(4));
    }
    
    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void getByIndexIdTest() {
    	MpPublication pub = MpPublication.getDao().getByIndexId(MPPUB1_INDEXID);
    	assertMpPub1(pub, MPPUB1_LOCKEDBY);
    	
    	//This one is only in Publication, so we shouldn't frind it
    	assertNull(MpPublication.getDao().getByIndexId("9"));
    }

    public static void assertMpPub1(Publication<?> pub, String expectedLockUsername) {
    	assertTrue(pub instanceof MpPublication);
    	assertPub1(pub);
        assertEquals(expectedLockUsername, ((MpPublication) pub).getLockUsername());
    }

    public static void assertPwPub1(Publication<?> pub) {
       	assertTrue(pub instanceof PwPublication);
    	assertPub1(pub);
    }

    public static void assertPub1(Publication<?> pub) {
    	assertEquals(1, pub.getId().intValue());
        assertEquals("sir20145083", pub.getIndexId());
        assertEquals("2014-07-14T17:27:36", pub.getDisplayToPublicDate().toString());
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
        assertEquals("100", pub.getScale());
        assertEquals(23, pub.getLargerWorkSubtype().getId().intValue());
        assertEquals("EPSG:3857", pub.getProjection());
        assertEquals("NAD83", pub.getDatum());
        assertEquals("USA", pub.getCountry());
        assertEquals("WI", pub.getState());
        assertEquals("DANE", pub.getCounty());
        assertEquals("MIDDLETON", pub.getCity());
        assertEquals("On the moon", pub.getOtherGeospatial());
        assertEquals("VOL123", pub.getVolume());
        assertEquals("IS IIVI", pub.getIssue());
        assertEquals("{ \"json\": \"extents\" }", pub.getGeographicExtents());
        assertEquals("My Contact Info", pub.getContact());
        assertEquals("Edition X", pub.getEdition());
        assertEquals("just a little comment", pub.getComments());
        assertEquals("tbl contents", pub.getTableOfContents());
        assertEquals(6, pub.getPublishingServiceCenter().getId().intValue());
        assertEquals("2002-02-02", pub.getPublishedDate().toString());
        assertEquals("2003-03-03", pub.getRevisedDate().toString());
    }

    public static void assertMpPub1Children(Publication<?> pub) {
        assertEquals(4, pub.getContributors().size());
        assertEquals(2, pub.getCostCenters().size());
        assertEquals(2, pub.getLinks().size());
    }

    public static void assertMpPub2(Publication<?> pub, String expectedLockUsername) {
    	assertTrue(pub instanceof MpPublication);
    	assertPub2(pub);
        assertEquals(expectedLockUsername, ((MpPublication) pub).getLockUsername());
    }

    public static void assertPwPub2(Publication<?> pub) {
       	assertTrue(pub instanceof PwPublication);
    	assertPub2(pub);
    }

    public static void assertPub2(Publication<?> pub) {
        assertEquals(2, pub.getId().intValue());
        assertEquals("2", pub.getIndexId());
        assertEquals("2014-07-22T20:06:10", pub.getDisplayToPublicDate().toString());
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

        assertNull(pub.getScale());
        assertNull(pub.getProjection());
        assertNull(pub.getDatum());
        assertNull(pub.getCountry());
        assertNull(pub.getState());
        assertNull(pub.getCounty());
        assertNull(pub.getCity());
        assertNull(pub.getOtherGeospatial());
        assertNull(pub.getGeographicExtents());
        
        assertNull(pub.getContact());
        assertNull(pub.getEdition());
        assertNull(pub.getComments());
        assertNull(pub.getTableOfContents());
        
        assertEquals(7, pub.getPublishingServiceCenter().getId().intValue());
        assertEquals("2003-03-03", pub.getPublishedDate().toString());
        assertEquals(4, pub.getIsPartOf().getId().intValue());
        assertEquals(6, pub.getSupersededBy().getId().intValue());

    }

    public static void assertMpPub2Children(Publication<?> pub) {
        assertEquals(2, pub.getContributors().size());
        assertEquals(1, pub.getCostCenters().size());
        assertEquals(1, pub.getLinks().size());
    }

    public static MpPublication buildAPub(final Integer pubId) {
        MpPublication newPub = (MpPublication) PublicationTest.buildAPub(new MpPublication(), pubId);
        newPub.setLockUsername(PubsConstants.ANONYMOUS_USER);
        return newPub;
    }
    
    public static MpPublication addAPub(final Integer pubId) {
        MpPublication newPub = buildAPub(pubId);
        MpPublication.getDao().add(newPub);
    	MpPublication.getDao().lockPub(newPub.getId());
        return newPub;
    }

    public static MpPublication updatePubProperties(final MpPublication pubToUpdate) {
        MpPublication updatedPub = pubToUpdate;
        updatedPub.setIndexId("indexid2" + updatedPub.getId());
        updatedPub.setDisplayToPublicDate(LocalDateTime.of(2012, 8, 23, 0, 0, 0));
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
        updatedPub.setProductDescription("Prod Description2");
        updatedPub.setStartPage("inStartPage2");
        updatedPub.setEndPage("inEndPage2");
        updatedPub.setNumberOfPages("6");
        updatedPub.setOnlineOnly("2");
        updatedPub.setAdditionalOnlineFiles("2");
        updatedPub.setTemporalStart(LocalDate.of(2010,10,10));
        updatedPub.setTemporalEnd(LocalDate.of(2012,12,12));
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
        updatedPub.setConferenceTitle("A new title");
        updatedPub.setConferenceLocation("a new conference location");
        updatedPub.setVolume("VOL13");
        updatedPub.setIssue("ISIX");
        updatedPub.setContact("My Contact InfoU");
        updatedPub.setEdition("Edition XU");
        updatedPub.setComments("just a little commentU");
        updatedPub.setTableOfContents("tbl contentsU");
        updatedPub.setPublishedDate(LocalDate.of(2010,10,11));
        PublishingServiceCenter publishingServiceCenter = new PublishingServiceCenter();
        publishingServiceCenter.setId(2);
        updatedPub.setPublishingServiceCenter(publishingServiceCenter);
        Publication<?> po = new MpPublication();
        po.setId(2);
        updatedPub.setIsPartOf(po);
        Publication<?> sb = new MpPublication();
        sb.setId(1);
        updatedPub.setSupersededBy(sb);
        updatedPub.setRevisedDate(LocalDate.of(2007,7,7));
        return updatedPub;
    }
}
