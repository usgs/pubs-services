package gov.usgs.cida.pubs.webservice;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.WebRequest;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;

public class MvcServiceTest {

	@MockBean
	WebRequest request;

	private class TestMvcService extends MvcService<MvcServiceTest> {

	}

	private TestMvcService testMvcService = new TestMvcService();

	@Test
	public void buildFiltersTest() {
		Boolean chorus = true;
		String[] contributingOffice = new String[]{"co1","co2"};
		String[] contributor = new String[]{"Rebecca B. Carvin", "c2"};
		String[] orcid = new String[]{"http://orcid.org/0000-0000-0000-0000", "http://orcid.org/1111-1111-1111-1111"};
		Boolean doi = true;
		String endYear = "2020";
		String g = "polygon((-122.3876953125 37.80869897600677,-122.3876953125 36.75979104322286,-123.55224609375 36.75979104322286," +
				"-123.55224609375 37.80869897600677,-122.3876953125 37.80869897600677))";
		String global = "false";
		String[] indexId = new String[]{"index1", "index2"};
		String[] ipdsId = new String[]{"ipds1", "ipds2"};
		String[] listId = new String[]{"list1", "list2", "3"};
		String modDateHigh = "modDayHi";
		String modDateLow = "modDayLo";
		String modXDays = "26";
		String orderBy = "ordered";
		Integer page_number = 3;
		Integer page_row_start = 43;
		Integer page_size = 18;
		String[] prodId = new String[]{"1234567","987654"};
		String[] pubAbstract = new String[]{"abstract1", "abstract2"};
		String pubDateHigh = "pubDateHi";
		String pubDateLow = "pubDateLo";
		String pubXDays = "23";
		String q = "turtles-loggerhead";
		String[] linkType = new String[]{"linkType1","linkType2"};
		String[] noLinkType = new String[]{"noLinkType1","noLinkType2"};
		String[] reportNumber = new String[]{"report1","report2"};
		String[] seriesName = new String[]{"series1","series2"};
		String startYear = "1859";
		String[] subtypeName = new String[]{"subtype1","subtype2"};
		String[] title = new String[]{"title1","title2"};
		String[] typeName = new String[]{"type1", "type2"};
		String[] year = new String[]{"year1","year2"};

		Map<String, Object> filters = testMvcService.buildFilters(chorus, contributingOffice, contributor, orcid, doi, endYear, g, global, indexId,
				ipdsId, listId, modDateHigh, modDateLow, modXDays, orderBy,
				String.valueOf(page_number), String.valueOf(page_row_start), String.valueOf(page_size), prodId, pubAbstract,
				pubDateHigh, pubDateLow, pubXDays, q, linkType, noLinkType, reportNumber, seriesName, startYear, subtypeName, title, typeName, year);

		assertTrue(filters.containsKey(PublicationDao.PUB_ABSTRACT ));
		assertEquals(pubAbstract, filters.get(PublicationDao.PUB_ABSTRACT));
		assertTrue(filters.containsKey(PwPublicationDao.CHORUS));
		assertEquals(chorus, filters.get(PwPublicationDao.CHORUS));
		assertTrue(filters.containsKey(PublicationDao.CONTRIBUTING_OFFICE));
		assertEquals(contributingOffice, filters.get(PublicationDao.CONTRIBUTING_OFFICE));
		assertTrue(filters.containsKey(PublicationDao.CONTRIBUTOR));
		assertEquals("rebecca:* & b.:* & carvin:* & c2:*", filters.get(PublicationDao.CONTRIBUTOR));
		assertTrue(filters.containsKey(PersonContributorDao.ORCID));
		assertEquals(orcid, filters.get(PersonContributorDao.ORCID));
		assertEquals(doi, filters.get(PublicationDao.DOI));
		assertTrue(filters.containsKey(PublicationDao.END_YEAR));
		assertEquals(endYear, filters.get(PublicationDao.END_YEAR));
		assertTrue(filters.containsKey(PwPublicationDao.G));
		assertEquals(BaseTest.SEARCH_POLYGON, filters.get(PwPublicationDao.G));
		assertTrue(filters.containsKey(MpPublicationDao.GLOBAL));
		assertEquals(global, filters.get(MpPublicationDao.GLOBAL));
		assertTrue(filters.containsKey(PublicationDao.INDEX_ID));
		assertEquals(indexId, filters.get(PublicationDao.INDEX_ID));
		assertTrue(filters.containsKey(PublicationDao.IPDS_ID));
		assertEquals(ipdsId, filters.get(PublicationDao.IPDS_ID));
		assertTrue(filters.containsKey(MpPublicationDao.LIST_ID));
//TODO		assertEquals(3, filters.get(MpPublicationDao.LIST_ID));
		assertTrue(filters.containsKey(PwPublicationDao.MOD_DATE_HIGH));
		assertEquals(modDateHigh, filters.get(PwPublicationDao.MOD_DATE_HIGH));
		assertTrue(filters.containsKey(PwPublicationDao.MOD_DATE_LOW));
		assertEquals(modDateLow, filters.get(PwPublicationDao.MOD_DATE_LOW));
		assertTrue(filters.containsKey(PwPublicationDao.MOD_X_DAYS));
		assertEquals(modXDays, filters.get(PwPublicationDao.MOD_X_DAYS));
		assertTrue(filters.containsKey(PublicationDao.ORDER_BY));
		assertEquals(orderBy, filters.get(PublicationDao.ORDER_BY));
		assertTrue(filters.containsKey(BaseDao.PAGE_NUMBER));
		assertEquals(page_number, filters.get(BaseDao.PAGE_NUMBER));
		assertTrue(filters.containsKey(PublicationDao.PAGE_ROW_START));
		assertEquals(page_row_start, filters.get(PublicationDao.PAGE_ROW_START));
		assertTrue(filters.containsKey(PublicationDao.PAGE_SIZE));
		assertEquals(page_size, filters.get(PublicationDao.PAGE_SIZE));
		assertTrue(filters.containsKey(PublicationDao.PROD_ID));
		assertEquals(prodId, filters.get(PublicationDao.PROD_ID));
		assertTrue(filters.containsKey(PwPublicationDao.PUB_DATE_HIGH));
		assertEquals(pubDateHigh, filters.get(PwPublicationDao.PUB_DATE_HIGH));
		assertTrue(filters.containsKey(PwPublicationDao.PUB_DATE_LOW));
		assertEquals(pubDateLow, filters.get(PwPublicationDao.PUB_DATE_LOW));
		assertTrue(filters.containsKey(PwPublicationDao.PUB_X_DAYS));
		assertEquals(pubXDays, filters.get(PwPublicationDao.PUB_X_DAYS));
		assertTrue(filters.containsKey(PublicationDao.Q));
		assertEquals("turtles:* & loggerhead:*", filters.get(PublicationDao.Q));
		assertTrue(filters.containsKey(MpPublicationDao.SEARCH_TERMS));
		assertArrayEquals(new String[]{"turtles","loggerhead"}, (Object[])filters.get(MpPublicationDao.SEARCH_TERMS));
		assertTrue(filters.containsKey(PublicationDao.LINK_TYPE));
		assertEquals(linkType, filters.get(PublicationDao.LINK_TYPE));
		assertTrue(filters.containsKey(PublicationDao.NO_LINK_TYPE));
		assertEquals(noLinkType, filters.get(PublicationDao.NO_LINK_TYPE));
		assertTrue(filters.containsKey(PublicationDao.REPORT_NUMBER));
		assertEquals(reportNumber, filters.get(PublicationDao.REPORT_NUMBER));
		assertTrue(filters.containsKey(PublicationDao.SERIES_NAME));
		assertEquals(seriesName, filters.get(PublicationDao.SERIES_NAME));
		assertTrue(filters.containsKey(PublicationDao.START_YEAR));
		assertEquals(startYear, filters.get(PublicationDao.START_YEAR));
		assertTrue(filters.containsKey(PublicationDao.SUBTYPE_NAME));
		assertEquals(subtypeName, filters.get(PublicationDao.SUBTYPE_NAME));
		assertTrue(filters.containsKey(PublicationDao.TITLE));
		assertEquals(title, filters.get(PublicationDao.TITLE));
		assertTrue(filters.containsKey(PublicationDao.TYPE_NAME));
		assertEquals(typeName, filters.get(PublicationDao.TYPE_NAME));
		assertTrue(filters.containsKey(PublicationDao.YEAR));
		assertEquals(year, filters.get(PublicationDao.YEAR));
	}

