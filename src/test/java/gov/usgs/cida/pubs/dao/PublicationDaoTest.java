package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDaoTest;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

import java.util.ArrayList;
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
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class PublicationDaoTest extends BaseSpringTest {

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void getByIdTest() {
        //From warehouse
        Publication<?> pub4 = Publication.getPublicationDao().getById(4);
        PwPublicationDaoTest.assertPwPub4(pub4);

        //From mypubs
        Publication<?> pub2 = Publication.getPublicationDao().getById(2);
        MpPublicationDaoTest.assertPub2(pub2);
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
    public void getByMapTest() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("id", new int[] { 2 });
        Collection<Publication<?>> pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(2, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

        filters.clear();
        filters.put("indexId", new int[] { 4 });
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(0, pubs.size());

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
        assertEquals(0, pubs.size());
        
        filters.clear();
        filters.put("searchTerms", Arrays.asList("a").toArray());
        pubs = Publication.getPublicationDao().getByMap(filters);

        
        filters.put("globalSearch", "true");
        filters.put("id", new int[] { 2 });
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(2, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

        filters.clear();
        filters.put("globalSearch", "true");
        filters.put("indexId", new int[] { 4 });
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(4, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

        filters.clear();
        filters.put("globalSearch", "true");
        filters.put("ipdsId", new String[] {"IP-056327"});
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(3, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

        filters.clear();
        filters.put("globalSearch", "true");
        filters.put("id", new int[] { 4 });
        filters.put("indexId", new int[] { 4 });
        filters.put("ipdsId", new String[] {"ipds_id"});
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(1, pubs.size());
        assertEquals(4, ((Publication<?>)pubs.toArray()[0]).getId().intValue());
        
        filters.clear();
        filters.put("globalSearch", "true");
        filters.put("searchTerms", Arrays.asList("a").toArray());
        pubs = Publication.getPublicationDao().getByMap(filters);

        //This only checks that the final query is syntactically correct, not that it is logically correct!
    	pubs = Publication.getPublicationDao().getByMap(buildAllParms());
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationOrderBy.xml"),
    	@DatabaseSetup("classpath:/testData/mpPublicationOrderBy.xml")
    })
    public void getByMapOrderByTest() {
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("globalSearch", "true");
    	List<Publication<?>> pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(36, pubs.size());
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
        assertEquals(360, pubs.get(14).getId().intValue());
        assertEquals(120, pubs.get(15).getId().intValue());
        assertEquals(380, pubs.get(16).getId().intValue());
        assertEquals(140, pubs.get(17).getId().intValue());
        assertEquals(400, pubs.get(18).getId().intValue());
        assertEquals(160, pubs.get(19).getId().intValue());
        assertEquals(420, pubs.get(20).getId().intValue());
        assertEquals(180, pubs.get(21).getId().intValue());
        assertEquals(440, pubs.get(22).getId().intValue());
        assertEquals(200, pubs.get(23).getId().intValue());
        assertEquals(460, pubs.get(24).getId().intValue());
        assertEquals(220, pubs.get(25).getId().intValue());
        assertEquals(480, pubs.get(26).getId().intValue());
        assertEquals(240, pubs.get(27).getId().intValue());
        assertEquals(500, pubs.get(28).getId().intValue());
        assertEquals(260, pubs.get(29).getId().intValue());
        assertEquals(520, pubs.get(30).getId().intValue());
        assertEquals(280, pubs.get(31).getId().intValue());
        assertEquals(540, pubs.get(32).getId().intValue());
        assertEquals(300, pubs.get(33).getId().intValue());
        assertEquals(560, pubs.get(34).getId().intValue());
        assertEquals(320, pubs.get(35).getId().intValue());

    	filters.put("orderBy", "mpNewest");
    	pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(36, pubs.size());
        assertEquals(770, pubs.get(0).getId().intValue());
        assertEquals(750, pubs.get(1).getId().intValue());
        assertEquals(730, pubs.get(2).getId().intValue());
        assertEquals(650, pubs.get(3).getId().intValue());
        assertEquals(630, pubs.get(4).getId().intValue());
        assertEquals(610, pubs.get(5).getId().intValue());
        assertEquals(830, pubs.get(6).getId().intValue());
        assertEquals(810, pubs.get(7).getId().intValue());
        assertEquals(790, pubs.get(8).getId().intValue());
        assertEquals(710, pubs.get(9).getId().intValue());
        assertEquals(690, pubs.get(10).getId().intValue());
        assertEquals(670, pubs.get(11).getId().intValue());
        assertEquals(100, pubs.get(12).getId().intValue());
        assertEquals(420, pubs.get(13).getId().intValue());
        assertEquals(260, pubs.get(14).getId().intValue());
        assertEquals(140, pubs.get(15).getId().intValue());
        assertEquals(460, pubs.get(16).getId().intValue());
        assertEquals(300, pubs.get(17).getId().intValue());
        assertEquals(340, pubs.get(18).getId().intValue());
        assertEquals(180, pubs.get(19).getId().intValue());
        assertEquals(500, pubs.get(20).getId().intValue());
        assertEquals(360, pubs.get(21).getId().intValue());
        assertEquals(120, pubs.get(22).getId().intValue());
        assertEquals(380, pubs.get(23).getId().intValue());
        assertEquals(400, pubs.get(24).getId().intValue());
        assertEquals(160, pubs.get(25).getId().intValue());
        assertEquals(440, pubs.get(26).getId().intValue());
        assertEquals(200, pubs.get(27).getId().intValue());
        assertEquals(220, pubs.get(28).getId().intValue());
        assertEquals(480, pubs.get(29).getId().intValue());
        assertEquals(240, pubs.get(30).getId().intValue());
        assertEquals(520, pubs.get(31).getId().intValue());
        assertEquals(280, pubs.get(32).getId().intValue());
        assertEquals(540, pubs.get(33).getId().intValue());
        assertEquals(560, pubs.get(34).getId().intValue());
        assertEquals(320, pubs.get(35).getId().intValue());
        
        filters.put("orderBy", "title");
        pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(36, pubs.size());
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
        assertEquals(560, pubs.get(24).getId().intValue());
        assertEquals(520, pubs.get(25).getId().intValue());
        assertEquals(480, pubs.get(26).getId().intValue());
        assertEquals(440, pubs.get(27).getId().intValue());
        assertEquals(400, pubs.get(28).getId().intValue());
        assertEquals(360, pubs.get(29).getId().intValue());
        assertEquals(320, pubs.get(30).getId().intValue());
        assertEquals(280, pubs.get(31).getId().intValue());
        assertEquals(240, pubs.get(32).getId().intValue());
        assertEquals(200, pubs.get(33).getId().intValue());
        assertEquals(160, pubs.get(34).getId().intValue());
        assertEquals(120, pubs.get(35).getId().intValue());
    }

    @Test
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationOrigin.xml"),
    	@DatabaseSetup("classpath:/testData/mpPublicationOrigin.xml")
    })
    public void getByMapOriginTest() {
    	Map<String, Object> filters = new HashMap<>();
    	//mypubs only
    	List<Publication<?>> pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(4, pubs.size());

        assertEquals(130, pubs.get(0).getId().intValue());
        assertEquals("mypubs", pubs.get(0).getSourceDatabase());
        assertFalse(pubs.get(0).isPublished());

        assertEquals(110, pubs.get(1).getId().intValue());
        assertEquals("mypubs", pubs.get(1).getSourceDatabase());
        assertFalse(pubs.get(1).isPublished());

        assertEquals(100, pubs.get(2).getId().intValue());
        assertEquals("mypubs", pubs.get(2).getSourceDatabase());
        assertTrue(pubs.get(2).isPublished());

        assertEquals(140, pubs.get(3).getId().intValue());
        assertEquals("mypubs", pubs.get(3).getSourceDatabase());
        assertTrue(pubs.get(3).isPublished());

    	filters.put("globalSearch", "true");
    	pubs = Publication.getPublicationDao().getByMap(filters);
        assertEquals(6, pubs.size());

        assertEquals(130, pubs.get(0).getId().intValue());
        assertEquals("mypubs", pubs.get(0).getSourceDatabase());
        assertFalse(pubs.get(0).isPublished());

        assertEquals(110, pubs.get(1).getId().intValue());
        assertEquals("mypubs", pubs.get(1).getSourceDatabase());
        assertFalse(pubs.get(1).isPublished());

        assertEquals(100, pubs.get(2).getId().intValue());
        assertEquals("mypubs", pubs.get(2).getSourceDatabase());
        assertTrue(pubs.get(2).isPublished());

        assertEquals(120, pubs.get(3).getId().intValue());
        assertEquals("pubs warehouse", pubs.get(3).getSourceDatabase());
        assertTrue(pubs.get(3).isPublished());

        assertEquals(140, pubs.get(4).getId().intValue());
        assertEquals("mypubs", pubs.get(4).getSourceDatabase());
        assertTrue(pubs.get(4).isPublished());

        assertEquals(160, pubs.get(5).getId().intValue());
        assertEquals("pubs warehouse", pubs.get(5).getSourceDatabase());
        assertTrue(pubs.get(5).isPublished());
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
        Integer cnt = Publication.getPublicationDao().getObjectCount(null);
        assertEquals(3, cnt.intValue());

        filters.put("globalSearch", "false");
        cnt = Publication.getPublicationDao().getObjectCount(filters);
        assertEquals(3, cnt.intValue());

        filters.put("globalSearch", "true");
        cnt = Publication.getPublicationDao().getObjectCount(filters);
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
    @DatabaseSetups({
    	@DatabaseSetup("classpath:/testData/publicationType.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
    	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
    	@DatabaseSetup("classpath:/testData/dataset.xml")
    })
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
        newPub.setDisplayToPublicDate(LocalDateTime.of(2012, 8, 23, 11, 29, 46));
        
        CostCenter costCenter = new CostCenter();
        costCenter.setId(2);
        costCenter.setText("Affiliation Cost Center 2");
        PublicationCostCenter<?> pcc = new PublicationCostCenter<>();
        pcc.setId(1);
        pcc.setCostCenter(costCenter);
        Collection<PublicationCostCenter<?>> costCenters = new ArrayList<>();
        costCenters.add(pcc);
        newPub.setCostCenters(costCenters);
        
        PublicationType pubType = new PublicationType();
        pubType.setId(PublicationType.REPORT);
        pubType.setText("Report");
        newPub.setPublicationType(pubType);
        
        PublicationSubtype pubSubtype = new PublicationSubtype();
        pubSubtype.setId(5);
        pubSubtype.setText("USGS Numbered Series");
        newPub.setPublicationSubtype(pubSubtype);
        
        PublicationSeries pubSeries = new PublicationSeries();
        pubSeries.setId(PublicationSeries.SIR);
        pubSeries.setText("Scientific Investigations Report");
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
        largerWorkType.setText("Article");
        newPub.setLargerWorkType(largerWorkType);
        
        newPub.setConferenceDate("a new free form date");
        newPub.setConferenceTitle("A title");
        newPub.setConferenceLocation("a conference location");
        
        PublicationSubtype largerWorkSubype = new PublicationSubtype();
        largerWorkSubype.setId(23);
        largerWorkSubype.setText("Database-spatial");
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
        newPub.setPublished(true);
        newPub.setSourceDatabase("mypubs");

        return newPub;
    }
}
