package gov.usgs.cida.pubs.dao.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
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
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class PwPublicationDaoStreamingTest extends BaseSpringTest {

	private class TestResultHandler<T> implements ResultHandler<T> {
		public ArrayList<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		@Override
		@SuppressWarnings("unchecked")
		public void handleResult(ResultContext<? extends T> context) {
			results.add((Map<String, Object>) context.getResultObject());
		}
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationStream.xml")
	})
	public void getStreamByMapTest() {
		//This test uses the VPD. If it fails because record counts are off:
		// - No rows returned probably means the publication_index_00 table does not have the correct data in it.
		//   see the <changeSet author="drsteini" id="testPublicationIndex" context="citrans" runOnChange="true"> in schema-pubs
		// - Too many rows returned probably means the VPD got hosed.
		//   see the changeLogVpd.xml file in schema-pubs
		TestResultHandler<PwPublication> handler = new TestResultHandler<>();
		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.ORDER_BY, "publication_year");
		PwPublication.getDao().stream(PwPublicationDao.NS + PwPublicationDao.GET_STREAM_BY_MAP, filters, handler);
		List<Map<String, Object>> pubs = handler.results;
		assertNotNull(pubs);
		assertEquals(2, pubs.size());

		assertEquals(PublicationColumns.getMappings().keySet().size(), pubs.get(0).keySet().size());
		assertTrue(pubs.get(0).keySet().containsAll(PublicationColumns.getMappings().keySet()));
		assertEquals(PublicationColumns.getMappings().keySet().size(), pubs.get(1).keySet().size());
		assertTrue(pubs.get(1).keySet().containsAll(PublicationColumns.getMappings().keySet()));

		Map<String, Object> pub = pubs.get(0);
		assertEquals(BigDecimal.valueOf(4), pub.get("PUBLICATION_ID"));
		assertEquals("4", pub.get("INDEX_ID"));
		assertEquals("Book chapter", pub.get("PUBLICATION_TYPE"));
		assertEquals("Abstract or summary", pub.get("PUBLICATION_SUBTYPE"));
        assertEquals("display title", pub.get("DISPLAY_TITLE"));
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
		assertEquals(BigDecimal.valueOf(2), pub.get("NUMBER_OF_LINKS"));
		assertEquals("http://sciencebase.org", pub.get("SCIENCEBASE_URI"));
		assertEquals("http://doi.org", pub.get("CHRS_DOI"));
		assertEquals("http://dx.doi.org/10.1002/ece3.1813", pub.get("CHRS_URL"));
		assertEquals("Wiley-Blackwell", pub.get("CHRS_PUBLISHER"));
		assertEquals("Beerens James M., Frederick Peter C., Noonburg Erik G., Gawlik Dale E.", pub.get("CHRS_AUTHORS"));
		assertEquals("Ecology and Evolution", pub.get("CHRS_JOURNAL_NAME"));
		assertEquals("11/19/2015", pub.get("CHRS_PUBLICATION_DATE"));
		assertEquals("11/21/2015", pub.get("CHRS_AUDITED_ON"));
		assertEquals("11/19/2015", pub.get("CHRS_PBLCLLY_ACCESS_DATE"));

		Map<String, Object> pub1 = pubs.get(1);
		assertEquals(BigDecimal.valueOf(0), pub1.get("NUMBER_OF_LINKS"));

	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationStream.xml")
	})
	public void getStreamByMapSyntaxTest() {
		TestResultHandler<PwPublication> handler = new TestResultHandler<>();
		PwPublication.getDao().stream(PwPublicationDao.NS + PwPublicationDao.GET_STREAM_BY_MAP, buildAllFilters(), handler);
		//TODO real filter testing, not just parsing 
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationOrderBy.xml")
	})
	public void streamOrderByTest() {
		TestResultHandler<PwPublication> handler = new TestResultHandler<>();
		PwPublication.getDao().stream(PwPublicationDao.NS + PwPublicationDao.GET_STREAM_BY_MAP, new HashMap<>(), handler);
		List<Map<String, Object>> pubs = handler.results;
		assertEquals(24, pubs.size());
		assertEquals("340", pubs.get(0).get("WAREHOUSE_URL"));
		assertEquals("100", pubs.get(1).get("WAREHOUSE_URL"));
		assertEquals("360", pubs.get(2).get("WAREHOUSE_URL"));
		assertEquals("120", pubs.get(3).get("WAREHOUSE_URL"));
		assertEquals("380", pubs.get(4).get("WAREHOUSE_URL"));
		assertEquals("140", pubs.get(5).get("WAREHOUSE_URL"));
		assertEquals("400", pubs.get(6).get("WAREHOUSE_URL"));
		assertEquals("160", pubs.get(7).get("WAREHOUSE_URL"));
		assertEquals("420", pubs.get(8).get("WAREHOUSE_URL"));
		assertEquals("180", pubs.get(9).get("WAREHOUSE_URL"));
		assertEquals("440", pubs.get(10).get("WAREHOUSE_URL"));
		assertEquals("200", pubs.get(11).get("WAREHOUSE_URL"));
		assertEquals("460", pubs.get(12).get("WAREHOUSE_URL"));
		assertEquals("220", pubs.get(13).get("WAREHOUSE_URL"));
		assertEquals("480", pubs.get(14).get("WAREHOUSE_URL"));
		assertEquals("240", pubs.get(15).get("WAREHOUSE_URL"));
		assertEquals("500", pubs.get(16).get("WAREHOUSE_URL"));
		assertEquals("260", pubs.get(17).get("WAREHOUSE_URL"));
		assertEquals("520", pubs.get(18).get("WAREHOUSE_URL"));
		assertEquals("280", pubs.get(19).get("WAREHOUSE_URL"));
		assertEquals("540", pubs.get(20).get("WAREHOUSE_URL"));
		assertEquals("300", pubs.get(21).get("WAREHOUSE_URL"));
		assertEquals("560", pubs.get(22).get("WAREHOUSE_URL"));
		assertEquals("320", pubs.get(23).get("WAREHOUSE_URL"));
	}

	public Map<String, Object> buildAllFilters() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.Q, "test");
		String[] polygon = {"-122.3876953125","37.80869897600677","-122.3876953125","36.75979104322286","-123.55224609375","36.75979104322286",
				"-123.55224609375","37.80869897600677","-122.3876953125","37.80869897600677"};
		filters.put(PwPublicationDao.G, polygon);
		filters.put(PublicationDao.TITLE, new String[] {"test","this","is"});
		filters.put(PublicationDao.PUB_ABSTRACT, new String[] {"test","this","is"});
		filters.put(PublicationDao.CONTRIBUTOR, "test");
		filters.put(PublicationDao.PROD_ID, new String[] {"1","2","3"});
		filters.put(PublicationDao.INDEX_ID, new String[] {"test","this","is"});
		filters.put(PublicationDao.IPDS_ID, new String[] {"test","this","is"});
		filters.put(PublicationDao.YEAR, new String[] {"test","this","is"});
		filters.put(PublicationDao.START_YEAR, "test");
		filters.put(PublicationDao.END_YEAR, "test");
		filters.put(PublicationDao.CONTRIBUTING_OFFICE, new String[] {"test","this","is"});
		filters.put(PublicationDao.TYPE_NAME, new String[] {"test","this","is"});
		filters.put(PublicationDao.SUBTYPE_NAME, new String[] {"test","this","is"});
		filters.put(PublicationDao.SERIES_NAME, new String[] {"test","this","is"});
		filters.put(PublicationDao.REPORT_NUMBER, new String[] {"test","this","is"});
		filters.put(PublicationDao.LINK_TYPE, new String[] {"test","this","is"});
		filters.put(BaseDao.PAGE_ROW_START, "1");
		filters.put(BaseDao.PAGE_NUMBER, "1");
		filters.put(BaseDao.PAGE_SIZE, "1");
		filters.put(PwPublicationDao.PUB_X_DAYS, "1");
		filters.put(PwPublicationDao.PUB_DATE_LOW, "2001-01-01");
		filters.put(PwPublicationDao.PUB_DATE_HIGH, "2001-01-01");
		filters.put(PwPublicationDao.MOD_X_DAYS, "1");
		filters.put(PwPublicationDao.MOD_DATE_LOW, "2001-01-01");
		filters.put(PwPublicationDao.MOD_DATE_HIGH, "2001-01-01");
		filters.put(PublicationDao.ORDER_BY, "publication_year");
		filters.put(PwPublicationDao.CHORUS, true);

		return filters;
	}
}
