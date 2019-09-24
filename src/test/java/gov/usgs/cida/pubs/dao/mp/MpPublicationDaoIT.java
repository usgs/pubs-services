package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationIT;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, MpPublication.class, MpPublicationDao.class,
			PublicationDao.class, PwPublicationDao.class,
			MpListPublication.class, MpListPublicationDao.class,
			MpPublicationContributor.class, MpPublicationContributorDao.class,
			MpPublicationCostCenter.class, MpPublicationCostCenterDao.class,
			MpPublicationLink.class, MpPublicationLinkDao.class})
public class MpPublicationDaoIT extends BaseIT {

	//TODO contributors, links, & CostCenters in test.
	public static final List<String> IGNORE_PROPERTIES = List.of("validationErrors", "valErrors", "costCenters",
			"contributors", "contributorsToMap", "links", "interactions", "sourceDatabase", "published", "ipdsContext");

	public static final String MPPUB1_INDEXID = "sir20145083";
	public static final String MPPUB1_LOCKEDBY = "drsteini";

	@Autowired
	MpPublicationDao mpPublicationDao;
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
	public void addAndGetByIds() {
		Integer pubId = mpPublicationDao.getNewProdId();
		MpPublication newpubA = addAPub(pubId);
		MpPublication persistedA = mpPublicationDao.getById(pubId);
		assertNotNull(persistedA);
		assertNotNull(persistedA.getId());
		assertDaoTestResults(MpPublication.class, newpubA, persistedA, IGNORE_PROPERTIES, true, true);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PublicationDao.PROD_ID, new int[] { pubId });
		List<MpPublication> pubs = mpPublicationDao.getByMap(params);
		assertTrue(pubs.size() > 0);
		Integer cnt = mpPublicationDao.getObjectCount(params);
		assertEquals(cnt.intValue(), pubs.size());
		MpPublication persistedB = pubs.get(0);
		assertNotNull(persistedB);
		assertNotNull(persistedB.getId());
		assertDaoTestResults(MpPublication.class, persistedA, persistedB, IGNORE_PROPERTIES, false, true);

		persistedA = updatePubProperties(persistedA);
		mpPublicationDao.update(persistedA);

		MpPublication persistedC = mpPublicationDao.getById(pubId);
		assertNotNull(persistedC);
		assertNotNull(persistedC.getId());
		assertDaoTestResults(MpPublication.class, persistedA, persistedC, IGNORE_PROPERTIES, true, true);

