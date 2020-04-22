package gov.usgs.cida.pubs.dao.pw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.ImmutableMap;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationDaoIT;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;
import gov.usgs.cida.pubs.domain.query.PwPublicationFilterParams;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, PwPublicationDao.class, ConfigurationService.class,
			PwPublicationFilterParams.class})
public class PwPublicationDaoIT extends BaseIT {

	@Autowired
	private PwPublicationDao pwPublicationDao;
	@MockBean
	private ConfigurationService configurationService;

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
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void getByMapTestOrcid() {
		Map<String, Object> filters = new HashMap<>();
		String[] orcids = new String[] { "0000-0000-0000-0001", "0000-0000-0000-0004" };
		for(String orcid : orcids) {
			filters.clear();
			filters.put(PersonContributorDao.ORCID, new String[] {orcid});

			List<PwPublication> pubs = pwPublicationDao.getByMap(filters);
			assertNotNull(pubs);
			assertTrue(pubs.size() > 0, String.format("Expected filter match on orcid '%s', got none", orcid));

			for(Publication<?> pub : pubs) {
				assertNotNull(pub, "Null publication returned from db query");
				assertNotNull(pub.getId(), "Pubication returned from db query has null id");
				assertTrue(pub.getId() > 0, "Publication returned from query has invalid id: " + pub.getId());
				assertTrue(pub.isValid(), String.format("Publication (id=%d) has validation errors:",pub.getId(), pub.getValidationErrors().toString()));
				List<String> orcidList = getOrcids(pub);
				assertTrue(orcidList.contains(orcid),
						String.format("Expected orcid '%s' in publication (id=%d) returned from query, got orcids: %s" , 
								orcid, pub.getId(), Arrays.toString(orcidList.toArray()))
						);
			}
		}

		// try both orcids in a single query
		filters.clear();
		filters.put(PersonContributorDao.ORCID, orcids);
		List<PwPublication> pubs = pwPublicationDao.getByMap(filters);
		assertNotNull(pubs);
		assertEquals(2, pubs.size(), String.format("Expected two matches on orcids '%s', got none", Arrays.toString(orcids)));
		List<String> foundOrcidList = new ArrayList<>();

		for(Publication<?> pub : pubs) {
			foundOrcidList.addAll(getOrcids(pub));
		}
		String[] foundOrcids = foundOrcidList.toArray(new String[foundOrcidList.size()]);
		Arrays.sort(foundOrcids);
		assertTrue(Arrays.equals(orcids, foundOrcids),
				String.format("Expected orcids %s from query specifying multiple oids, got: %s", Arrays.toString(orcids), Arrays.toString(foundOrcids))
				);

		// test for orcid being undefined in the filter, these queries should match on all publications
		String[] emptyOrcids = new String[]{ null, ""};
		for(String orcid : emptyOrcids) {
			filters.clear();
			filters.put(PersonContributorDao.ORCID, new String[]{orcid});
			pubs = pwPublicationDao.getByMap(filters);
			assertNotNull(pubs);
			String orcidDesc = orcid == null ? "null" : orcid.isEmpty() ? "empty string" : orcid;
			assertTrue(pubs.isEmpty(), String.format("Expected %d matches on filter with orcid %s, got %d", 4, orcidDesc, pubs.size()));
		}

		// try a few illegal orcids that might fool query into a match
		String[] badOrcids = new String[]{"0000", "0000-0000-0000-00001", "0000-0000-0000-000A", "any"};
		for(String orcid : badOrcids) {
			filters.clear();
			filters.put(PersonContributorDao.ORCID, new String[]{orcid});
			pubs = pwPublicationDao.getByMap(filters);
			assertNotNull(pubs);
			assertTrue(pubs.isEmpty(), String.format("Expected no matches on filter with orcid '%s', got %d", orcid, pubs.size()));
		}
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
		pwPublicationDao.stream(PwPublicationDao.NS + PwPublicationDao.GET_STREAM_BY_MAP, buildAllFilterParms(), null);
		//TODO add in real filter tests
	}

