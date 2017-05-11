package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;

@Category(IntegrationTest.class)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class PublicationDaoTest extends BaseSpringTest {

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void getByIdTest() {
		//From warehouse
		Publication<?> pub4 = Publication.getPublicationDao().getById(4);
		PwPublicationTest.assertPwPub4(pub4);

		//From mypubs
		Publication<?> pub2 = Publication.getPublicationDao().getById(2);
		MpPublicationDaoTest.assertPub2(pub2);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void getByMapTest() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.PROD_ID, new int[] { 2 });
		Collection<Publication<?>> pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(2, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		filters.clear();
		filters.put(PublicationDao.INDEX_ID, new int[] { 4 });
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(0, pubs.size());

		filters.clear();
		filters.put(PublicationDao.IPDS_ID, new String[] {"IP-056327"});
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(3, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		filters.clear();
		filters.put(PublicationDao.PROD_ID, new int[] { 4 });
		filters.put(PublicationDao.INDEX_ID, new int[] { 4 });
		filters.put(PublicationDao.IPDS_ID, new String[] {"ipds_id"});
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(0, pubs.size());
		
		filters.clear();
		filters.put(MpPublicationDao.SEARCH_TERMS, Arrays.asList("a").toArray());
		pubs = Publication.getPublicationDao().getByMap(filters);

		filters.put(MpPublicationDao.GLOBAL, "true");
		filters.put(PublicationDao.PROD_ID, new int[] { 2 });
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(2, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		filters.clear();
		filters.put(MpPublicationDao.GLOBAL, "true");
		filters.put(PublicationDao.INDEX_ID, new int[] { 4 });
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(4, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		filters.clear();
		filters.put(MpPublicationDao.GLOBAL, "true");
		filters.put(PublicationDao.IPDS_ID, new String[] {"IP-056327"});
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(3, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		filters.clear();
		filters.put(MpPublicationDao.GLOBAL, "true");
		filters.put(PublicationDao.PROD_ID, new int[] { 4 });
		filters.put(PublicationDao.INDEX_ID, new int[] { 4 });
		filters.put(PublicationDao.IPDS_ID, new String[] {"ipds_id"});
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(4, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		filters.clear();
		filters.put(MpPublicationDao.GLOBAL, "true");
		filters.put(MpPublicationDao.SEARCH_TERMS, Arrays.asList("a").toArray());
		pubs = Publication.getPublicationDao().getByMap(filters);

		filters.clear();
		filters.put(MpPublicationDao.GLOBAL, "true");
		filters.put(PublicationDao.SERIES_ID_SEARCH, 330);
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(2, pubs.size());

		//This only checks that the final query is syntactically correct, not that it is logically correct!
		pubs = Publication.getPublicationDao().getByMap(buildAllParms());
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationOrderBy.xml"),
		@DatabaseSetup("classpath:/testData/mpPublicationOrderBy.xml")
	})
	public void getByMapOrderByTest() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(MpPublicationDao.GLOBAL, "true");
		List<Publication<?>> pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(36, pubs.size());
		assertEquals(830, pubs.get(0).getId().intValue());
		assertEquals(810, pubs.get(1).getId().intValue());
		assertEquals(790, pubs.get(2).getId().intValue());
		assertEquals(770, pubs.get(3).getId().intValue());
		assertEquals(750, pubs.get(4).getId().intValue());
		assertEquals(730, pubs.get(5).getId().intValue());
		assertEquals(710, pubs.get(6).getId().intValue());
		assertEquals(690, pubs.get(7).getId().intValue());
		assertEquals(670, pubs.get(8).getId().intValue());
		assertEquals(650, pubs.get(9).getId().intValue());
		assertEquals(630, pubs.get(10).getId().intValue());
		assertEquals(610, pubs.get(11).getId().intValue());
		assertEquals(340, pubs.get(12).getId().intValue());
		assertEquals(100, pubs.get(13).getId().intValue());
		assertEquals(360, pubs.get(14).getId().intValue());
		assertEquals(120, pubs.get(15).getId().intValue());
		assertEquals(380, pubs.get(16).getId().intValue());
		assertEquals(140, pubs.get(17).getId().intValue());
		assertEquals(400, pubs.get(18).getId().intValue());
		assertEquals(160, pubs.get(19).getId().intValue());
		assertEquals(420, pubs.get(20).getId().intValue());
		assertEquals(180, pubs.get(21).getId().intValue());
		assertEquals(440, pubs.get(22).getId().intValue());
		assertEquals(200, pubs.get(23).getId().intValue());
		assertEquals(460, pubs.get(24).getId().intValue());
		assertEquals(220, pubs.get(25).getId().intValue());
		assertEquals(480, pubs.get(26).getId().intValue());
		assertEquals(240, pubs.get(27).getId().intValue());
		assertEquals(500, pubs.get(28).getId().intValue());
		assertEquals(260, pubs.get(29).getId().intValue());
		assertEquals(520, pubs.get(30).getId().intValue());
		assertEquals(280, pubs.get(31).getId().intValue());
		assertEquals(540, pubs.get(32).getId().intValue());
		assertEquals(300, pubs.get(33).getId().intValue());
		assertEquals(560, pubs.get(34).getId().intValue());
		assertEquals(320, pubs.get(35).getId().intValue());

		filters.put(PublicationDao.ORDER_BY, "mpNewest");
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(36, pubs.size());
		assertEquals(770, pubs.get(0).getId().intValue());
		assertEquals(750, pubs.get(1).getId().intValue());
		assertEquals(730, pubs.get(2).getId().intValue());
		assertEquals(650, pubs.get(3).getId().intValue());
		assertEquals(630, pubs.get(4).getId().intValue());
		assertEquals(610, pubs.get(5).getId().intValue());
		assertEquals(830, pubs.get(6).getId().intValue());
		assertEquals(810, pubs.get(7).getId().intValue());
		assertEquals(790, pubs.get(8).getId().intValue());
		assertEquals(710, pubs.get(9).getId().intValue());
		assertEquals(690, pubs.get(10).getId().intValue());
		assertEquals(670, pubs.get(11).getId().intValue());
		assertEquals(100, pubs.get(12).getId().intValue());
		assertEquals(420, pubs.get(13).getId().intValue());
		assertEquals(260, pubs.get(14).getId().intValue());
		assertEquals(140, pubs.get(15).getId().intValue());
		assertEquals(460, pubs.get(16).getId().intValue());
		assertEquals(300, pubs.get(17).getId().intValue());
		assertEquals(340, pubs.get(18).getId().intValue());
		assertEquals(180, pubs.get(19).getId().intValue());
		assertEquals(500, pubs.get(20).getId().intValue());
		assertEquals(360, pubs.get(21).getId().intValue());
		assertEquals(120, pubs.get(22).getId().intValue());
		assertEquals(380, pubs.get(23).getId().intValue());
		assertEquals(400, pubs.get(24).getId().intValue());
		assertEquals(160, pubs.get(25).getId().intValue());
		assertEquals(440, pubs.get(26).getId().intValue());
		assertEquals(200, pubs.get(27).getId().intValue());
		assertEquals(220, pubs.get(28).getId().intValue());
		assertEquals(480, pubs.get(29).getId().intValue());
		assertEquals(240, pubs.get(30).getId().intValue());
		assertEquals(520, pubs.get(31).getId().intValue());
		assertEquals(280, pubs.get(32).getId().intValue());
		assertEquals(540, pubs.get(33).getId().intValue());
		assertEquals(560, pubs.get(34).getId().intValue());
		assertEquals(320, pubs.get(35).getId().intValue());

		filters.put(PublicationDao.ORDER_BY, "title");
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(36, pubs.size());
		assertEquals(100, pubs.get(0).getId().intValue());
		assertEquals(610, pubs.get(1).getId().intValue());
		assertEquals(650, pubs.get(2).getId().intValue());
		assertEquals(690, pubs.get(3).getId().intValue());
		assertEquals(730, pubs.get(4).getId().intValue());
		assertEquals(770, pubs.get(5).getId().intValue());
		assertEquals(810, pubs.get(6).getId().intValue());
		assertEquals(830, pubs.get(7).getId().intValue());
		assertEquals(790, pubs.get(8).getId().intValue());
		assertEquals(750, pubs.get(9).getId().intValue());
		assertEquals(710, pubs.get(10).getId().intValue());
		assertEquals(670, pubs.get(11).getId().intValue());
		assertEquals(630, pubs.get(12).getId().intValue());
		assertEquals(140, pubs.get(13).getId().intValue());
		assertEquals(180, pubs.get(14).getId().intValue());
		assertEquals(220, pubs.get(15).getId().intValue());
		assertEquals(260, pubs.get(16).getId().intValue());
		assertEquals(300, pubs.get(17).getId().intValue());
		assertEquals(340, pubs.get(18).getId().intValue());
		assertEquals(380, pubs.get(19).getId().intValue());
		assertEquals(420, pubs.get(20).getId().intValue());
		assertEquals(460, pubs.get(21).getId().intValue());
		assertEquals(500, pubs.get(22).getId().intValue());
		assertEquals(540, pubs.get(23).getId().intValue());
		assertEquals(560, pubs.get(24).getId().intValue());
		assertEquals(520, pubs.get(25).getId().intValue());
		assertEquals(480, pubs.get(26).getId().intValue());
		assertEquals(440, pubs.get(27).getId().intValue());
		assertEquals(400, pubs.get(28).getId().intValue());
		assertEquals(360, pubs.get(29).getId().intValue());
		assertEquals(320, pubs.get(30).getId().intValue());
		assertEquals(280, pubs.get(31).getId().intValue());
		assertEquals(240, pubs.get(32).getId().intValue());
		assertEquals(200, pubs.get(33).getId().intValue());
		assertEquals(160, pubs.get(34).getId().intValue());
		assertEquals(120, pubs.get(35).getId().intValue());
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationOrigin.xml"),
		@DatabaseSetup("classpath:/testData/mpPublicationOrigin.xml")
	})
	public void getByMapOriginTest() {
		Map<String, Object> filters = new HashMap<>();
		//mypubs only
		List<Publication<?>> pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(4, pubs.size());

		assertEquals(130, pubs.get(0).getId().intValue());
		assertEquals("mypubs", pubs.get(0).getSourceDatabase());
		assertFalse(pubs.get(0).isPublished());

		assertEquals(110, pubs.get(1).getId().intValue());
		assertEquals("mypubs", pubs.get(1).getSourceDatabase());
		assertFalse(pubs.get(1).isPublished());

		assertEquals(100, pubs.get(2).getId().intValue());
		assertEquals("mypubs", pubs.get(2).getSourceDatabase());
		assertTrue(pubs.get(2).isPublished());

		assertEquals(140, pubs.get(3).getId().intValue());
		assertEquals("mypubs", pubs.get(3).getSourceDatabase());
		assertTrue(pubs.get(3).isPublished());

		filters.put(MpPublicationDao.GLOBAL, "true");
		pubs = Publication.getPublicationDao().getByMap(filters);
		assertEquals(6, pubs.size());

		assertEquals(130, pubs.get(0).getId().intValue());
		assertEquals("mypubs", pubs.get(0).getSourceDatabase());
		assertFalse(pubs.get(0).isPublished());

		assertEquals(110, pubs.get(1).getId().intValue());
		assertEquals("mypubs", pubs.get(1).getSourceDatabase());
		assertFalse(pubs.get(1).isPublished());

		assertEquals(100, pubs.get(2).getId().intValue());
		assertEquals("mypubs", pubs.get(2).getSourceDatabase());
		assertTrue(pubs.get(2).isPublished());

		assertEquals(120, pubs.get(3).getId().intValue());
		assertEquals("pubs warehouse", pubs.get(3).getSourceDatabase());
		assertTrue(pubs.get(3).isPublished());

		assertEquals(140, pubs.get(4).getId().intValue());
		assertEquals("mypubs", pubs.get(4).getSourceDatabase());
		assertTrue(pubs.get(4).isPublished());

		assertEquals(160, pubs.get(5).getId().intValue());
		assertEquals("pubs warehouse", pubs.get(5).getSourceDatabase());
		assertTrue(pubs.get(5).isPublished());
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void getObjectCountTest() {
		Map<String, Object> filters = new HashMap<>();
		Integer cnt = Publication.getPublicationDao().getObjectCount(null);
		assertEquals(3, cnt.intValue());

		filters.put(MpPublicationDao.GLOBAL, "false");
		cnt = Publication.getPublicationDao().getObjectCount(filters);
		assertEquals(3, cnt.intValue());

		filters.put(MpPublicationDao.GLOBAL, "true");
		cnt = Publication.getPublicationDao().getObjectCount(filters);
		assertEquals(6, cnt.intValue());

		filters.put(PublicationDao.IPDS_ID, new String[] { "ipds_id" });
		cnt = Publication.getPublicationDao().getObjectCount(filters);
		assertEquals(1, cnt.intValue());

		filters.clear();
		filters.put(MpPublicationDao.GLOBAL, "true");
		filters.put(PublicationDao.SERIES_ID_SEARCH, 330);
		cnt = Publication.getPublicationDao().getObjectCount(filters);
		assertEquals(2, cnt.intValue());
	}

	public static Map<String, Object> buildAllParms() {
		Map<String, Object> filters = new HashMap<>();

		filters.put(PublicationDao.PUB_ABSTRACT, new String[]{"abstract1", "abstractp"});
		filters.put(PwPublicationDao.CHORUS, true);
		filters.put(PublicationDao.CONTRIBUTING_OFFICE, new String[]{"contributingOffice1", "contributingOffice2"});
		filters.put(PublicationDao.CONTRIBUTOR, "contributor1% and contributor2%");
		
		filters.put(PublicationDao.DOI, true);

		filters.put(PublicationDao.END_YEAR, "yearEnd");
		filters.put(PwPublicationDao.G, new String[]{"polygon"});
		filters.put(MpPublicationDao.GLOBAL, "yes");
		filters.put(PublicationDao.INDEX_ID, new String[]{"indexId1", "indexId2"});
		filters.put(PublicationDao.IPDS_ID, new String[]{"ipdsId1", "ipdsId2"});

		filters.put(MpPublicationDao.LIST_ID, new String[]{"listId1", "listId2"});
		filters.put(PwPublicationDao.MOD_DATE_HIGH, "2012-12-12");
		filters.put(PwPublicationDao.MOD_DATE_LOW, "2010-10-10");
		filters.put(PwPublicationDao.MOD_X_DAYS, "3");
		filters.put(PublicationDao.ORDER_BY, "title");

		filters.put(BaseDao.PAGE_NUMBER, "66");
		filters.put(PublicationDao.PAGE_ROW_START, "19");
		filters.put(PublicationDao.PAGE_SIZE, "54");
		filters.put(PublicationDao.PROD_ID, new String[]{"1", "2"});
		filters.put(PwPublicationDao.PUB_DATE_HIGH, "2012-12-12");

		filters.put(PwPublicationDao.PUB_DATE_LOW, "2010-10-10");
		filters.put(PwPublicationDao.PUB_X_DAYS, "1");
		filters.put(PublicationDao.Q, "$turtles");

		
		filters.put(PublicationDao.LINK_TYPE, new String[]{"linkType1", "linkType2"});
		filters.put(PublicationDao.REPORT_NUMBER, new String[]{"reportNumber1", "reportNumber2"});
		filters.put(MpPublicationDao.SEARCH_TERMS, new String[]{"searchTerms1", "searchTerms2"});

		filters.put(PublicationDao.SERIES_ID_SEARCH, 330);
		filters.put(PublicationDao.SERIES_NAME, new String[]{"reportSeries1", "reportSeries2"});
		filters.put(PublicationDao.START_YEAR, "yearStart");
		filters.put(PublicationDao.SUBTYPE_NAME, new String[]{"subtype1", "subtype2"});
		filters.put(PublicationDao.TITLE, new String[]{"title1", "title2"});

		filters.put(PublicationDao.TYPE_NAME, new String[]{"type1", "type2"});
		filters.put(PublicationDao.YEAR, new String[]{"year1", "year2"});

		return filters;
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void filterByIndexIdTest() {
		List<Publication<?>> pubs = Publication.getPublicationDao().filterByIndexId("sir");
		assertEquals(2, pubs.size());
		boolean got1 = false;
		boolean got6 = false;
		for (Publication<?> pub : pubs) {
			if (1 == pub.getId()) {
				got1 = true;
			} else if (6 == pub.getId()) {
				got6 = true;
			} else {
				fail("unexpected pub:" + pub.getId());
			}
		}
		assertTrue(got1);
		assertTrue(got6);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void validateByMapTest() {
		//These should always check both tables - ignoring the MpPublicationDao.GLOBAL parameter
		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.PROD_ID, new int[] { 2 });
		Collection<Publication<?>> pubs = Publication.getPublicationDao().validateByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(2, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		filters.clear();
		filters.put(PublicationDao.INDEX_ID, new int[] { 4 });
		pubs = Publication.getPublicationDao().validateByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(4, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		filters.clear();
		filters.put(PublicationDao.IPDS_ID, new String[] {"IP-056327"});
		pubs = Publication.getPublicationDao().validateByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(3, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		filters.clear();
		filters.put(MpPublicationDao.GLOBAL, "false");
		filters.put(PublicationDao.IPDS_ID, new String[] {"IP-056327"});
		pubs = Publication.getPublicationDao().validateByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(3, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		filters.clear();
		filters.put(MpPublicationDao.GLOBAL, "false");
		filters.put(PublicationDao.PROD_ID, new int[] { 4 });
		filters.put(PublicationDao.INDEX_ID, new int[] { 4 });
		filters.put(PublicationDao.IPDS_ID, new String[] {"ipds_id"});
		pubs = Publication.getPublicationDao().validateByMap(filters);
		assertEquals(1, pubs.size());
		assertEquals(4, ((Publication<?>)pubs.toArray()[0]).getId().intValue());

		//This only checks that the final query is syntactically correct, not that it is logically correct!
		pubs = Publication.getPublicationDao().getByMap(buildAllParms());
	}
}