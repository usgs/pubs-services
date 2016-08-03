package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoTest;
import gov.usgs.cida.pubs.dao.mp.MpPublicationLinkDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationIndex;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationTest;
import gov.usgs.cida.pubs.utility.PubsUtilitiesTest;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.webservice.security.PubsRoles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class MpPublicationBusServiceTest extends BaseSpringTest {

	public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors", "costCenters", "contributors", "contributorsToMap", "links",
			"doi", "indexId", "interactions", "sourceDatabase", "published");

	public Integer lockTimeoutHours = 1;

	@Autowired
	public Validator validator;

	@Mock
	private ICrossRefBusService crossRefBusService;

	@Autowired
	public IListBusService<PublicationCostCenter<MpPublicationCostCenter>> ccBusService;

	@Autowired
	public IListBusService<PublicationLink<MpPublicationLink>> linkBusService;

	@Autowired
	public IListBusService<PublicationContributor<MpPublicationContributor>> contributorBusService;

	@Autowired
	public String warehouseEndpoint;

	private MpPublicationBusService busService;

	@Before
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		busService = new MpPublicationBusService(validator, lockTimeoutHours, crossRefBusService, ccBusService, linkBusService, contributorBusService, warehouseEndpoint);
	}

	@Test
	public void getObjectTest() {
		busService.getObject(null);
		assertNull(busService.getObject(-1));
		assertNotNull(busService.getObject(1));
		MpPublication mpPub = busService.getObject(1);
		MpPublicationDaoTest.assertMpPub1(mpPub, PubsConstants.ANONYMOUS_USER);
		MpPublicationDaoTest.assertMpPub1Children(mpPub);
	}

	@Test
	public void getByIndexIdTest() {
		busService.getByIndexId(null);
		assertNull(busService.getByIndexId("9"));
		MpPublication mpPub = busService.getByIndexId(MpPublicationDaoTest.MPPUB1_INDEXID);
		MpPublicationDaoTest.assertMpPub1(mpPub, MpPublicationDaoTest.MPPUB1_LOCKEDBY);
		MpPublicationDaoTest.assertMpPub1Children(mpPub);
	}

	@Test
	public void getObjectsTest() {
		busService.getObjects(null);
		busService.getObjects(new HashMap<String, Object>());

		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.PROD_ID, new int[] {-1});
		Collection<MpPublication> pubs = busService.getObjects(filters);
		assertNotNull(pubs);
		assertEquals(0, pubs.size());

		filters.put(PublicationDao.PROD_ID, new int[] {1});
		pubs = busService.getObjects(filters);
		assertNotNull(pubs);
		assertEquals(1, pubs.size());
	}

	@Test
	public void createObjectTest() {
		//TODO both a good create and a create w/validation errors.
		//public MpPublication createObject(MpPublication object)
		busService.createObject(null);

		MpPublication pub = busService.createObject(new MpPublication());
		assertNotNull(pub.getId());
	}

	@Test
	public void updateObjectTest() {
		busService.updateObject(null);
		busService.updateObject(new MpPublication());

		MpPublication pub = MpPublicationDaoTest.updatePubProperties(MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId()));
		MpPublication after = busService.updateObject(MpPublicationDaoTest.updatePubProperties(pub));
		assertDaoTestResults(MpPublication.class, pub, after, IGNORE_PROPERTIES, true, true);
		assertEquals(pub.getId().toString(), after.getIndexId());
		assertEquals("doiname2", after.getDoi());

		pub = MpPublicationDaoTest.updatePubProperties(MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId()));
		MpPublication mid = MpPublicationDaoTest.updatePubProperties(pub);
		mid.setIpdsId("12345678901234567890");
		after = busService.updateObject(mid);
		assertDaoTestResults(MpPublication.class, pub, after, IGNORE_PROPERTIES, true, true);
