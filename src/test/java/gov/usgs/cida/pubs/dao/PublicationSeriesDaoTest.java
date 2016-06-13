package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.dao.typehandler.StringBooleanTypeHandler;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSeriesTest;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.validation.unique.UniqueKeyValidatorForPublicationSeries;

@Category(IntegrationTest.class)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
@DatabaseTearDown("classpath:/testCleanup/clearAll.xml")
public class PublicationSeriesDaoTest extends BaseSpringTest {

	public static final int pubSeriesCnt = 15;
	public static final int activePubSeriesCnt = 8;

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	public void getByIdInteger() {
		PublicationSeries pubSeries = PublicationSeries.getDao().getById(330);
		assertNotNull(pubSeries);
		assertEquals(330, pubSeries.getId().intValue());
		assertEquals("Open-File Report", pubSeries.getText());
		assertEquals("OFR", pubSeries.getCode());
		assertNull(pubSeries.getSeriesDoiName());
		assertEquals("0196-1497", pubSeries.getPrintIssn());
		assertEquals("2331-1258", pubSeries.getOnlineIssn());
		assertTrue(pubSeries.isActive());

		pubSeries = PublicationSeries.getDao().getById(341);
		assertNotNull(pubSeries);
		assertEquals(341, pubSeries.getId().intValue());
		assertEquals("Water Supply Paper", pubSeries.getText());
		assertEquals("WSP", pubSeries.getCode());
		assertNull(pubSeries.getSeriesDoiName());
		assertNull(pubSeries.getPrintIssn());
		assertNull(pubSeries.getOnlineIssn());
		assertFalse(pubSeries.isActive());
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	public void getByIdString() {
		PublicationSeries pubSeries = PublicationSeries.getDao().getById("1");
		assertPubSeries1(pubSeries);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	public void getByMapAndCount() {
		List<PublicationSeries> pubSeries = PublicationSeries.getDao().getByMap(null);
		assertEquals(pubSeriesCnt, pubSeries.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 133);
		pubSeries = PublicationSeries.getDao().getByMap(filters);
		assertNotNull(pubSeries);
		assertEquals(1, pubSeries.size());
		assertEquals(133, pubSeries.get(0).getId().intValue());
		assertEquals("Report", pubSeries.get(0).getText());
		assertNull(pubSeries.get(0).getCode());
		assertNull(pubSeries.get(0).getSeriesDoiName());
		assertNull(pubSeries.get(0).getPrintIssn());
		assertNull(pubSeries.get(0).getOnlineIssn());
		assertFalse(pubSeries.get(0).isActive());
		assertEquals(1, PublicationSeries.getDao().getObjectCount(filters).intValue());

		filters.clear();
		filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, 5);
		pubSeries = PublicationSeries.getDao().getByMap(filters);
		assertNotNull(pubSeries);
		assertEquals(9, pubSeries.size());
		assertEquals(9, PublicationSeries.getDao().getObjectCount(filters).intValue());

		filters.put("text", "sc");
		pubSeries = PublicationSeries.getDao().getByMap(filters);
		assertEquals(2, pubSeries.size());
		assertEquals(2, PublicationSeries.getDao().getObjectCount(filters).intValue());

		filters.clear();
		filters.put("code", "MINERAL");
		pubSeries = PublicationSeries.getDao().getByMap(filters);
		assertEquals(1, pubSeries.size());
		assertEquals(323, pubSeries.get(0).getId().intValue());
		assertEquals("Mineral Commodities Summaries", pubSeries.get(0).getText());
		assertEquals("MINERAL", pubSeries.get(0).getCode());
		assertNull(pubSeries.get(0).getSeriesDoiName());
		assertEquals("0076-8952", pubSeries.get(0).getPrintIssn());
		assertNull(pubSeries.get(0).getOnlineIssn());
		assertTrue(pubSeries.get(0).isActive());
		assertEquals(1, PublicationSeries.getDao().getObjectCount(filters).intValue());

		filters.clear();
		filters.put("active", "Y");
		pubSeries = PublicationSeries.getDao().getByMap(filters);
		assertEquals(activePubSeriesCnt, pubSeries.size());
		assertEquals(activePubSeriesCnt, PublicationSeries.getDao().getObjectCount(filters).intValue());

	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/delete.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteTest() {
		PublicationSeries.getDao().delete(new PublicationSeries());
		PublicationSeries one = new PublicationSeries();
		one.setId(1);
		PublicationSeries.getDao().delete(one);
		PublicationSeries.getDao().deleteById(333);
		PublicationSeries.getDao().deleteByParent(10);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml")
	})
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/add.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED, modifiers = IdModifier.class)
	public void addTest() {
		PublicationSeries publicationSeries = PublicationSeriesTest.buildAPubSeries(null);
		id = PublicationSeries.getDao().add(publicationSeries);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/update.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void updateTest() {
		PublicationSeries publicationSeries = update330Properties();
		PublicationSeries.getDao().update(publicationSeries);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	public void uniqueCheckOnAddTest() {
		PublicationSeries publicationSeries = new PublicationSeries();
		PublicationSubtype st = new PublicationSubtype();
		st.setId(2);
		publicationSeries.setPublicationSubtype(st);
		publicationSeries.setText("report");
		publicationSeries.setCode("ofr");
		publicationSeries.setPrintIssn("2328-031x");
		publicationSeries.setOnlineIssn("2329-132x");
		Map<BigDecimal, Map<String, Object>> dups = PublicationSeries.getDao().uniqueCheck(publicationSeries);
		assertNotNull(dups);
		assertEquals(4, dups.size());
		assertTrue(dups.containsKey(BigDecimal.valueOf(133)));
		assertTrue(dups.containsKey(BigDecimal.valueOf(330)));
		assertTrue(dups.containsKey(BigDecimal.valueOf(333)));
		assertTrue(dups.containsKey(BigDecimal.valueOf(334)));
		Map<String, Object> dup133 = dups.get(BigDecimal.valueOf(133));
		assertEquals(StringBooleanTypeHandler.TRUE, dup133.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup133.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup133.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup133.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup133.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
		Map<String, Object> dup330 = dups.get(BigDecimal.valueOf(330));
		assertEquals(StringBooleanTypeHandler.FALSE, dup330.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.TRUE, dup330.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup330.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup330.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup330.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
		Map<String, Object> dup333 = dups.get(BigDecimal.valueOf(333));
		assertEquals(StringBooleanTypeHandler.FALSE, dup333.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup333.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup333.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup333.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertEquals(StringBooleanTypeHandler.TRUE, dup333.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
		Map<String, Object> dup334 = dups.get(BigDecimal.valueOf(334));
		assertEquals(StringBooleanTypeHandler.FALSE, dup334.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup334.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup334.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.TRUE, dup334.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup334.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	public void uniqueCheckOnUpdateTest() {
		PublicationSeries publicationSeries = new PublicationSeries();
		publicationSeries.setId(330);
		PublicationSubtype st = new PublicationSubtype();
		st.setId(2);
		publicationSeries.setPublicationSubtype(st);
		publicationSeries.setText("report");
		publicationSeries.setCode("ofr");
		publicationSeries.setPrintIssn("2328-031x");
		publicationSeries.setOnlineIssn("2329-132x");
		Map<BigDecimal, Map<String, Object>> dups = PublicationSeries.getDao().uniqueCheck(publicationSeries);
		assertNotNull(dups);
		assertEquals(3, dups.size());
		assertTrue(dups.containsKey(BigDecimal.valueOf(133)));
		assertTrue(dups.containsKey(BigDecimal.valueOf(333)));
		assertTrue(dups.containsKey(BigDecimal.valueOf(334)));
		Map<String, Object> dup133 = dups.get(BigDecimal.valueOf(133));
		assertEquals(StringBooleanTypeHandler.TRUE, dup133.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup133.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup133.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup133.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup133.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
		Map<String, Object> dup333 = dups.get(BigDecimal.valueOf(333));
		assertEquals(StringBooleanTypeHandler.FALSE, dup333.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup333.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup333.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup333.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertEquals(StringBooleanTypeHandler.TRUE, dup333.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
		Map<String, Object> dup334 = dups.get(BigDecimal.valueOf(334));
		assertEquals(StringBooleanTypeHandler.FALSE, dup334.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup334.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup334.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertEquals(StringBooleanTypeHandler.TRUE, dup334.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertEquals(StringBooleanTypeHandler.FALSE, dup334.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
	}

	@Test
	public void uniqueCheckEmptyReturnTest() {
		Map<BigDecimal, Map<String, Object>> dups = PublicationSeries.getDao().uniqueCheck(new PublicationSeries());
		assertNotNull(dups);
		assertTrue(dups.isEmpty());
	}

	public static void assertPubSeries1(PublicationSeries pubSeries) {
		assertNotNull(pubSeries);
		assertEquals(1, pubSeries.getId().intValue());
		assertEquals("Administrative Report", pubSeries.getText());
		assertNull(pubSeries.getCode());
		assertNull(pubSeries.getSeriesDoiName());
		assertNull(pubSeries.getPrintIssn());
		assertNull(pubSeries.getOnlineIssn());
		assertFalse(pubSeries.isActive());
	}

	public static PublicationSeries update330Properties() {
		PublicationSeries pubSeries = new PublicationSeries();
		pubSeries.setId(330);
		PublicationSubtype publicationSubtype = new PublicationSubtype();
		publicationSubtype.setId(29);
		pubSeries.setPublicationSubtype(publicationSubtype);
		pubSeries.setText("New Video");
		pubSeries.setCode("XYZ");
		pubSeries.setSeriesDoiName("doiname is here");
		pubSeries.setPrintIssn("1234-4321");
		pubSeries.setOnlineIssn("5678-8765");
		pubSeries.setActive(false);
		return pubSeries;
	}
}
