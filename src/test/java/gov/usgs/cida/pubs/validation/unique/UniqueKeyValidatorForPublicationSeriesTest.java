package gov.usgs.cida.pubs.validation.unique;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.typehandler.StringBooleanTypeHandler;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we nned to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class UniqueKeyValidatorForPublicationSeriesTest extends BaseValidatorTest {

	protected UniqueKeyValidatorForPublicationSeries validator;
	protected PublicationSeries series;
	protected PublicationSubtype st;

	@Mock
	protected PublicationSeriesDao publicationSeriesDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new UniqueKeyValidatorForPublicationSeries();
		series = new PublicationSeries();
		series.setPublicationSeriesDao(publicationSeriesDao);
		series.setText("test");
		series.setCode("tst");
		series.setSeriesDoiName("doi");
		series.setPrintIssn("print");
		series.setOnlineIssn("online");
		st = new PublicationSubtype();
		st.setId(1);
		series.setPublicationSubtype(st);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(series, null));
	}

	@Test
	public void isValidTrueTest() {
		when(publicationSeriesDao.uniqueCheck(any(PublicationSeries.class))).thenReturn(new HashMap<BigDecimal, Map<String, Object>>());
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

	public static Map<BigDecimal, Map<String, Object>> allDup() {
		Map<BigDecimal, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, BigDecimal.ONE);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, StringBooleanTypeHandler.TRUE);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, StringBooleanTypeHandler.TRUE);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, StringBooleanTypeHandler.TRUE);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, StringBooleanTypeHandler.TRUE);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, StringBooleanTypeHandler.TRUE);
		rtn.put(BigDecimal.ONE, entry);
		return rtn;
	}

	public static Map<BigDecimal, Map<String, Object>> nameDup() {
		Map<BigDecimal, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, BigDecimal.ONE);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, StringBooleanTypeHandler.TRUE);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, StringBooleanTypeHandler.FALSE);
		rtn.put(BigDecimal.ONE, entry);
		return rtn;
	}

	public static Map<BigDecimal, Map<String, Object>> codeDup() {
		Map<BigDecimal, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, BigDecimal.ONE);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, StringBooleanTypeHandler.TRUE);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, StringBooleanTypeHandler.FALSE);
		rtn.put(BigDecimal.ONE, entry);
		return rtn;
	}

	public static Map<BigDecimal, Map<String, Object>> doiDup() {
		Map<BigDecimal, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, BigDecimal.ONE);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, StringBooleanTypeHandler.TRUE);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, StringBooleanTypeHandler.TRUE);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, StringBooleanTypeHandler.FALSE);
		rtn.put(BigDecimal.ONE, entry);
		return rtn;
	}

	public static Map<BigDecimal, Map<String, Object>> printIssnDup() {
		Map<BigDecimal, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, BigDecimal.ONE);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, StringBooleanTypeHandler.TRUE);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, StringBooleanTypeHandler.FALSE);
		rtn.put(BigDecimal.ONE, entry);
		return rtn;
	}

	public static Map<BigDecimal, Map<String, Object>> onlineIssnDup() {
		Map<BigDecimal, Map<String, Object>> rtn = new HashMap<>();
		Map<String, Object> entry = new HashMap<>();
		entry.put(UniqueKeyValidatorForPublicationSeries.ID, BigDecimal.ONE);
		entry.put(UniqueKeyValidatorForPublicationSeries.NAME_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.CODE_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH, StringBooleanTypeHandler.FALSE);
		entry.put(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH, StringBooleanTypeHandler.TRUE);
		rtn.put(BigDecimal.ONE, entry);
		return rtn;
	}

}
