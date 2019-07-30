package gov.usgs.cida.pubs.dao.pw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.google.common.collect.ImmutableMap;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationDaoIT;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, PwPublicationDao.class})
public class PwPublicationDaoIT extends BaseIT {

	@Autowired
	PwPublicationDao pwPublicationDao;

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void getByIdTest() {
		PwPublication pub = pwPublicationDao.getById(4);
		assertNotNull(pub);
		PwPublicationTest.assertPwPub4(pub);
		PwPublicationTest.assertPwPub4Children(pub);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void getByIpdsIdTest() {
		PwPublication pub = pwPublicationDao.getByIpdsId("ipds_id");
		assertNotNull(pub);
		PwPublicationTest.assertPwPub4(pub);
		PwPublicationTest.assertPwPub4Children(pub);
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void getByMapTest() {
		pwPublicationDao.refreshTextIndex();
		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.Q, "title");
		List<PwPublication> pubs = pwPublicationDao.getByMap(filters);
		assertNotNull(pubs);
		assertEquals(1, pubs.size());
		PwPublicationTest.assertPwPub4(pubs.get(0));
		PwPublicationTest.assertPwPub4Children(pubs.get(0));

		filters.put(PwPublicationDao.G, SEARCH_POLYGON);
		pubs = pwPublicationDao.getByMap(filters);

		//This only checks that the final query is syntactically correct, not that it is logically correct!
		pwPublicationDao.getByMap(PublicationDaoIT.buildAllParms());
		//TODO add in real filter tests
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationOrderBy.xml")
	})
	public void getByMapOrderByTest() {
		Map<String, Object> filters = new HashMap<>();
		List<PwPublication> pubs = pwPublicationDao.getByMap(filters);
		assertEquals(24, pubs.size());
		assertEquals(340, pubs.get(0).getId().intValue());
		assertEquals(100, pubs.get(1).getId().intValue());
		assertEquals(360, pubs.get(2).getId().intValue());
		assertEquals(120, pubs.get(3).getId().intValue());
		assertEquals(380, pubs.get(4).getId().intValue());
		assertEquals(140, pubs.get(5).getId().intValue());
		assertEquals(400, pubs.get(6).getId().intValue());
		assertEquals(160, pubs.get(7).getId().intValue());
		assertEquals(420, pubs.get(8).getId().intValue());
		assertEquals(180, pubs.get(9).getId().intValue());
		assertEquals(440, pubs.get(10).getId().intValue());
		assertEquals(200, pubs.get(11).getId().intValue());
		assertEquals(460, pubs.get(12).getId().intValue());
		assertEquals(220, pubs.get(13).getId().intValue());
		assertEquals(480, pubs.get(14).getId().intValue());
		assertEquals(240, pubs.get(15).getId().intValue());
		assertEquals(500, pubs.get(16).getId().intValue());
		assertEquals(260, pubs.get(17).getId().intValue());
		assertEquals(520, pubs.get(18).getId().intValue());
		assertEquals(280, pubs.get(19).getId().intValue());
		assertEquals(540, pubs.get(20).getId().intValue());
		assertEquals(300, pubs.get(21).getId().intValue());
		assertEquals(560, pubs.get(22).getId().intValue());
		assertEquals(320, pubs.get(23).getId().intValue());

		filters.put(PublicationDao.ORDER_BY, "title");
		pubs = pwPublicationDao.getByMap(filters);
		assertEquals(24, pubs.size());
		assertEquals(100, pubs.get(0).getId().intValue());
		assertEquals(140, pubs.get(1).getId().intValue());
		assertEquals(180, pubs.get(2).getId().intValue());
		assertEquals(220, pubs.get(3).getId().intValue());
		assertEquals(260, pubs.get(4).getId().intValue());
		assertEquals(300, pubs.get(5).getId().intValue());
		assertEquals(340, pubs.get(6).getId().intValue());
		assertEquals(380, pubs.get(7).getId().intValue());
		assertEquals(420, pubs.get(8).getId().intValue());
		assertEquals(460, pubs.get(9).getId().intValue());
		assertEquals(500, pubs.get(10).getId().intValue());
		assertEquals(540, pubs.get(11).getId().intValue());
		assertEquals(560, pubs.get(12).getId().intValue());
		assertEquals(520, pubs.get(13).getId().intValue());
		assertEquals(480, pubs.get(14).getId().intValue());
		assertEquals(440, pubs.get(15).getId().intValue());
		assertEquals(400, pubs.get(16).getId().intValue());
		assertEquals(360, pubs.get(17).getId().intValue());
		assertEquals(320, pubs.get(18).getId().intValue());
		assertEquals(280, pubs.get(19).getId().intValue());
		assertEquals(240, pubs.get(20).getId().intValue());
		assertEquals(200, pubs.get(21).getId().intValue());
		assertEquals(160, pubs.get(22).getId().intValue());
		assertEquals(120, pubs.get(23).getId().intValue());
	}