//		assertEquals(4, after.getValidationErrors().getValidationErrors().size());
		assertEquals(3, after.getValidationErrors().getValidationErrors().size());
	}

	@Test
	public void deleteObjectTest() {
		//TODO a delete w/validation errors.
		//public ValidationResults deleteObject(MpPublication object)
		busService.deleteObject(null);
		busService.deleteObject(-1);

		busService.deleteObject(2);
		assertMpPublicationDeleted(2);
	}

	@Test
	public void preProcessingTest() {
		busService.publicationPreProcessing(null);
		busService.publicationPreProcessing(new MpPublication());

		MpPublication inPublication = new MpPublication();
		PublicationType pubType = new PublicationType();
		pubType.setId(PublicationType.REPORT);
		inPublication.setPublicationType(pubType);
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		inPublication.setPublicationSubtype(pubSubtype);
		PublicationSeries pubSeries = new PublicationSeries();
		pubSeries.setId(PublicationSeries.SIR);
		inPublication.setSeriesTitle(pubSeries);
		inPublication.setSeriesNumber("nu-m,be r");
		inPublication.setTitle("test\nnewline\rcarriage\n\rreturn\n\n\r\r");
		MpPublication outPublication = busService.publicationPreProcessing(inPublication);
		assertNotNull(outPublication);
		assertNotNull(outPublication.getId());
		assertEquals("sirnumber", outPublication.getIndexId());
		assertEquals(PubsConstants.DOI_PREFIX + "/" + outPublication.getIndexId(), outPublication.getDoi());
		assertEquals("testnewlinecarriagereturn", outPublication.getTitle());

		inPublication = new MpPublication();
		inPublication.setSeriesNumber("nu-m,be r");
		inPublication.setId(123);
		outPublication = busService.publicationPreProcessing(inPublication);
		assertNotNull(outPublication);
		assertNotNull(outPublication.getId());
		assertEquals("123", outPublication.getIndexId());
		assertNull(outPublication.getDoi());

		inPublication = new MpPublication();
		pubSeries.setId(508);
		inPublication.setSeriesTitle(pubSeries);
		outPublication = busService.publicationPreProcessing(inPublication);
		assertNotNull(outPublication);
		assertNotNull(outPublication.getId());
		assertEquals(outPublication.getId().toString(), outPublication.getIndexId());
		assertNull(outPublication.getDoi());

		inPublication = new MpPublication();
		pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
		inPublication.setPublicationType(pubType);
		inPublication.setPublicationSubtype(pubSubtype);
		inPublication.setSeriesTitle(pubSeries);
		outPublication = busService.publicationPreProcessing(inPublication);
		assertNotNull(outPublication);
		assertNotNull(outPublication.getId());
		assertEquals(outPublication.getId().toString(), outPublication.getIndexId());
		assertEquals(PubsConstants.DOI_PREFIX + "/" + outPublication.getIndexId(), outPublication.getDoi());

		//Test that we cannot update indexId or doi on a USGS series
		inPublication = new MpPublication();
		inPublication.setId(4);
		inPublication.setIndexId("inIndexId");
		inPublication.setDoi("inDoi");
		inPublication.setPublicationType(pubType);
		pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
		inPublication.setPublicationSubtype(pubSubtype);
		outPublication = busService.publicationPreProcessing(inPublication);
		assertNotNull(outPublication);
		assertNotNull(outPublication.getId());
		assertEquals("4", outPublication.getIndexId());
		assertEquals("doi", outPublication.getDoi());

		inPublication = new MpPublication();
		inPublication.setId(4);
		inPublication.setIndexId("inIndexId");
		inPublication.setDoi("inDoi");
		inPublication.setPublicationType(pubType);
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		inPublication.setPublicationSubtype(pubSubtype);
		outPublication = busService.publicationPreProcessing(inPublication);
		assertNotNull(outPublication);
		assertNotNull(outPublication.getId());
		assertEquals("4", outPublication.getIndexId());
		assertEquals("doi", outPublication.getDoi());

		//Test that we can update doi (but not indexId) when not a USGS series
		inPublication = new MpPublication();
		inPublication.setId(4);
		inPublication.setIndexId("inIndexId");
		inPublication.setDoi("inDoi");
		inPublication.setPublicationType(pubType);
		outPublication = busService.publicationPreProcessing(inPublication);
		assertNotNull(outPublication);
		assertNotNull(outPublication.getId());
		assertEquals("4", outPublication.getIndexId());
		assertEquals("inDoi", outPublication.getDoi());

		//Test that we can update doi (but not indexId) when a USGS series without a doi
		inPublication = new MpPublication();
		inPublication.setId(5);
		inPublication.setIndexId("inIndexId");
		inPublication.setDoi("inDoi");
		inPublication.setPublicationType(pubType);
		pubSubtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		inPublication.setPublicationSubtype(pubSubtype);
		outPublication = busService.publicationPreProcessing(inPublication);
		assertNotNull(outPublication);
		assertNotNull(outPublication.getId());
		assertEquals("9", outPublication.getIndexId());
		assertEquals("inDoi", outPublication.getDoi());

		inPublication = new MpPublication();
		inPublication.setId(5);
		inPublication.setIndexId("inIndexId");
		inPublication.setDoi("inDoi");
		inPublication.setPublicationType(pubType);
		pubSubtype.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
		inPublication.setPublicationSubtype(pubSubtype);
		outPublication = busService.publicationPreProcessing(inPublication);
		assertNotNull(outPublication);
		assertNotNull(outPublication.getId());
		assertEquals("9", outPublication.getIndexId());
		assertEquals("inDoi", outPublication.getDoi());
	}

	@Test
	public void getUsgsNumberedSeriesIndexId() {
		assertNull(busService.getUsgsNumberedSeriesIndexId(null));
		assertNull(busService.getUsgsNumberedSeriesIndexId(new MpPublication()));
		MpPublication pub = new MpPublication();
		PublicationSeries pubSeries = new PublicationSeries();
		pub.setSeriesTitle(pubSeries);
		assertNull(busService.getUsgsNumberedSeriesIndexId(pub));
		pubSeries.setId(-1);
		assertNull(busService.getUsgsNumberedSeriesIndexId(pub));
		pubSeries.setId(330);
		assertNull(busService.getUsgsNumberedSeriesIndexId(pub));
		pub.setSeriesNumber( "1- 2-3,4,5 6");
		assertEquals("ofr123456", busService.getUsgsNumberedSeriesIndexId(pub));
		pub.setChapter("abc");
		pub.setSubchapterNumber("123");
		assertEquals("ofr123456ABC123", busService.getUsgsNumberedSeriesIndexId(pub));
	}

	@Test
	public void doiNameTest() {
		assertNull(MpPublicationBusService.getDoiName(null));
		assertNull(MpPublicationBusService.getDoiName(""));
		assertEquals(PubsConstants.DOI_PREFIX + "/abc", MpPublicationBusService.getDoiName("abc"));
	}

	@Test
	public void publicationPostProcessingTest() {
		assertNull(busService.publicationPostProcessing(null));
		MpPublication pub = busService.publicationPostProcessing(new MpPublication());
		assertNull(pub);

		//Check CostCenters merged
		pub = busService.getObject(1);
		Collection<PublicationCostCenter<?>> costCenters = pub.getCostCenters();
		costCenters.remove(costCenters.toArray()[0]);
		MpPublicationCostCenter cc = new MpPublicationCostCenter();
		cc.setCostCenter((CostCenter) CostCenter.getDao().getById(4));
		costCenters.add(cc);
		pub = busService.publicationPostProcessing(pub);
		assertEquals(2, pub.getCostCenters().size());
		boolean gotCc2 = false;
		boolean gotCc4 = false;
		for (Object i : pub.getCostCenters().toArray()) {
			if (i instanceof MpPublicationCostCenter) {
				if (2 == ((MpPublicationCostCenter) i).getCostCenter().getId()) {
					gotCc2 = true;
				} else if (4 == ((MpPublicationCostCenter) i).getCostCenter().getId()) {
					gotCc4 = true;
				}
			}
		}
		assertTrue(gotCc2);
		assertTrue(gotCc4);

		//Check Links merged
		pub = busService.getObject(1);
		Collection<PublicationLink<?>> links = pub.getLinks();
		links.remove(links.toArray()[0]);
		MpPublicationLink link = new MpPublicationLink();
		link.setPublicationId(1);
		link.setRank(3);
		link.setDescription("new merge");
		links.add(link);
		pub = busService.publicationPostProcessing(pub);
		assertEquals(2, pub.getLinks().size());
		boolean gotLink1 = false;
		boolean gotLink2 = false;
		boolean gotLink3 = false;
		for (PublicationLink<?> added : pub.getLinks()) {
			assertEquals(1, added.getPublicationId().intValue());
			if (1 == added.getRank()) {
				gotLink1 = true;
			} else if (2 == added.getRank()) {
				gotLink2 = true;
			} else if (3 == added.getRank()) {
				gotLink3 = true;
			}
		}
		assertFalse(gotLink1);
		assertTrue(gotLink2);
		assertTrue(gotLink3);

		//Check Contributors merged
		pub = busService.getObject(1);
		Collection<PublicationContributor<?>> contributors = pub.getContributors();
		contributors.remove(contributors.toArray()[0]);
		MpPublicationContributor author = new MpPublicationContributor();
		author.setPublicationId(1);
		author.setContributorType(ContributorType.getDao().getById(ContributorType.AUTHORS));
		author.setContributor(Contributor.getDao().getById(3));
		author.setRank(80);
		contributors.add(author);
		pub = busService.publicationPostProcessing(pub);
		assertEquals(4, pub.getContributors().size());
		boolean gotAuth1 = false;
		boolean gotAuth2 = false;
		boolean gotAuth3 = false;
		boolean gotEdit1 = false;
		boolean gotEdit2 = false;
		for (PublicationContributor<?> added : pub.getContributors()) {
			assertEquals(1, added.getPublicationId().intValue());
			if (ContributorType.AUTHORS == added.getContributorType().getId()) {
				if (1 == added.getContributor().getId()) {
					gotAuth1 = true;
				} else if (2 == added.getContributor().getId()) {
					gotAuth2 = true;
				} else if (3 == added.getContributor().getId()) {
					gotAuth3 = true;
				}
			} else if (ContributorType.EDITORS == added.getContributorType().getId()) {
				if (1 == added.getContributor().getId()) {
					gotEdit1 = true;
				} else if (2 == added.getContributor().getId()) {
					gotEdit2 = true;
				}
			}
		}
		assertFalse(gotAuth1);
		assertTrue(gotAuth2);
		assertTrue(gotAuth3);
		assertTrue(gotEdit1);
		assertTrue(gotEdit2);

	}

	@Test
	public void beginPublicationEditTest() {
		//This one should change nothing
		busService.beginPublicationEdit(2);
		Publication<?> mpPub2after = MpPublication.getDao().getById(2);
		MpPublicationDaoTest.assertMpPub2(mpPub2after, PubsConstants.ANONYMOUS_USER);
		MpPublicationDaoTest.assertMpPub2Children(mpPub2after);

		//This one is in PW, not MP and should be moved
		MpPublication mpPub4before = MpPublication.getDao().getById(4);
		assertNull(mpPub4before);
		busService.beginPublicationEdit(4);
		MpPublication mpPub4after = MpPublication.getDao().getById(4);
		PwPublicationTest.assertPwPub4(mpPub4after);
		PwPublicationTest.assertPwPub4Children(mpPub4after);
		assertEquals(PubsConstants.ANONYMOUS_USER, mpPub4after.getLockUsername());
	}

	@Test
	public void checkLocksTest() {
		//nulls = OK (assume this is an add)
		assertNull(busService.checkAvailability(null));

		//No lockedUsername = OK
		assertNull(busService.checkAvailability(3));

		//Not expired = not OK
		assertEquals("drsteini", busService.checkAvailability(1).getValue());

		//Same user = OK
		PubsUtilitiesTest.buildTestAuthentication("drsteini");
		assertNull(busService.checkAvailability(1));

		//Expired = OK (We are testing by setting the timeout to 0 and -1 for these test)
//TODO make less brittle and re-activate
//		PubsUtilitiesTest.clearTestAuthentication();
//		busService = new MpPublicationBusService(validator, 0, crossRefBusService, ccBusService, linkBusService, contributorBusService);
//		assertNull(busService.checkAvailability(1));
//		busService = new MpPublicationBusService(validator, -1, crossRefBusService, ccBusService, linkBusService, contributorBusService);
//		assertNull(busService.checkAvailability(1));
	}

	@Test
	public void releaseLocksUserTest() {
		MpPublication mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
		busService.releaseLocksUser(PubsConstants.ANONYMOUS_USER);
		mpPub = MpPublication.getDao().getById(mpPub.getId());
		assertNull(mpPub.getLockUsername());

		//this one was also anonymous
		mpPub = MpPublication.getDao().getById(2);
		assertNull(mpPub.getLockUsername());

		//this should still be locked
		mpPub = MpPublication.getDao().getById(1);
		assertEquals("drsteini", mpPub.getLockUsername());

		busService.releaseLocksUser("drsteini");
		mpPub = MpPublication.getDao().getById(1);
		assertNull(mpPub.getLockUsername());
	}

	@Test
	public void releaseLocksPubTest() {
		MpPublication mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
		busService.releaseLocksPub(mpPub.getId());
		mpPub = MpPublication.getDao().getById(mpPub.getId());
		assertNull(mpPub.getLockUsername());

		mpPub = MpPublication.getDao().getById(2);
		assertEquals(PubsConstants.ANONYMOUS_USER,mpPub.getLockUsername());

		mpPub = MpPublication.getDao().getById(1);
		assertEquals("drsteini", mpPub.getLockUsername());

		busService.releaseLocksPub(1);
		mpPub = MpPublication.getDao().getById(1);
		assertNull(mpPub.getLockUsername());
	}

	@Test
	public void setListTest() {
		busService.setList(null);
		MpPublication mpPub = new MpPublication();
		busService.setList(mpPub);
		mpPub.setId(1);
		busService.setList(mpPub);

		mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
		busService.setList(mpPub);
		Map<String, Object> filters = new HashMap<>();
		filters.put("publicationId", mpPub.getId());
		List<MpListPublication> lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(1, lists.size());
		testLists(lists, false, false, false, true, false);

		PublicationType pubType = new PublicationType();
		pubType.setId(PublicationType.ARTICLE);
		mpPub.setPublicationType(pubType);
		busService.setList(mpPub);
		lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(2, lists.size());
		testLists(lists, true, false, false, true, false);

		pubType.setId(4);
		mpPub.setPublicationSubtype(null);
		busService.setList(mpPub);
		lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(3, lists.size());
		testLists(lists, true, true, false, true, false);

		mpPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		mpPub.setPublishingServiceCenter(new PublishingServiceCenter());
		busService.setList(mpPub);
		lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(4, lists.size());
		testLists(lists, true, true, true, true, false);

		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(PublicationSubtype.USGS_DATA_RELEASE);
		mpPub.setPublicationSubtype(pubSubtype);
		mpPub.setIpdsReviewProcessState(null);
		busService.setList(mpPub);
		lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(5, lists.size());
		testLists(lists, true, true, true, true, true);

		pubSubtype.setId(PublicationSubtype.USGS_WEBSITE);
		mpPub.setIpdsReviewProcessState(null);
		busService.setList(mpPub);
		lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(6, lists.size());
		testLists(lists, true, true, true, true, true);

		mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
		mpPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		PublishingServiceCenter psc = new PublishingServiceCenter();
		psc.setId(6);
		mpPub.setPublishingServiceCenter(psc);
		busService.setList(mpPub);
		filters.put("publicationId", mpPub.getId());
		lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(1, lists.size());
		assertEquals(9, lists.get(0).getMpList().getId().intValue());

		//This one should avoid all list logic since it is in the warehouse
		mpPub = MpPublicationDaoTest.addAPub(4);
		mpPub.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		mpPub.setPublishingServiceCenter(psc);
		busService.setList(mpPub);
		filters.put("publicationId", mpPub.getId());
		lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(0, lists.size());
	}

	@Test
	public void setList2Test() {
		//NPE tests
		busService.setList(null, null);
		MpPublication mpPub = new MpPublication();
		busService.setList(mpPub, null);
		mpPub.setId(1);
		busService.setList(mpPub, null);
		busService.setList(null, MpList.IPDS_OTHER_PUBS);

		//New List
		mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
		busService.setList(mpPub, MpList.IPDS_OTHER_PUBS);
		Map<String, Object> filters = new HashMap<>();
		filters.put("publicationId", mpPub.getId());
		List<MpListPublication> lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(1, lists.size());
		testLists(lists, false, true, false, false, false);

		//Add List
		busService.setList(mpPub, MpList.IPDS_USGS_NUMBERED_SERIES);
		lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(2, lists.size());
		testLists(lists, false, true, false, true, false);

		//Update List
		busService.setList(mpPub, MpList.IPDS_OTHER_PUBS);
		lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(2, lists.size());
		testLists(lists, false, true, false, true, false);
	}

	public void testLists(List<MpListPublication> lists, boolean expect_journal, boolean expect_other,
			boolean expect_pending, boolean expect_numbered, boolean expect_website) {
		boolean got_journal = false;
		boolean got_other = false;
		boolean got_pending = false;
		boolean got_numbered = false;
		boolean got_website = false;

		for (MpListPublication list : lists) {
			if (MpList.IPDS_JOURNAL_ARTICLES.contentEquals(list.getMpList().getId().toString())) {
				got_journal = true;
			} else 	if (MpList.IPDS_OTHER_PUBS.contentEquals(list.getMpList().getId().toString())) {
				got_other = true;
			} else 	if (MpList.PENDING_USGS_SERIES.contentEquals(list.getMpList().getId().toString())) {
				got_pending = true;
			} else 	if (MpList.IPDS_USGS_NUMBERED_SERIES.contentEquals(list.getMpList().getId().toString())) {
				got_numbered = true;
			} else 	if (MpList.USGS_DATA_RELEASES.contentEquals(list.getMpList().getId().toString())) {
				got_website = true;
			}
		}
		assertEquals(expect_journal, got_journal);
		assertEquals(expect_other, got_other);
		assertEquals(expect_pending, got_pending);
		assertEquals(expect_numbered, got_numbered);
		assertEquals(expect_website, got_website);
	}

	@Test
	public void defaultThumbnailTest() {
		busService.defaultThumbnail(null);
		MpPublication mpPub = new MpPublication();
		busService.defaultThumbnail(mpPub);

		mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
		busService.defaultThumbnail(mpPub);
		Map<String, Object> filters = new HashMap<>();
		filters.put(MpPublicationLinkDao.PUB_SEARCH, mpPub.getId());
		List<MpPublicationLink> links = MpPublicationLink.getDao().getByMap(filters);
		assertEquals(1, links.size());
		MpPublicationLink link = links.get(0);
		assertEquals(LinkType.THUMBNAIL, link.getLinkType().getId());
		assertEquals(warehouseEndpoint + MpPublicationLink.USGS_THUMBNAIL, link.getUrl());

		mpPub = MpPublicationDaoTest.addAPub(MpPublication.getDao().getNewProdId());
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(1);
		mpPub.setPublicationSubtype(pubSubtype);
		busService.defaultThumbnail(mpPub);
		filters.clear();
		filters.put(MpPublicationLinkDao.PUB_SEARCH, mpPub.getId());
		links = MpPublicationLink.getDao().getByMap(filters);
		assertEquals(1, links.size());
		link = links.get(0);
		assertEquals(LinkType.THUMBNAIL, link.getLinkType().getId());
		assertEquals(warehouseEndpoint + MpPublicationLink.EXTERNAL_THUMBNAIL, link.getUrl());

		link.setUrl("something else");
		MpPublicationLink.getDao().update(link);
		busService.defaultThumbnail(mpPub);
		links = MpPublicationLink.getDao().getByMap(filters);
		assertEquals(1, links.size());
		link = links.get(0);
		assertEquals(LinkType.THUMBNAIL, link.getLinkType().getId());
		assertEquals("something else", link.getUrl());
	}

	@Test
	public void publishTest() {
		assertTrue(busService.publish(null).isEmpty());
		assertEquals("Field:Publication - Message:Publication does not exist. - Level:FATAL - Value:-1\nValidator Results: 1 result(s)\n",
				busService.publish(-1).toString());

		ValidationResults valRes = busService.publish(2);
		assertTrue(valRes.isEmpty());
		Publication<?> pub = PwPublication.getDao().getById(2);
		MpPublicationDaoTest.assertPwPub2(pub);
		assertEquals(2, pub.getContributors().size());
		//Link count is one more than in the dataset.xml because a default thumbnail is added by the service.
		assertEquals(2, pub.getLinks().size());
		assertEquals(1, pub.getCostCenters().size());
		PublicationIndex pi = PublicationIndex.getDao().getById(2);
		assertNotNull(pi);
		assertEquals("title the abstract subseries title series number 2 ipdsid Report USGS Numbered Series Professional Paper PP ConFamily, ConGiven, ConSuffix US Geological Survey Ice Survey Team xAffiliation Cost Center 4    ", pi.getQ());

		assertMpPublicationDeleted(2);
	}

	@Test
	public void publishSPNTest() {
		//Stuff published by an SPN User should end up in both the warehouse and MyPubs on the USGS Series list.
		buildTestAuthentication("dummy", new ArrayList<>(Arrays.asList(PubsRoles.PUBS_SPN_USER.name())));
		ValidationResults valRes = busService.publish(2);
		assertTrue(valRes.isEmpty());
		Publication<?> pub = PwPublication.getDao().getById(2);
		MpPublicationDaoTest.assertPwPub2(pub);
		assertEquals(2, pub.getContributors().size());
		//Link count is one more than in the dataset.xml because a default thumbnail is added by the service.
		assertEquals(2, pub.getLinks().size());
		assertEquals(1, pub.getCostCenters().size());
		PublicationIndex pi = PublicationIndex.getDao().getById(2);
		assertNotNull(pi);
		assertEquals("title the abstract subseries title series number 2 ipdsid Report USGS Numbered Series Professional Paper PP ConFamily, ConGiven, ConSuffix US Geological Survey Ice Survey Team xAffiliation Cost Center 4    ", pi.getQ());

		//Still in MP
		MpPublication mpPub = MpPublication.getDao().getById(2);
		MpPublicationDaoTest.assertMpPub2(mpPub, "dummy");

		//On the list
		Map<String, Object> filters = new HashMap<>();
		filters.put("publicationId", 2);
		filters.put("mpListId", MpList.IPDS_USGS_NUMBERED_SERIES);
		List<MpListPublication> lists = MpListPublication.getDao().getByMap(filters);
		assertEquals(1, lists.size());
		testLists(lists, false, false, false, true, false);
	}

	public void assertMpPublicationDeleted(Integer id) {
		assertNull(MpPublication.getDao().getById(id));
		Map<String, Object> filters = new HashMap<>();
		filters.put("publicationId", id);
		assertTrue(MpPublicationLink.getDao().getByMap(filters).isEmpty());
		assertTrue(MpPublicationCostCenter.getDao().getByMap(filters).isEmpty());
		assertTrue(MpPublicationContributor.getDao().getByMap(filters).isEmpty());
		assertTrue(MpListPublication.getDao().getByMap(filters).isEmpty());
	}

}
