package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class PublicationDaoTest extends BaseSpringTest {

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

    public static Publication<?> buildAPub(Publication<?> newPub, final Integer pubId) {
    	newPub.setIndexId("indexid" + pubId);
        newPub.setDisplayToPublicDate(LocalDateTime.of(2012, 8, 23, 0, 0, 0));
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
        newPub.setTemporalStart(LocalDate.of(2010,10,10));
        newPub.setTemporalEnd(LocalDate.of(2012,12,12));
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
        newPub.setPublishedDate(LocalDate.of(2002,2,2));
        Publication<?> po = new PwPublication();
        po.setId(1);
        newPub.setIsPartOf(po);
        Publication<?> sb = new PwPublication();
        sb.setId(2);
        newPub.setSupersededBy(sb);
        newPub.setRevisedDate(LocalDate.of(2003,3,3));

        return newPub;
    }
}
