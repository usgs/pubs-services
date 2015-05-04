package gov.usgs.cida.pubs.dao.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.transform.PublicationColumns;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testData/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationStream.xml")
})
public class PwPublicationDaoStreamingTest extends BaseSpringTest {

	private class TestResultHandler implements ResultHandler {
		public ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		@Override
		@SuppressWarnings("unchecked")
		public void handleResult(ResultContext context) {
			results.add((Map<String, Object>) context.getResultObject());
		}
	}
	
    @Test
    public void getStreamByMapTest() {
    	//This test uses the VPD. If it fails because record counts are off:
    	// - No rows returned probably means the publication_index_00 table does not have the correct data in it.
    	//   see the <changeSet author="drsteini" id="testPublicationIndex" context="citrans" runOnChange="true"> in schema-pubs
    	// - Too many rows returned probably means the VPD got hosed.
    	//   see the changeLogVpd.xml file in schema-pubs
		TestResultHandler handler = new TestResultHandler();
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("orderBy", "publication_year");
    	PwPublication.getDao().stream(filters, handler);
        List<Map<String, Object>> pubs = handler.results;
        assertNotNull(pubs);
        assertEquals(2, pubs.size());
        
        assertEquals(PublicationColumns.getMappings().keySet().size(), pubs.get(0).keySet().size());
        assertTrue(pubs.get(0).keySet().containsAll(PublicationColumns.getMappings().keySet()));
        assertEquals(PublicationColumns.getMappings().keySet().size(), pubs.get(1).keySet().size());
        assertTrue(pubs.get(1).keySet().containsAll(PublicationColumns.getMappings().keySet()));
        
        Map<String, Object> pub = pubs.get(0);
		assertEquals("Book chapter", pub.get("PUBLICATION_TYPE"));
        assertEquals("Abstract or summary", pub.get("PUBLICATION_SUBTYPE"));
        assertEquals("title", pub.get("TITLE"));
        assertEquals("Resource Publication", pub.get("SERIES_TITLE"));
        assertEquals("series number", pub.get("SERIES_NUMBER"));
        assertEquals("subseries title", pub.get("SUBSERIES_TITLE"));
        assertEquals("chapter", pub.get("CHAPTER"));
        assertEquals("subchapter", pub.get("SUBCHAPTER"));
        assertEquals("soIssn", pub.get("ONLINE_ISSN"));
        assertEquals("spIssn", pub.get("PRINT_ISSN"));
        assertEquals("isbn", pub.get("ISBN"));
        assertEquals("doi", pub.get("DOI_NAME"));
        assertEquals("edition4", pub.get("EDITION"));
        assertEquals("V98675", pub.get("VOLUME"));
        assertEquals("I78123", pub.get("ISSUE"));
        assertEquals("2014", pub.get("PUBLICATION_YEAR"));
        assertEquals("language", pub.get("LANGUAGE"));
        assertEquals("publisher", pub.get("PUBLISHER"));
        assertEquals("publisher loc", pub.get("PUBLISHER_LOCATION"));
        assertEquals("Affiliation Cost Center 1; Affiliation Cost Center 2", pub.get("COST_CENTERS"));
        assertEquals("product description", pub.get("PRODUCT_DESCRIPTION"));
        assertEquals("Dataset", pub.get("LARGER_WORK_TYPE"));
        assertEquals("Database-spatial", pub.get("LARGER_WORK_SUBTYPE"));
        assertEquals("Some Journal", pub.get("LARGER_WORK_TITLE"));
        assertEquals("start", pub.get("START_PAGE"));
        assertEquals("end", pub.get("END_PAGE"));
        assertEquals(BigDecimal.valueOf(12), pub.get("NUMBER_OF_PAGES"));
        assertEquals("comments on this4", pub.get("PUBLIC_COMMENTS"));
        assertEquals("2014-07-22", pub.get("TEMPORAL_START"));
        assertEquals("2014-07-23", pub.get("TEMPORAL_END"));
        assertEquals("Conference Title", pub.get("CONFERENCE_TITLE"));
        assertEquals("A conference location", pub.get("CONFERENCE_LOCATION"));
        assertEquals("A free form DATE", pub.get("CONFERENCE_DATE"));
        assertEquals("USA", pub.get("COUNTRY"));
        assertEquals("WI", pub.get("STATE"));
        assertEquals("DANE", pub.get("COUNTY"));
        assertEquals("MIDDLETON", pub.get("CITY"));
        assertEquals("On the moon", pub.get("OTHER_GEOSPATIAL"));
        assertEquals("NAD83", pub.get("DATUM"));
        assertEquals("EPSG:3857", pub.get("PROJECTION"));
        assertEquals(BigDecimal.valueOf(100), pub.get("SCALE"));
        assertEquals("N", pub.get("ONLINE_ONLY"));
        assertEquals("Y", pub.get("ADDITIONAL_ONLINE_FILES"));
        assertEquals("ConFamily, ConGiven ConSuffix con@usgs.gov; US Geological Survey Ice Survey Team; outerfamily, outerGiven outerSuffix outer@gmail.com", pub.get("AUTHORS"));
        assertEquals("outerfamily, outerGiven outerSuffix outer@gmail.com; US Geological Survey Ice Survey Team; ConFamily, ConGiven ConSuffix con@usgs.gov", pub.get("EDITORS"));
        assertEquals("outerfamily, outerGiven outerSuffix outer@gmail.com; ConFamily, ConGiven ConSuffix con@usgs.gov; US Geological Survey Ice Survey Team", pub.get("COMPILERS"));
	}
	
    @Test
    public void getStreamByMapSyntaxTest() {
		TestResultHandler handler = new TestResultHandler();
    	PwPublication.getDao().stream(buildAllFilters(), handler);
    	//TODO real filter testing, not just parsing 
    }
    
    public Map<String, Object> buildAllFilters() {
    	Map<String, Object> filters = new HashMap<>();
    	filters.put("q", "test");
        String[] polygon = {"-122.3876953125","37.80869897600677","-122.3876953125","36.75979104322286","-123.55224609375","36.75979104322286",
	            "-123.55224609375","37.80869897600677","-122.3876953125","37.80869897600677"};
    	filters.put("g", polygon);
    	filters.put("title", new String[] {"test","this","is"});
    	filters.put("abstract", new String[] {"test","this","is"});
    	filters.put("contributor", "test");
    	filters.put("prodId", new String[] {"1","2","3"});
    	filters.put("indexId", new String[] {"test","this","is"});
    	filters.put("ipdsId", new String[] {"test","this","is"});
    	filters.put("year", new String[] {"test","this","is"});
    	filters.put("startYear", "test");
    	filters.put("endYear", "test");
    	filters.put("contributingOffice", new String[] {"test","this","is"});
    	filters.put("typeName", new String[] {"test","this","is"});
    	filters.put("subtypeName", new String[] {"test","this","is"});
    	filters.put("seriesName", new String[] {"test","this","is"});
    	filters.put("reportNumber", new String[] {"test","this","is"});
    	filters.put("pageRowStart", "1");
    	filters.put("pageNumber", "1");
    	filters.put("pageSize", "1");
    	filters.put("pubXDays", "1");
    	filters.put("pubDateLow", "2001-01-01");
    	filters.put("pubDateHigh", "2001-01-01");
    	filters.put("modXDays", "1");
    	filters.put("modDateLow", "2001-01-01");
    	filters.put("modDateHigh", "2001-01-01");
    	filters.put("orderBy", "publication_year");

    	return filters;
    }
}
