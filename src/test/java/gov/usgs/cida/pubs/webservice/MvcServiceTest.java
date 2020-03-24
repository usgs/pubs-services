package gov.usgs.cida.pubs.webservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.WebRequest;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.CorporateContributorDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.query.PersonContributorFilterParams;

public class MvcServiceTest {

	@MockBean
	WebRequest request;

	private class TestMvcService extends MvcService<MvcServiceTest> {

	}

	private TestMvcService testMvcService = new TestMvcService();

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
}