	@Test
	public void getStreamByMapTest() {
		//This only checks that the final query is syntactically correct, not that it is logically correct!
		pwPublicationDao.stream(PwPublicationDao.NS + PwPublicationDao.GET_STREAM_BY_MAP, PublicationDaoIT.buildAllParms(), null);
		//TODO add in real filter tests
	}

	@Test
	public void getPoJoStreamByMapTest() {
		//This only checks that the final query is syntactically correct, not that it is logically correct!
		pwPublicationDao.stream(PwPublicationDao.NS + PwPublicationDao.GET_BY_MAP, PublicationDaoIT.buildAllParms(), null);
		//TODO add in real filter tests
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void getObjectCountTest() {
		pwPublicationDao.refreshTextIndex();
		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.Q, "title");
		Integer cnt = pwPublicationDao.getObjectCount(filters);
		assertEquals(1, cnt.intValue());

		//This only checks that the final query is syntactically correct, not that it is logically correct!
		cnt = pwPublicationDao.getObjectCount(PublicationDaoIT.buildAllParms());
		//TODO add in real filter tests
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void getByIndexIdTest() {
		pwPublicationDao.refreshTextIndex();
		//We can get 4
		PwPublication pub = pwPublicationDao.getByIndexId("4");
		assertNotNull(pub);
		PwPublicationTest.assertPwPub4(pub);
		PwPublicationTest.assertPwPub4Children(pub);

		//5 is not ready to display
		pub = pwPublicationDao.getByIndexId("9");
		assertNull(pub);
		//but it really does exist
		assertNotNull(pwPublicationDao.getById(5));
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void testGetByLinkType() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.LINK_TYPE, new String[] {"Abstract"});
		List<PwPublication> pubs = pwPublicationDao.getByMap(filters);
		assertNotNull(pubs);
		assertFalse(pubs.isEmpty());
		assertFalse(pubs.get(0).getLinks().isEmpty());
		PublicationLink<?> publicationLink = (PublicationLink<?>)pubs.get(0).getLinks().toArray()[0];
		assertEquals("Abstract",publicationLink.getLinkType().getText());
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void testGetByNoLinkType() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.NO_LINK_TYPE, new String[] {"Abstract"});
		List<PwPublication> pubs = pwPublicationDao.getByMap(filters);
		assertNotNull(pubs);
		assertFalse(pubs.isEmpty());
		assertTrue(pubs.get(0).getLinks().isEmpty());
		filters.put(PublicationDao.NO_LINK_TYPE, new String[] {"NoExistUniqueNotUsedTestValue"});
		pubs = pwPublicationDao.getByMap(filters);
		assertNotNull(pubs);
		assertFalse(pubs.isEmpty());
		assertEquals(2, pubs.size());
	}
	
	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/contributor.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/crossrefDataset.xml")
	})
	public void selectByPublicationSubtype(){
		//get exactly one unnumbered usgs series pubs with a doi
		Map<String, Object> filters = ImmutableMap.of(
			PwPublicationDao.SUBTYPE_ID, new int[]{PublicationSubtype.USGS_NUMBERED_SERIES}
		);
		List<PwPublication> pubs = pwPublicationDao.getCrossrefPublications(filters);
		assertEquals(1, pubs.size());
		assertEquals("sir2", pubs.get(0).getIndexId());
		
		//get exactly one numbered usgs series pubs with a doi
		filters = ImmutableMap.of(
			PwPublicationDao.SUBTYPE_ID, new int[]{PublicationSubtype.USGS_UNNUMBERED_SERIES}
		);
		pubs = pwPublicationDao.getCrossrefPublications(filters);
		assertEquals(1, pubs.size());
		assertEquals("sir3", pubs.get(0).getIndexId());
		
		//get exactly two usgs series pubs with dois
		filters = ImmutableMap.of(
			PwPublicationDao.SUBTYPE_ID, new int[]{
				PublicationSubtype.USGS_NUMBERED_SERIES,
				PublicationSubtype.USGS_UNNUMBERED_SERIES
			}
		);
		
		pubs = pwPublicationDao.getCrossrefPublications(filters);
		assertEquals(2, pubs.size());
		List<String> actualIndexIds = pubs.stream().map((pub) -> pub.getIndexId()).sorted().collect(Collectors.toList());
		List<String> expectedIndexIds = Arrays.asList("sir2", "sir3").stream().sorted().collect(Collectors.toList());
		assertEquals(expectedIndexIds, actualIndexIds);
		
		//verify that none of an unknown subtype ID are returned
		filters = ImmutableMap.of(
			PwPublicationDao.SUBTYPE_ID, new int[]{-999}
		);
		pubs = pwPublicationDao.getCrossrefPublications(filters);
		assertEquals(0, pubs.size());
	}

	@Test
	public void refreshIndexTest() {
		pwPublicationDao.refreshTextIndex();
	}

}
