package gov.usgs.cida.pubs.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSeriesTest;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.validation.unique.UniqueKeyValidatorForPublicationSeries;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, PublicationSeriesDao.class})
public class PublicationSeriesDaoIT extends BaseIT {

	public static final int pubSeriesCnt = 17;
	public static final int activePubSeriesCnt = 10;

	@Autowired
	PublicationSeriesDao publicationSeriesDao;

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	public void getByIdInteger() {
		PublicationSeries pubSeries = publicationSeriesDao.getById(330);
		assertNotNull(pubSeries);
		assertEquals(330, pubSeries.getId().intValue());
		assertEquals("Open-File Report", pubSeries.getText());
		assertEquals("OFR", pubSeries.getCode());
		assertNull(pubSeries.getSeriesDoiName());
		assertEquals("0196-1497", pubSeries.getPrintIssn());
		assertEquals("2331-1258", pubSeries.getOnlineIssn());
		assertTrue(pubSeries.isActive());

		pubSeries = publicationSeriesDao.getById(341);
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
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	public void getByIdString() {
		PublicationSeries pubSeries = publicationSeriesDao.getById("1");
		assertPubSeries1(pubSeries);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	public void getByMapAndCount() {
		List<PublicationSeries> pubSeries = publicationSeriesDao.getByMap(null);
		assertEquals(pubSeriesCnt, pubSeries.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 133);
		pubSeries = publicationSeriesDao.getByMap(filters);
		assertNotNull(pubSeries);
		assertEquals(1, pubSeries.size());
		assertEquals(133, pubSeries.get(0).getId().intValue());
		assertEquals("Report", pubSeries.get(0).getText());
		assertNull(pubSeries.get(0).getCode());
		assertNull(pubSeries.get(0).getSeriesDoiName());
		assertNull(pubSeries.get(0).getPrintIssn());
		assertNull(pubSeries.get(0).getOnlineIssn());
		assertFalse(pubSeries.get(0).isActive());
		assertEquals(1, publicationSeriesDao.getObjectCount(filters).intValue());

		filters.clear();
		filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, 5);
		pubSeries = publicationSeriesDao.getByMap(filters);
		assertNotNull(pubSeries);
		assertEquals(9, pubSeries.size());
		assertEquals(9, publicationSeriesDao.getObjectCount(filters).intValue());

		filters.put("text", "sc");
		pubSeries = publicationSeriesDao.getByMap(filters);
		assertEquals(2, pubSeries.size());
		assertEquals(2, publicationSeriesDao.getObjectCount(filters).intValue());

		filters.clear();
		filters.put("code", "MINERAL");
		pubSeries = publicationSeriesDao.getByMap(filters);
		assertEquals(1, pubSeries.size());
		assertEquals(323, pubSeries.get(0).getId().intValue());
		assertEquals("Mineral Commodities Summaries", pubSeries.get(0).getText());
		assertEquals("MINERAL", pubSeries.get(0).getCode());
		assertNull(pubSeries.get(0).getSeriesDoiName());
		assertEquals("0076-8952", pubSeries.get(0).getPrintIssn());
		assertNull(pubSeries.get(0).getOnlineIssn());
		assertTrue(pubSeries.get(0).isActive());
		assertEquals(1, publicationSeriesDao.getObjectCount(filters).intValue());

		filters.clear();
		filters.put("active", true);
		pubSeries = publicationSeriesDao.getByMap(filters);
		assertEquals(activePubSeriesCnt, pubSeries.size());
		assertEquals(activePubSeriesCnt, publicationSeriesDao.getObjectCount(filters).intValue());

	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/delete.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void deleteTest() {
		publicationSeriesDao.delete(new PublicationSeries());
		PublicationSeries one = new PublicationSeries();
		one.setId(1);
		publicationSeriesDao.delete(one);
		publicationSeriesDao.deleteById(333);
		publicationSeriesDao.deleteByParent(10);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml")
	})
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/add.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED, modifiers = IdModifier.class)
	public void addTest() {
		PublicationSeries publicationSeries = PublicationSeriesTest.buildAPubSeries(null);
		id = publicationSeriesDao.add(publicationSeries);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml")
	})
	@ExpectedDatabase(value = "classpath:/testResult/publicationSeries/update.xml", assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void updateTest() {
		PublicationSeries publicationSeries = update330Properties();
		publicationSeriesDao.update(publicationSeries);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
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
		Map<Integer, Map<String, Object>> dups = publicationSeriesDao.uniqueCheck(publicationSeries);
		assertNotNull(dups);
		assertEquals(4, dups.size());
		assertTrue(dups.containsKey(133));
		assertTrue(dups.containsKey(330));
		assertTrue(dups.containsKey(333));
		assertTrue(dups.containsKey(334));
		Map<String, Object> dup133 = dups.get(133);
		assertTrue((Boolean)dup133.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertFalse((Boolean)dup133.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertFalse((Boolean)dup133.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertFalse((Boolean)dup133.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertFalse((Boolean)dup133.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
		Map<String, Object> dup330 = dups.get(330);
		assertFalse((Boolean)dup330.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertTrue((Boolean)dup330.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertFalse((Boolean)dup330.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertFalse((Boolean)dup330.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertFalse((Boolean)dup330.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
		Map<String, Object> dup333 = dups.get(333);
		assertFalse((Boolean)dup333.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertFalse((Boolean)dup333.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertFalse((Boolean)dup333.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertFalse((Boolean)dup333.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertTrue((Boolean)dup333.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
		Map<String, Object> dup334 = dups.get(334);
		assertFalse((Boolean)dup334.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertFalse((Boolean)dup334.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertFalse((Boolean)dup334.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertTrue((Boolean)dup334.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertFalse((Boolean)dup334.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
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
		Map<Integer, Map<String, Object>> dups = publicationSeriesDao.uniqueCheck(publicationSeries);
		assertNotNull(dups);
		assertEquals(3, dups.size());
		assertTrue(dups.containsKey(133));
		assertTrue(dups.containsKey(333));
		assertTrue(dups.containsKey(334));
		Map<String, Object> dup133 = dups.get(133);
		assertTrue((Boolean)dup133.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertFalse((Boolean)dup133.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertFalse((Boolean)dup133.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertFalse((Boolean)dup133.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertFalse((Boolean)dup133.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
		Map<String, Object> dup333 = dups.get(333);
		assertFalse((Boolean)dup333.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertFalse((Boolean)dup333.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertFalse((Boolean)dup333.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertFalse((Boolean)dup333.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertTrue((Boolean)dup333.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
		Map<String, Object> dup334 = dups.get(334);
		assertFalse((Boolean)dup334.get(UniqueKeyValidatorForPublicationSeries.NAME_MATCH));
		assertFalse((Boolean)dup334.get(UniqueKeyValidatorForPublicationSeries.CODE_MATCH));
		assertFalse((Boolean)dup334.get(UniqueKeyValidatorForPublicationSeries.DOI_NAME_MATCH));
		assertTrue((Boolean)dup334.get(UniqueKeyValidatorForPublicationSeries.PRINT_ISSN_MATCH));
		assertFalse((Boolean)dup334.get(UniqueKeyValidatorForPublicationSeries.ONLINE_ISSN_MATCH));
	}

	@Test
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
	public void uniqueCheckEmptyReturnTest() {
		Map<Integer, Map<String, Object>> dups = publicationSeriesDao.uniqueCheck(new PublicationSeries());
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