		mpPublicationDao.delete(persistedC);
		assertNull(mpPublicationDao.getById(pubId));
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/mpPublicationOrderBy.xml")
	})
	public void getByMapTest() {
		Map<String, Object> filters = new HashMap<>();
		List<MpPublication> pubs = mpPublicationDao.getByMap(filters);
		assertEquals(24, pubs.size());
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
		assertEquals(380, pubs.get(14).getId().intValue());
		assertEquals(140, pubs.get(15).getId().intValue());
		assertEquals(420, pubs.get(16).getId().intValue());
		assertEquals(180, pubs.get(17).getId().intValue());
		assertEquals(460, pubs.get(18).getId().intValue());
		assertEquals(220, pubs.get(19).getId().intValue());
		assertEquals(500, pubs.get(20).getId().intValue());
		assertEquals(260, pubs.get(21).getId().intValue());
		assertEquals(540, pubs.get(22).getId().intValue());
		assertEquals(300, pubs.get(23).getId().intValue());

		filters.put(PublicationDao.ORDER_BY, "title");
		pubs = mpPublicationDao.getByMap(filters);
		assertEquals(24, pubs.size());
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
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void copyFromPwTest() {
		mpPublicationDao.copyFromPw(4);
		Publication<MpPublication> mpPub = mpPublicationDao.getById(4);
		PwPublicationTest.assertPwPub4(mpPub);
		assertTrue(((MpPublication) mpPub).isPublished());
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void lockPubTest() {
		mpPublicationDao.lockPub(3);
		MpPublication mpPub = mpPublicationDao.getById(3);
		assertEquals(PubsConstantsHelper.ANONYMOUS_USER, mpPub.getLockUsername());
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void releaseLocksUserTest() {
		MpPublication mpPub = addAPub(mpPublicationDao.getNewProdId());
		mpPublicationDao.releaseLocksUser(PubsConstantsHelper.ANONYMOUS_USER);
		mpPub = mpPublicationDao.getById(mpPub.getId());
		assertNull(mpPub.getLockUsername());

		//this one was also anonymous
		mpPub = mpPublicationDao.getById(2);
		assertNull(mpPub.getLockUsername());

		//this should still be locked
		mpPub = mpPublicationDao.getById(1);
		assertEquals("drsteini", mpPub.getLockUsername());

		mpPublicationDao.releaseLocksUser("drsteini");
		mpPub = mpPublicationDao.getById(1);
		assertNull(mpPub.getLockUsername());
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void releaseLocksPubTest() {
		MpPublication mpPub = addAPub(mpPublicationDao.getNewProdId());
		mpPublicationDao.releaseLocksPub(mpPub.getId());
		mpPub = mpPublicationDao.getById(mpPub.getId());
		assertNull(mpPub.getLockUsername());

		mpPub = mpPublicationDao.getById(2);
		assertEquals(PubsConstantsHelper.ANONYMOUS_USER,mpPub.getLockUsername());

		mpPub = mpPublicationDao.getById(1);
		assertEquals("drsteini", mpPub.getLockUsername());

		mpPublicationDao.releaseLocksPub(1);
		mpPub = mpPublicationDao.getById(1);
		assertNull(mpPub.getLockUsername());
	}

	@Test
	@DatabaseSetups({
		@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
		@DatabaseSetup("classpath:/testData/publicationType.xml"),
		@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
		@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
		@DatabaseSetup("classpath:/testData/dataset.xml")
	})
	public void publishToPwTest() {
		mpPublicationDao.publishToPw(null);
		mpPublicationDao.publishToPw(-1);
		
		//this one should be a straight add.
		mpPublicationDao.publishToPw(1);
		assertPwPub1(pwPublicationDao.getById(1));

		//this one should be a merge.
		mpPublicationDao.publishToPw(4);
		PwPublicationTest.assertPwPub4(pwPublicationDao.getById(4));
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
		MpPublication pub = mpPublicationDao.getByIndexId(MPPUB1_INDEXID);
		assertMpPub1(pub, MPPUB1_LOCKEDBY);

		//This one is only in Publication, so we shouldn't frind it
		assertNull(mpPublicationDao.getByIndexId("9"));
	}

	@Test
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
	@DatabaseSetup("classpath:/testData/purgeTest/common/")
	@DatabaseSetup("classpath:/testData/purgeTest/mp/")
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/mp/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	@ExpectedDatabase(
			value="classpath:/testResult/purgeTest/common/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED)
	public void purgePublication() {
		//This one is not found and should not cause an error
		mpPublicationDao.purgePublication(668);

		//This one should delete
		mpPublicationDao.purgePublication(2);
	}

	public static void assertMpPub1(Publication<?> pub, String expectedLockUsername) {
		assertTrue(pub instanceof MpPublication);
		assertPub1(pub);
		assertEquals(expectedLockUsername, ((MpPublication) pub).getLockUsername());
	}

	public static void assertPwPub1(Publication<?> pub) {
		assertTrue(pub instanceof PwPublication);
		assertPub1(pub);
	}

	public static void assertPub1(Publication<?> pub) {
		assertEquals(1, pub.getId().intValue());
		assertEquals("sir20145083", pub.getIndexId());
		assertEquals("2014-07-14T17:27:36", pub.getDisplayToPublicDate().toString());
		assertEquals(18, pub.getPublicationType().getId().intValue());
		assertEquals(5, pub.getPublicationSubtype().getId().intValue());
		assertEquals(334, pub.getSeriesTitle().getId().intValue());
		assertEquals("2014-5083", pub.getSeriesNumber());
		assertEquals("Climate Change Adaption Series", pub.getSubseriesTitle());
		assertNull(pub.getChapter());
		assertNull(pub.getSubchapterNumber());
		assertEquals("Monitoring recharge in areas of seasonally frozen ground in the Columbia Plateau and Snake River Plain, Idaho, Oregon, and Washington", pub.getTitle());
		assertNull(pub.getDocAbstract());
		assertEquals("English", pub.getLanguage());
		assertEquals("U.S. Geological Survey", pub.getPublisher());
		assertEquals("Reston, VA", pub.getPublisherLocation());
		assertEquals("10.3133/sir20145083", pub.getDoi());
		assertNull(pub.getIssn());
		assertNull(pub.getIsbn());
		assertEquals("Written in collaboration with the National Snow and Ice Data Center", pub.getCollaboration());
		assertNull(pub.getUsgsCitation());
		assertNull(pub.getProductDescription());
		assertNull(pub.getStartPage());
		assertNull(pub.getEndPage());
		assertNull(pub.getNumberOfPages());
		assertNull(pub.getOnlineOnly());
		assertNull(pub.getAdditionalOnlineFiles());
		assertEquals("2014-07-14", pub.getTemporalStart().toString());
		assertEquals("2014-07-20", pub.getTemporalEnd().toString());
		assertNull(pub.getNotes());
		assertNull(pub.getIpdsId());
		assertNull(pub.getIpdsReviewProcessState());
		assertNull(pub.getIpdsInternalId());
		assertEquals("2014", pub.getPublicationYear());
		assertFalse(pub.isNoYear());
		assertEquals("Some Journal", pub.getLargerWorkTitle());
		assertEquals(23, pub.getLargerWorkType().getId().intValue());
		assertEquals("Conference Title", pub.getConferenceTitle());
		assertEquals("A free form DATE", pub.getConferenceDate());
		assertEquals("A conference location", pub.getConferenceLocation());
		assertEquals("100", pub.getScale());
		assertEquals(23, pub.getLargerWorkSubtype().getId().intValue());
		assertEquals("EPSG:3857", pub.getProjection());
		assertEquals("NAD83", pub.getDatum());
		assertEquals("USA", pub.getCountry());
		assertEquals("WI", pub.getState());
		assertEquals("DANE", pub.getCounty());
		assertEquals("MIDDLETON", pub.getCity());
		assertEquals("On the moon", pub.getOtherGeospatial());
		assertEquals("VOL123", pub.getVolume());
		assertEquals("IS IIVI", pub.getIssue());
		assertEquals(GEOGRAPHIC_EXTENTS, pub.getGeographicExtents());
		assertEquals("My Contact Info", pub.getContact());
		assertEquals("Edition X", pub.getEdition());
		assertEquals("just a little comment", pub.getComments());
		assertEquals("tbl contents", pub.getTableOfContents());
		assertEquals(6, pub.getPublishingServiceCenter().getId().intValue());
		assertEquals("2002-02-02", pub.getPublishedDate().toString());
		assertEquals("2003-03-03", pub.getRevisedDate().toString());
	}

	public static void assertMpPub1Children(Publication<?> pub) {
		assertEquals(4, pub.getContributors().size());
		assertEquals(2, pub.getCostCenters().size());
		assertEquals(2, pub.getLinks().size());
	}

	public static void assertMpPub2(Publication<?> pub, String expectedLockUsername) {
		assertTrue(pub instanceof MpPublication);
		assertPub2(pub);
		assertEquals(expectedLockUsername, ((MpPublication) pub).getLockUsername());
	}

	public static void assertPwPub2(Publication<?> pub) {
		assertTrue(pub instanceof PwPublication);
		assertPub2(pub);
	}

	public static void assertPub2(Publication<?> pub) {
		assertEquals(2, pub.getId().intValue());
		assertEquals("2", pub.getIndexId());
		assertEquals("2014-07-22T20:06:10", pub.getDisplayToPublicDate().toString());
		assertEquals(18, pub.getPublicationType().getId().intValue());
		assertEquals(5, pub.getPublicationSubtype().getId().intValue());
		assertEquals(331, pub.getSeriesTitle().getId().intValue());
		assertEquals("series number", pub.getSeriesNumber());
		assertEquals("subseries title", pub.getSubseriesTitle());
		assertEquals("chapter", pub.getChapter());
		assertEquals("subchapter title", pub.getSubchapterNumber());
		assertEquals("display title", pub.getDisplayTitle());
		assertEquals("title", pub.getTitle());
		assertEquals("the abstract", pub.getDocAbstract());
		assertEquals("language", pub.getLanguage());
		assertEquals("publisher", pub.getPublisher());
		assertEquals("publicsher location", pub.getPublisherLocation());
		assertEquals("doi", pub.getDoi());
		assertEquals("issn", pub.getIssn());
		assertEquals("isbn", pub.getIsbn());
		assertEquals("collaboration", pub.getCollaboration());
		assertEquals("usgs citation", pub.getUsgsCitation());
		assertEquals("product description", pub.getProductDescription());
		assertEquals("start", pub.getStartPage());
		assertEquals("end", pub.getEndPage());
		assertEquals("1", pub.getNumberOfPages());
		assertEquals("N", pub.getOnlineOnly());
		assertEquals("Y", pub.getAdditionalOnlineFiles());
		assertEquals("2014-07-14", pub.getTemporalStart().toString());
		assertEquals("2014-07-20", pub.getTemporalEnd().toString());
		assertEquals("some notes", pub.getNotes());
		assertEquals("ipdsid", pub.getIpdsId());
		assertEquals("reviewprocessstate", pub.getIpdsReviewProcessState());
		assertEquals("123", pub.getIpdsInternalId());

		assertNull(pub.getPublicationYear());
		assertTrue(pub.isNoYear());
		assertNull(pub.getLargerWorkTitle());
		assertNull(pub.getLargerWorkType());
		assertNull(pub.getConferenceTitle());
		assertNull(pub.getConferenceDate());
		assertNull(pub.getPublicationYear());

		assertNull(pub.getScale());
		assertNull(pub.getProjection());
		assertNull(pub.getDatum());
		assertNull(pub.getCountry());
		assertNull(pub.getState());
		assertNull(pub.getCounty());
		assertNull(pub.getCity());
		assertNull(pub.getOtherGeospatial());
		assertNull(pub.getGeographicExtents());

		assertNull(pub.getContact());
		assertNull(pub.getEdition());
		assertNull(pub.getComments());
		assertNull(pub.getTableOfContents());

		assertEquals(7, pub.getPublishingServiceCenter().getId().intValue());
		assertEquals("2003-03-03", pub.getPublishedDate().toString());
		assertEquals(4, pub.getIsPartOf().getId().intValue());
		assertEquals(6, pub.getSupersededBy().getId().intValue());

	}

	public static void assertMpPub2Children(Publication<?> pub) {
		assertEquals(2, pub.getContributors().size());
		assertEquals(1, pub.getCostCenters().size());
		assertEquals(1, pub.getLinks().size());
	}

	public static MpPublication buildAPub(final Integer pubId) {
		MpPublication newPub = (MpPublication) PublicationIT.buildAPub(new MpPublication(), pubId);
		newPub.setLockUsername(PubsConstantsHelper.ANONYMOUS_USER);
		return newPub;
	}

	public static MpPublication addAPub(final Integer pubId) {
		MpPublication newPub = buildAPub(pubId);
		MpPublication.getDao().add(newPub);
		MpPublication.getDao().lockPub(newPub.getId());
		return newPub;
	}

	public static MpPublication updatePubProperties(final MpPublication pubToUpdate) {
		MpPublication updatedPub = pubToUpdate;
		updatedPub.setIndexId("indexid2" + updatedPub.getId());
		updatedPub.setDisplayToPublicDate(LocalDateTime.of(2012, 8, 23, 0, 0, 0));
		PublicationType pubType = new PublicationType();
		pubType.setId(23);
		updatedPub.setPublicationType(pubType);
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(22);
		updatedPub.setPublicationSubtype(pubSubtype);
		PublicationSeries pubSeries = new PublicationSeries();
		pubSeries.setId(501);
		updatedPub.setSeriesTitle(pubSeries);
		updatedPub.setSeriesNumber("Series Number2");
		updatedPub.setSubseriesTitle("subseries2");
		updatedPub.setChapter("chapter2");
		updatedPub.setSubchapterNumber("subchapter2");
		updatedPub.setDisplayTitle("display title 2");
		updatedPub.setTitle("Title2");
		updatedPub.setDocAbstract("Abstract Text2");
		updatedPub.setLanguage("Language2");
		updatedPub.setPublisher("Publisher2");
		updatedPub.setPublisherLocation("Publisher Location2");
		updatedPub.setDoi("doiname2");
		updatedPub.setIssn("inIssn2");
		updatedPub.setIsbn("inIsbn2");
		updatedPub.setCollaboration("collaboration2");
		updatedPub.setUsgsCitation("usgscitation2");
		updatedPub.setProductDescription("Prod Description2");
		updatedPub.setStartPage("inStartPage2");
		updatedPub.setEndPage("inEndPage2");
		updatedPub.setNumberOfPages("6");
		updatedPub.setOnlineOnly("2");
		updatedPub.setAdditionalOnlineFiles("2");
		updatedPub.setTemporalStart(LocalDate.of(2010,10,10));
		updatedPub.setTemporalEnd(LocalDate.of(2012,12,12));
		updatedPub.setNotes("notes2");
		updatedPub.setIpdsId("ipds_i2" + updatedPub.getId());
		updatedPub.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
		updatedPub.setIpdsInternalId("122");
		updatedPub.setPublicationYear("2001");
		updatedPub.setNoYear(true);
		updatedPub.setLargerWorkTitle("Larger Work Title");
		PublicationType largerWorkType = new PublicationType();
		largerWorkType.setId(PublicationType.ARTICLE);
		updatedPub.setLargerWorkType(largerWorkType);
		updatedPub.setConferenceDate("a new free form date");
		updatedPub.setConferenceTitle("A new title");
		updatedPub.setConferenceLocation("a new conference location");
		updatedPub.setVolume("VOL13");
		updatedPub.setIssue("ISIX");
		updatedPub.setContact("My Contact InfoU");
		updatedPub.setEdition("Edition XU");
		updatedPub.setComments("just a little commentU");
		updatedPub.setTableOfContents("tbl contentsU");
		updatedPub.setPublishedDate(LocalDate.of(2010,10,11));
		PublishingServiceCenter publishingServiceCenter = new PublishingServiceCenter();
		publishingServiceCenter.setId(2);
		updatedPub.setPublishingServiceCenter(publishingServiceCenter);
		Publication<?> po = new MpPublication();
		po.setId(2);
		updatedPub.setIsPartOf(po);
		Publication<?> sb = new MpPublication();
		sb.setId(1);
		updatedPub.setSupersededBy(sb);
		updatedPub.setRevisedDate(LocalDate.of(2007,7,7));
		return updatedPub;
	}
}
