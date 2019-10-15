package gov.usgs.cida.pubs.webservice;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.CorporateContributorDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.query.PersonContributorFilterParams;
import gov.usgs.cida.pubs.domain.query.PublicationFilterParams;
import gov.usgs.cida.pubs.utility.DataNormalizationUtils;

public class MvcServiceTest {

	@MockBean
	WebRequest request;

	private class TestMvcService extends MvcService<MvcServiceTest> {

	}

	private TestMvcService testMvcService = new TestMvcService();

	@Test
	public void buildPublicationFiltersTest() {
		Boolean chorus = true;
		String[] contributingOffice = new String[]{"co1","co2"};
		String[] contributor = new String[]{"Rebecca B. Carvin", "c2"};
		String[] orcid = new String[]{"http://orcid.org/0000-0000-0000-0000", "http://orcid.org/1111-1111-1111-1111"};
		String[] doi = new String[]{"DOI-123"};
		Boolean hasDoi = true;
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

		PublicationFilterParams filterParams = buildPwPubFilterParams(chorus, contributingOffice, contributor, orcid, doi, hasDoi, endYear, g,
				global, indexId, ipdsId, listId, orderBy, prodId, pubAbstract,
				q, linkType, noLinkType, reportNumber, seriesName, startYear, subtypeName, title, typeName, year);

		Map<String, Object> filters = testMvcService.buildFilters(filterParams);
		filters.put(BaseDao.PAGE_NUMBER, page_number);
		filters.put(PublicationDao.PAGE_ROW_START, page_row_start);
		filters.put(PublicationDao.PAGE_SIZE, page_size);

		filters.putAll(testMvcService.buildFilters(modDateHigh, modDateLow, modXDays, pubDateHigh, pubDateLow, pubXDays));

		assertTrue(filters.containsKey(PublicationDao.PUB_ABSTRACT ));
		assertEquals(pubAbstract, filters.get(PublicationDao.PUB_ABSTRACT));
		assertTrue(filters.containsKey(PwPublicationDao.CHORUS));
		assertEquals(chorus, filters.get(PwPublicationDao.CHORUS));
		assertTrue(filters.containsKey(PublicationDao.CONTRIBUTING_OFFICE));
		assertEquals(contributingOffice, filters.get(PublicationDao.CONTRIBUTING_OFFICE));
		assertTrue(filters.containsKey(PublicationDao.CONTRIBUTOR));
		assertEquals("rebecca:* & b.:* & carvin:* & c2:*", filters.get(PublicationDao.CONTRIBUTOR));
		assertTrue(filters.containsKey(PersonContributorDao.ORCID));
		assertTrue(Arrays.equals(DataNormalizationUtils.normalizeOrcid(orcid), (String[]) filters.get(PersonContributorDao.ORCID)));
		assertTrue(Arrays.equals(doi, (String[]) filters.get(PublicationDao.DOI)));
		assertTrue((Boolean)filters.get(PublicationDao.HAS_DOI));
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
		assertTrue(Arrays.equals(new Integer[] {3}, (Integer[]) filters.get(MpPublicationDao.LIST_ID)));
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
	public void buildPersonContriborFiltersTest() {
		PersonContributorFilterParams filterParams = new PersonContributorFilterParams();

		Integer[] id = new Integer[]{10, 20};
		String[] text = new String[]{"text to search"};
		Boolean corporation = true;
		Boolean usgs = false;
		String[] family = new String[]{"TestFamily"};
		String[] given = new String[]{"testGiven"};
		String[] email = new String[]{"nobody@usgs.gov"};
		String[] orcid = new String[]{"0000-0001-0002-0003"};
		Boolean preferred = true;

		filterParams.setId(id);
		filterParams.setText(text);
		filterParams.setCorporation(corporation);
		filterParams.setUsgs(usgs);
		filterParams.setFamilyName(family);
		filterParams.setGivenName(given);
		filterParams.setEmail(email);
		filterParams.setOrcid(orcid);
		filterParams.setPreferred(preferred);

		Map<String, Object> filters = testMvcService.buildFilters(filterParams);

		assertNotNull(filters);
		assertEquals(9, filters.keySet().size());

		assertTrue(filters.containsKey(BaseDao.ID_SEARCH));
		assertTrue(filters.containsKey(BaseDao.TEXT_SEARCH));
		assertTrue(filters.containsKey(CorporateContributorDao.CORPORATION));
		assertTrue(filters.containsKey(PersonContributorDao.USGS));
		assertTrue(filters.containsKey(PersonContributorDao.FAMILY));
		assertTrue(filters.containsKey(PersonContributorDao.GIVEN));
		assertTrue(filters.containsKey(PersonContributorDao.EMAIL));
		assertTrue(filters.containsKey(PersonContributorDao.ORCID));
		assertTrue(filters.containsKey(PersonContributorDao.PREFERRED));

		assertTrue(Arrays.equals(id, (Integer[]) filters.get(BaseDao.ID_SEARCH)));
		assertEquals("text:* & to:* & search:*", (String) filters.get(BaseDao.TEXT_SEARCH));
		assertTrue("Expected corporation to be true", (Boolean) filters.get(CorporateContributorDao.CORPORATION));
		assertFalse("Expected usgs to be false", (Boolean) filters.get(PersonContributorDao.USGS));
		assertTrue(Arrays.equals(family, (Object[]) filters.get(PersonContributorDao.FAMILY)));
		assertTrue(Arrays.equals(given, (Object[]) filters.get(PersonContributorDao.GIVEN)));
		assertTrue(Arrays.equals(email, (Object[]) filters.get(PersonContributorDao.EMAIL)));
		assertTrue(Arrays.equals(orcid, (Object[]) filters.get(PersonContributorDao.ORCID)));
		assertTrue("Expected perferred to be true", (Boolean) filters.get(PersonContributorDao.PREFERRED));
	}

	@Test
	public void buildFiltersBadGTest() {
		PublicationFilterParams filterParams = buildPwPubFilterParams(null, null, null, null, null, null, "abc", null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		Map<String, Object> filters = testMvcService.buildFilters(filterParams);
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
		assertNull(testMvcService.buildSearchTerms(List.of("   ","  ")));

		String[] searchTerms = testMvcService.buildSearchTerms(List.of("turtles","loggerhead",""," "));
		assertEquals(2, searchTerms.length);
		assertEquals("turtles", searchTerms[0]);
		assertEquals("loggerhead", searchTerms[1]);
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
		assertEquals(PubsConstantsHelper.DEFAULT_ENCODING, response.getCharacterEncoding());
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
		request.addHeader(PubsConstantsHelper.ACCEPT_HEADER, PubsConstantsHelper.MEDIA_TYPE_CSV_VALUE);
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

	protected PublicationFilterParams buildPwPubFilterParams(Boolean chorus, String[] contributingOffice, String[] contributor,
			String[] orcid, String[] doi, Boolean hasDoi, String endYear, String g, String global, String[] indexId, String[] ipdsId, String[] listId,
			String orderBy, String[] prodId, String[] pubAbstract,
			String q, String[] linkType, String[] noLinkType, String[] reportNumber, String[] seriesName,
			String startYear, String[] subtypeName, String[] title, String[] typeName, String[] year) {

			PublicationFilterParams filterParams = new PublicationFilterParams();
			filterParams.setChorus(chorus);
			filterParams.setContributingOffice(contributingOffice);
			filterParams.setContributor(contributor);
			filterParams.setOrcid(orcid);
			filterParams.setDoi(doi);
			filterParams.setHasDoi(hasDoi);
			filterParams.setEndYear(endYear);
			filterParams.setG(g);
			filterParams.setGlobal(global);
			filterParams.setIndexId(indexId);
			filterParams.setIpdsId(ipdsId);
			filterParams.setListId(listId);
			filterParams.setOrderBy(orderBy);
			filterParams.setProdId(prodId);
			filterParams.setPubAbstract(pubAbstract);
			filterParams.setQ(q);
			filterParams.setLinkType(linkType);
			filterParams.setNoLinkType(noLinkType);
			filterParams.setReportNumber(reportNumber);
			filterParams.setSeriesName(seriesName);
			filterParams.setStartYear(startYear);
			filterParams.setSubtypeName(subtypeName);
			filterParams.setTitle(title);
			filterParams.setTypeName(typeName);
			filterParams.setYear(year);

			return filterParams;
	}
}