	@Test
	public void buildFiltersBadGTest() {
		Map<String, Object> filters = testMvcService.buildFilters(null, null, null, null, null, null, "abc", null, null,
				null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null);
		assertFalse(filters.containsKey(PwPublicationDao.G));
	}

	@Test
	public void buildPagingTest() {
		Map<String, Object> filters = testMvcService.buildPaging(null, null, null);
		assertTrue(filters.containsKey(BaseDao.PAGE_ROW_START));
		assertTrue(filters.containsKey(BaseDao.PAGE_SIZE));
		assertTrue(filters.containsKey(BaseDao.PAGE_NUMBER));
		assertEquals(0, filters.get(BaseDao.PAGE_ROW_START));
		assertEquals(15, filters.get(BaseDao.PAGE_SIZE));
		assertNull(filters.get(BaseDao.PAGE_NUMBER));

		filters = testMvcService.buildPaging("6", "18", null);
		assertTrue(filters.containsKey(BaseDao.PAGE_ROW_START));
		assertTrue(filters.containsKey(BaseDao.PAGE_SIZE));
		assertTrue(filters.containsKey(BaseDao.PAGE_NUMBER));
		assertEquals(6, filters.get(BaseDao.PAGE_ROW_START));
		assertEquals(18, filters.get(BaseDao.PAGE_SIZE));
		assertNull(filters.get(BaseDao.PAGE_NUMBER));

		filters = testMvcService.buildPaging(null, null, "89");
		assertTrue(filters.containsKey(BaseDao.PAGE_ROW_START));
		assertTrue(filters.containsKey(BaseDao.PAGE_SIZE));
		assertTrue(filters.containsKey(BaseDao.PAGE_NUMBER));
		assertEquals(2200, filters.get(BaseDao.PAGE_ROW_START));
		assertEquals(25, filters.get(BaseDao.PAGE_SIZE));
		assertEquals(89, filters.get(BaseDao.PAGE_NUMBER));

		filters = testMvcService.buildPaging("2200", "3", "32");
		assertTrue(filters.containsKey(BaseDao.PAGE_ROW_START));
		assertTrue(filters.containsKey(BaseDao.PAGE_SIZE));
		assertTrue(filters.containsKey(BaseDao.PAGE_NUMBER));
		assertEquals(93, filters.get(BaseDao.PAGE_ROW_START));
		assertEquals(3, filters.get(BaseDao.PAGE_SIZE));
		assertEquals(32, filters.get(BaseDao.PAGE_NUMBER));
	}

