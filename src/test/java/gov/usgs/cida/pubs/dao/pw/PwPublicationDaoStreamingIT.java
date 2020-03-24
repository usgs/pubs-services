package gov.usgs.cida.pubs.dao.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.query.PwPublicationFilterParams;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.transform.PublicationColumnsHelper;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, PwPublicationDao.class, ConfigurationService.class,
			PwPublicationFilterParams.class})
public class PwPublicationDaoStreamingIT extends BaseIT {

	@Autowired
	private PwPublicationDao pwPublicationDao;
	@MockBean
	private ConfigurationService configurationService;

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
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationStream.xml")
	})
	public void getStreamByMapTest() {
		when(configurationService.getWarehouseEndpoint()).thenReturn("https://test.gov");
		TestResultHandler<PwPublication> handler = new TestResultHandler<>();
		PwPublicationFilterParams filters = new PwPublicationFilterParams();
		filters.setOrderBy("publication_year");
		pwPublicationDao.stream(PwPublicationDao.NS + PwPublicationDao.GET_STREAM_BY_MAP, filters, handler);
		List<Map<String, Object>> pubs = handler.results;
		assertNotNull(pubs);
		assertEquals(2, pubs.size());

		assertEquals(PublicationColumnsHelper.getMappings().keySet().size(), pubs.get(0).keySet().size());
		assertTrue(pubs.get(0).keySet().containsAll(PublicationColumnsHelper.getMappings().keySet()));
		assertEquals(PublicationColumnsHelper.getMappings().keySet().size(), pubs.get(1).keySet().size());
		assertTrue(pubs.get(1).keySet().containsAll(PublicationColumnsHelper.getMappings().keySet()));

		Map<String, Object> pub = pubs.get(0);
		assertEquals("https://test.gov/publication/4", pub.get("warehouse_url"));
		assertEquals(4, pub.get("publication_id"));
		assertEquals("4", pub.get("index_id"));
		assertEquals("Book chapter", pub.get("publication_type"));
		assertEquals("Abstract or summary", pub.get("publication_subtype"));
		assertEquals("display title", pub.get("display_title"));
		assertEquals("title", pub.get("title"));
		assertEquals("Resource Publication", pub.get("series_title"));
		assertEquals("series number", pub.get("series_number"));
		assertEquals("subseries title", pub.get("subseries_title"));
		assertEquals("chapter", pub.get("chapter"));
		assertEquals("subchapter", pub.get("subchapter"));
		assertEquals("soIssn", pub.get("online_issn"));
		assertEquals("spIssn", pub.get("print_issn"));
		assertEquals("isbn", pub.get("isbn"));
		assertEquals("doi", pub.get("doi_name"));
		assertEquals("edition4", pub.get("edition"));
		assertEquals("V98675", pub.get("volume"));
		assertEquals("I78123", pub.get("issue"));
		assertEquals("2014", pub.get("publication_year"));
		assertEquals("language", pub.get("language"));
		assertEquals("publisher", pub.get("publisher"));
		assertEquals("publisher loc", pub.get("publisher_location"));
		assertEquals("Affiliation Cost Center 1; Affiliation Cost Center 2", pub.get("cost_centers"));
		assertEquals("product description", pub.get("product_description"));
		assertEquals("Dataset", pub.get("larger_work_type"));
		assertEquals("Database-spatial", pub.get("larger_work_subtype"));
		assertEquals("Some Journal", pub.get("larger_work_title"));
		assertEquals("start", pub.get("start_page"));
		assertEquals("end", pub.get("end_page"));
		assertEquals(12, pub.get("number_of_pages"));
		assertEquals("comments on this4", pub.get("public_comments"));
		assertEquals("2014-07-22", pub.get("temporal_start"));
		assertEquals("2014-07-23", pub.get("temporal_end"));
		assertEquals("Conference Title", pub.get("conference_title"));
		assertEquals("A conference location", pub.get("conference_location"));
		assertEquals("A free form DATE", pub.get("conference_date"));
		assertEquals("USA", pub.get("country"));
		assertEquals("WI", pub.get("state"));
		assertEquals("DANE", pub.get("county"));
		assertEquals("MIDDLETON", pub.get("city"));
		assertEquals("On the moon", pub.get("other_geospatial"));
		assertEquals("NAD83", pub.get("datum"));
		assertEquals("EPSG:3857", pub.get("projection"));
		assertEquals(100, pub.get("scale"));
		assertEquals("N", pub.get("online_only"));
		assertEquals("Y", pub.get("additional_online_files"));
		assertEquals("ConFamily, ConGiven ConSuffix con@usgs.gov; US Geological Survey Ice Survey Team; outerfamily, outerGiven outerSuffix outer@gmail.com", pub.get("authors"));
		assertEquals("outerfamily, outerGiven outerSuffix outer@gmail.com; US Geological Survey Ice Survey Team; ConFamily, ConGiven ConSuffix con@usgs.gov", pub.get("editors"));
		assertEquals("outerfamily, outerGiven outerSuffix outer@gmail.com; ConFamily, ConGiven ConSuffix con@usgs.gov; US Geological Survey Ice Survey Team", pub.get("compilers"));
		assertEquals(Long.valueOf("3"), pub.get("number_of_links"));
		assertEquals("http://sciencebase.org", pub.get("sciencebase_uri"));
		assertEquals("http://doi.org", pub.get("chrs_doi"));
		assertEquals("http://dx.doi.org/10.1002/ece3.1813", pub.get("chrs_url"));
		assertEquals("Wiley-Blackwell", pub.get("chrs_publisher"));
		assertEquals("Beerens James M., Frederick Peter C., Noonburg Erik G., Gawlik Dale E.", pub.get("chrs_authors"));
		assertEquals("Ecology and Evolution", pub.get("chrs_journal_name"));
		assertEquals("11/19/2015", pub.get("chrs_publication_date"));
		assertEquals("11/21/2015", pub.get("chrs_audited_on"));
		assertEquals("11/19/2015", pub.get("chrs_pblclly_access_date"));

		Map<String, Object> pub1 = pubs.get(1);
		assertEquals(Long.valueOf(0), pub1.get("number_of_links"));

	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationStream.xml")
	})
	public void getStreamByMapSyntaxTest() {
		TestResultHandler<PwPublication> handler = new TestResultHandler<>();
		pwPublicationDao.stream(PwPublicationDao.NS + PwPublicationDao.GET_STREAM_BY_MAP, buildAllFilters(), handler);
		//TODO real filter testing, not just parsing 
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationOrderBy.xml")
	})
	public void streamOrderByTest() {
		TestResultHandler<PwPublication> handler = new TestResultHandler<>();
		PwPublicationFilterParams filters = new PwPublicationFilterParams();
		filters.setMimetype("tsv");
		pwPublicationDao.stream(PwPublicationDao.NS + PwPublicationDao.GET_STREAM_BY_MAP, filters, handler);
		List<Map<String, Object>> pubs = handler.results;
		assertEquals(24, pubs.size());
		assertEquals(340, pubs.get(0).get("publication_id"));
		assertEquals(100, pubs.get(1).get("publication_id"));
		assertEquals(360, pubs.get(2).get("publication_id"));
		assertEquals(120, pubs.get(3).get("publication_id"));
		assertEquals(380, pubs.get(4).get("publication_id"));
		assertEquals(140, pubs.get(5).get("publication_id"));
		assertEquals(400, pubs.get(6).get("publication_id"));
		assertEquals(160, pubs.get(7).get("publication_id"));
		assertEquals(420, pubs.get(8).get("publication_id"));
		assertEquals(180, pubs.get(9).get("publication_id"));
		assertEquals(440, pubs.get(10).get("publication_id"));
		assertEquals(200, pubs.get(11).get("publication_id"));
		assertEquals(460, pubs.get(12).get("publication_id"));
		assertEquals(220, pubs.get(13).get("publication_id"));
		assertEquals(480, pubs.get(14).get("publication_id"));
		assertEquals(240, pubs.get(15).get("publication_id"));
		assertEquals(500, pubs.get(16).get("publication_id"));
		assertEquals(260, pubs.get(17).get("publication_id"));
		assertEquals(520, pubs.get(18).get("publication_id"));
		assertEquals(280, pubs.get(19).get("publication_id"));
		assertEquals(540, pubs.get(20).get("publication_id"));
		assertEquals(300, pubs.get(21).get("publication_id"));
		assertEquals(560, pubs.get(22).get("publication_id"));
		assertEquals(320, pubs.get(23).get("publication_id"));
	}

	public PwPublicationFilterParams buildAllFilters() {
		PwPublicationFilterParams filters = new PwPublicationFilterParams();
		filters.setQ("test");
		filters.setG(SEARCH_POLYGON);
		filters.setTitle(new String[] {"test","this","is"});
		filters.setPubAbstract(new String[] {"test","this","is"});
		filters.setContributor(new String[] {"test"});
		filters.setProdId(new String[] {"1", "2", "3"});
		filters.setIndexId(new String[] {"test","this","is"});
		filters.setIpdsId(new String[] {"test","this","is"});
		filters.setYear(new String[] {"test","this","is"});
		filters.setStartYear("test");
		filters.setEndYear("test");
		filters.setContributingOffice(new String[] {"test","this","is"});
		filters.setTypeName(new String[] {"test","this","is"});
		filters.setSubtypeName(new String[] {"test","this","is"});
		filters.setSeriesName(new String[] {"test","this","is"});
		filters.setReportNumber(new String[] {"test","this","is"});
		filters.setLinkType(new String[] {"test","this","is"});
		filters.setNoLinkType(new String[] {"test","this","is"});
		filters.setPage_row_start("1");
		filters.setPage_number("1");
		filters.setPage_size("1");
		filters.setPub_x_days("1");
		filters.setPub_date_low("2001-01-01");
		filters.setPub_date_high("2001-01-01");
		filters.setMod_x_days("1");
		filters.setMod_date_low("2001-01-01");
		filters.setMod_date_high("2001-01-01");
		filters.setOrderBy("publication_year");
		filters.setChorus(true);
		return filters;
	}
}