	@Test
	public void getPoJoStreamByMapTest() {
		//This only checks that the final query is syntactically correct, not that it is logically correct!
		pwPublicationDao.stream(PwPublicationDao.NS + PwPublicationDao.GET_BY_MAP, buildAllFilterParms(), null);
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
		assertEquals(4, pubs.size());
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
		List<String> expectedIndexIds = List.of("sir2", "sir3").stream().sorted().collect(Collectors.toList());
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

	@Test
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
	@DatabaseSetup("classpath:/testData/purgeTest/common/")
	@DatabaseSetup("classpath:/testData/purgeTest/pw/")
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/pw/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/common/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void purgePublication() {
		//This one is not found and should not cause an error
		pwPublicationDao.purgePublication(668);

		//This one should delete
		pwPublicationDao.purgePublication(2);
	}

	@Test
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
	@DatabaseSetup("classpath:/testData/relatedPublications.xml")
	public void getRelatedPublications() {
		//Just a part of
		List<Map<String, Object>> related = pwPublicationDao.getRelatedPublications(1);
		assertEquals(1, related.size());
		assertEquals("[publication_id, index_id, doi_name, relation]", related.get(0).keySet().toString());
		assertEquals("[2, index2, doi2, isPartOf]", related.get(0).values().toString());

		//Just a supersedes
		related = pwPublicationDao.getRelatedPublications(2);
		assertEquals(1, related.size());
		assertEquals("[publication_id, index_id, doi_name, relation]", related.get(0).keySet().toString());
		assertEquals("[3, index3, doi3, supersededBy]", related.get(0).values().toString());

		//List of both in specified order
		related = pwPublicationDao.getRelatedPublications(4);
		assertEquals(4, related.size());
		assertEquals("[publication_id, index_id, doi_name, relation]", related.get(0).keySet().toString());
		assertEquals("[5, index5, doi5, isPartOf]", related.get(0).values().toString());
		assertEquals("[6, index6, doi6, isPartOf]", related.get(1).values().toString());
		assertEquals("[7, index7, doi7, supersededBy]", related.get(2).values().toString());
		assertEquals("[8, index8, doi8, supersededBy]", related.get(3).values().toString());
	}

	private List<String> getOrcids(Publication<?> pub) {
		ArrayList<String> orcidList = new ArrayList<>();

		for(PublicationContributor<?> contributor : pub.getContributors()) {
			if(contributor.getContributor() instanceof PersonContributor) {
				String orcid = ((PersonContributor<?>)contributor.getContributor()).getOrcid();
				if(!orcidList.contains(orcid)) {
					orcidList.add(orcid);
				}
			}
		}

		return orcidList;
	}

	public PwPublicationFilterParams buildAllFilterParms() {
		PwPublicationFilterParams filters = new PwPublicationFilterParams();

		filters.setPubAbstract(new String[]{"abstract1", "abstractp"});
		filters.setChorus(true);
		filters.setContributingOffice(new String[]{"contributingOffice1", "contributingOffice2"});
		filters.setContributor(new String[] {"contributor1", "contributor2"});

		filters.setDoi(new String[]{"DOI-123", "DOI-456"});
		filters.setHasDoi(true);

		filters.setEndYear("yearEnd");
		filters.setG(SEARCH_POLYGON);
		filters.setIndexId(new String[]{"indexId1", "indexId2"});
		filters.setIpdsId(new String[]{"ipdsId1", "ipdsId2"});

		filters.setListId(new String[]{"1", "2"});
		filters.setMod_date_high("2012-12-12");
		filters.setMod_date_low("2010-10-10");
		filters.setMod_x_days("3");
		filters.setOrderBy("title");

		filters.setPage_number("66");
		filters.setPage_row_start("19");
		filters.setPage_size("54");
		filters.setProdId(new String[]{"1", "2"});
		filters.setPub_date_high("2012-12-12");

		filters.setPub_date_low("2010-10-10");
		filters.setPub_x_days("1");
		filters.setQ("$turtles");

		filters.setLinkType(new String[]{"linkType1", "linkType2"});
		filters.setNoLinkType(new String[]{"noLinkType1", "noLinkType2"});
		filters.setReportNumber(new String[]{"reportNumber1", "reportNumber2"});
		filters.setSearchTerms(new String[]{"searchTerms1", "searchTerms2"});

		filters.setSeriesName(new String[]{"reportSeries1", "reportSeries2"});
		filters.setStartYear("yearStart");
		filters.setSubtypeName(new String[]{"subtype1", "subtype2"});
		filters.setTitle(new String[]{"title1", "title2"});

		filters.setTypeName(new String[]{"type1", "type2"});
		filters.setYear(new String[]{"year1", "year2"});

		return filters;
	}
}