	@Test
	public void buildQTest() {
		List<String> list = new LinkedList<>();
		assertNull(testMvcService.buildQ(null));
		assertNull(testMvcService.buildQ(list));
		
		list.add("test");
		assertEquals("test:*", testMvcService.buildQ(list));
		
		list.add("turtles");
		list.add(" ");
		list.add("");
		list.add(null);
		list.add("loggerhead");
		list.add("within");
		assertEquals("test:* & turtles:* & loggerhead:* & within:*",  testMvcService.buildQ(list));
	}

	@Test
	public void buildSearchTermsTest() {
		//A null value should return a null
		assertNull(testMvcService.buildSearchTerms(null));

		//An empty value should return a null
		assertNull(testMvcService.buildSearchTerms(new LinkedList<String>()));
		assertNull(testMvcService.buildSearchTerms(Arrays.asList(new String[]{"   ","  "})));

		String[] searchTerms = testMvcService.buildSearchTerms(Arrays.asList(new String[]{"turtles","loggerhead",""," "}));
		assertEquals(2, searchTerms.length);
		assertEquals("turtles", searchTerms[0].toString());
		assertEquals("loggerhead", searchTerms[1].toString());
	}

	@Test
	public void configureContributorFilterTest() {
		//A null value should return a null
		assertNull(testMvcService.configureContributorFilter(null));
		assertNull(testMvcService.configureContributorFilter(new String[0]));

		//An empty value should return a null
		assertNull(testMvcService.configureContributorFilter(new String[]{"", null}));

		assertEquals(".:*", testMvcService.configureContributorFilter(new String[]{"   .  "}));

		assertEquals("rebecca:* & b.:* & carvin:*", testMvcService.configureContributorFilter(new String[]{"Rebecca B. Carvin"}));

		assertEquals("carvin:* & rebecca:* & b.:*", testMvcService.configureContributorFilter(new String[]{"Carvin", " Rebecca B."}));
	}

