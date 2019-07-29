package gov.usgs.cida.pubs.validation.unique;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={PublicationSeries.class})
public class UniqueKeyValidatorForPublicationSeriesTest extends BaseValidatorTest {

	protected UniqueKeyValidatorForPublicationSeries validator;
	protected PublicationSeries series;
	protected PublicationSubtype st;

	@MockBean(name="publicationSeriesDao")
	protected PublicationSeriesDao publicationSeriesDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new UniqueKeyValidatorForPublicationSeries();
		series = new PublicationSeries();
		series.setText("test");
		series.setCode("tst");
		series.setSeriesDoiName("doi");
		series.setPrintIssn("print");
		series.setOnlineIssn("online");
		st = new PublicationSubtype();
		st.setId(1);
		series.setPublicationSubtype(st);

		reset(publicationSeriesDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(series, null));
	}

	@Test
	public void isValidTrueTest() {
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(new HashMap<Integer, Map<String, Object>>());
		assertTrue(validator.isValid(series, context));
	}

	@Test
	public void isValidAllTest() {
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(allDup());
		assertFalse(validator.isValid(series, context));
	}

	@Test
	public void isValidNameTest() {
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(nameDup());
		assertFalse(validator.isValid(series, context));
	}

	@Test
	public void isValidCodeTest() {
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(codeDup());
		assertFalse(validator.isValid(series, context));
	}

	@Test
	public void isValidDoiTest() {
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(doiDup());
		assertFalse(validator.isValid(series, context));
	}

	@Test
	public void isValidPrintIssnTest() {
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(printIssnDup());
		assertFalse(validator.isValid(series, context));
	}

	@Test
	public void isValidOnlineIssnTest() {
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(onlineIssnDup());
		assertFalse(validator.isValid(series, context));
	}

	public static Map<Integer, Map<String, Object>> allDup() {
		Map<Integer, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, 1);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, true);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, true);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, true);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, true);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, true);
		rtn.put(1, entry);
		return rtn;
	}

	public static Map<Integer, Map<String, Object>> nameDup() {
		Map<Integer, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, 1);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, true);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, false);
		rtn.put(1, entry);
		return rtn;
	}

	public static Map<Integer, Map<String, Object>> codeDup() {
		Map<Integer, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, 1);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, true);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, false);
		rtn.put(1, entry);
		return rtn;
	}

	public static Map<Integer, Map<String, Object>> doiDup() {
		Map<Integer, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, 1);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, true);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, true);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, false);
		rtn.put(1, entry);
		return rtn;
	}

	public static Map<Integer, Map<String, Object>> printIssnDup() {
		Map<Integer, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, 1);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, true);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, false);
		rtn.put(1, entry);
		return rtn;
	}

	public static Map<Integer, Map<String, Object>> onlineIssnDup() {
		Map<Integer, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, 1);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, false);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, true);
		rtn.put(1, entry);
		return rtn;
	}

}