	@Test
	public void configureSingleSearchFiltersTest() {
		//A null value should not add to the map
		Map<String, Object> filters = testMvcService.configureSingleSearchFilters(null);
		assertEquals(0, filters.keySet().size());

		//An empty value should not add to the map
		filters = testMvcService.configureSingleSearchFilters("");
		assertEquals(0, filters.keySet().size());
		filters = testMvcService.configureSingleSearchFilters("   ,  ");
		assertEquals(0, filters.keySet().size());

		filters = testMvcService.configureSingleSearchFilters("turtles and, loggerhead,, ");
		assertEquals(2, filters.keySet().size());
		assertTrue(filters.containsKey(MpPublicationDao.SEARCH_TERMS));
		Object[] searchTerms = (Object[]) filters.get(MpPublicationDao.SEARCH_TERMS);
		assertEquals(3, searchTerms.length);
		assertEquals("turtles", searchTerms[0].toString());
		assertEquals("and", searchTerms[1].toString());
		assertEquals("loggerhead", searchTerms[2].toString());
		assertTrue(filters.containsKey(MpPublicationDao.SEARCH_TERMS));
		String q = (String) filters.get(PublicationDao.Q);
		assertEquals("turtles:* & and:* & loggerhead:*", q);

		filters = testMvcService.configureSingleSearchFilters("turtles-loggerhead");
		assertEquals(2, filters.keySet().size());
		assertTrue(filters.containsKey(MpPublicationDao.SEARCH_TERMS));
		searchTerms = (Object[]) filters.get(MpPublicationDao.SEARCH_TERMS);
		assertEquals(2, searchTerms.length);
		assertEquals("turtles", searchTerms[0].toString());
		assertEquals("loggerhead", searchTerms[1].toString());
		assertTrue(filters.containsKey(MpPublicationDao.SEARCH_TERMS));
		q = (String) filters.get(PublicationDao.Q);
		assertEquals("turtles:* & loggerhead:*", q);
	}

	@Test
	public void setHeadersTest() {
		HttpServletResponse response = new MockHttpServletResponse();
		testMvcService.setHeaders(response);
		assertEquals(PubsConstants.DEFAULT_ENCODING, response.getCharacterEncoding());
	}

	@Test
	public void validateParametersSetHeaders() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		assertFalse(testMvcService.validateParametersSetHeaders(request, response));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		Map<String, String[]> parms = new HashMap<>();
		parms.put("test", new String[]{"val"});
		request = new MockHttpServletRequest();
		request.setParameters(parms);
		response = new MockHttpServletResponse();
		assertTrue(testMvcService.validateParametersSetHeaders(request, response));
		assertFalse(HttpStatus.BAD_REQUEST.value() == response.getStatus());

		request = new MockHttpServletRequest();
		request.addHeader(PubsConstants.ACCEPT_HEADER, PubsConstants.MEDIA_TYPE_CSV_VALUE);
		response = new MockHttpServletResponse();
		assertTrue(testMvcService.validateParametersSetHeaders(request, response));
		assertFalse(HttpStatus.BAD_REQUEST.value() == response.getStatus());

		request.setParameters(parms);
		response = new MockHttpServletResponse();
		assertTrue(testMvcService.validateParametersSetHeaders(request, response));
		assertFalse(HttpStatus.BAD_REQUEST.value() == response.getStatus());
	}

	@Test
	public void mapListIdTest() {
		Integer[] ids = testMvcService.mapListId(new String[] {"1", "2"});
		assertArrayEquals(new Integer[] {1,2}, ids);

		ids = testMvcService.mapListId(new String[0]);
		assertArrayEquals(new Integer[0], ids);

		ids = testMvcService.mapListId(new String[] {"bad", "3", "way", "4", "evil"});
		assertArrayEquals(new Integer[] {3,4}, ids);

		ids = testMvcService.mapListId(null);
		assertNull(ids);
	}
}
