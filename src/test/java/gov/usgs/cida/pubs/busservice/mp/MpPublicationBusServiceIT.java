package gov.usgs.cida.pubs.busservice.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.ReactorContextTestExecutionListener;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.busservice.intfc.ICrossRefBusService;
import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.dao.ContributorDao;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.dao.LinkTypeDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.dao.PublicationTypeDao;
import gov.usgs.cida.pubs.dao.PublishingServiceCenterDao;
import gov.usgs.cida.pubs.dao.mp.MpListDao;
import gov.usgs.cida.pubs.dao.mp.MpListPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationContributorDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationCostCenterDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDaoIT;
import gov.usgs.cida.pubs.dao.mp.MpPublicationLinkDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationIndexHelper;
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
import gov.usgs.cida.pubs.security.UserDetailTestService;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.utility.PubsUtilitiesTest;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, LocalValidatorFactoryBean.class, ConfigurationService.class,
			MpPublicationCostCenterBusService.class, MpPublicationLinkBusService.class,
			MpPublicationContributorBusService.class, MpPublication.class, MpPublicationDao.class,
			PublicationDao.class, PwPublication.class, PwPublicationDao.class,
			MpList.class, MpListDao.class, MpListPublication.class, MpListPublicationDao.class,
			MpPublicationContributor.class, MpPublicationContributorDao.class,
			MpPublicationCostCenter.class, MpPublicationCostCenterDao.class,
			MpPublicationLink.class, MpPublicationLinkDao.class,
			CostCenter.class, CostCenterDao.class, AffiliationDao.class,
			ContributorType.class, ContributorTypeDao.class, Contributor.class, ContributorDao.class, PersonContributorDao.class,
			PublicationType.class, PublicationTypeDao.class, PublicationSubtype.class, PublicationSubtypeDao.class,
			PublicationSeries.class, PublicationSeriesDao.class, Publication.class, PublicationDao.class,
			PublishingServiceCenter.class, PublishingServiceCenterDao.class, LinkType.class, LinkTypeDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
//Needed to use @WithMockUser - @SecurityTestExecutionListeners and @ContextConfiguration interfere with @SpringBootTest
@TestExecutionListeners({WithSecurityContextTestExecutionListener.class, ReactorContextTestExecutionListener.class})
public class MpPublicationBusServiceIT extends BaseIT {

	public static final List<String> IGNORE_PROPERTIES = List.of("validationErrors", "valErrors", "costCenters", "contributors", "contributorsToMap", "links",
			"doi", "indexId", "interactions", "sourceDatabase", "published", "ipdsContext");

	public Integer lockTimeoutHours = 1;

	@Autowired
	public Validator validator;

	@MockBean
	private ICrossRefBusService crossRefBusService;

	@Autowired
	public IListBusService<PublicationCostCenter<MpPublicationCostCenter>> ccBusService;

	@Autowired
	public IListBusService<PublicationLink<MpPublicationLink>> linkBusService;

	@Autowired
	public IListBusService<PublicationContributor<MpPublicationContributor>> contributorBusService;

	@Autowired
	public ConfigurationService configurationService;

	private MpPublicationBusService busService;

	@Before
	public void initTest() throws Exception {
		busService = new MpPublicationBusService(validator, configurationService, crossRefBusService, ccBusService, linkBusService, contributorBusService);
	}

	@Test
	public void getObjectTest() {
		busService.getObject(null);
		assertNull(busService.getObject(-1));
		assertNotNull(busService.getObject(1));
		MpPublication mpPub = busService.getObject(1);
		MpPublicationDaoIT.assertMpPub1(mpPub, PubsConstantsHelper.ANONYMOUS_USER);
		MpPublicationDaoIT.assertMpPub1Children(mpPub);
	}

	@Test
	public void getByIndexIdTest() {
		busService.getByIndexId(null);
		assertNull(busService.getByIndexId("9"));
		MpPublication mpPub = busService.getByIndexId(MpPublicationDaoIT.MPPUB1_INDEXID);
		MpPublicationDaoIT.assertMpPub1(mpPub, MpPublicationDaoIT.MPPUB1_LOCKEDBY);
		MpPublicationDaoIT.assertMpPub1Children(mpPub);
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
	public void testCreateNull() {
		assertNull("null should return null", busService.createObject(null));
	}

	@Test
	public void testCreateEmpty() {
		MpPublication mpPub = new MpPublication();
		MpPublication newPub = busService.createObject(mpPub);
		ValidationResults validationErrors = newPub.getValidationErrors();
		assertFalse("Should be invalid", validationErrors.isEmpty());
		assertEquals("Two required fields", 2, validationErrors.getValidationErrors().size());
		for (ValidatorResult result : validationErrors.getValidationErrors()) {
			assertEquals(SeverityLevel.FATAL, result.getLevel());
			assertEquals(BaseValidatorTest.NOT_NULL_MSG, result.getMessage());
		}
	}

	@Test
	public void testCreateValid() {
		MpPublication mpPub = new MpPublication();
		
		mpPub.setTitle("newPubTitle");
		mpPub.setPublicationType(new PublicationType());
		mpPub.setIpdsId("zeroToFifteen");

		MpPublication newPub = busService.createObject(mpPub);
		ValidationResults validationErrors = newPub.getValidationErrors();

		assertTrue("Should be valid", validationErrors.isEmpty());
		assertNotNull(newPub.getId());
		assertEquals("newPubTitle", newPub.getTitle());
		assertEquals("zeroToFifteen", newPub.getIpdsId());
	}

	@Test
	public void testUpdateNull() {
		assertNull("null should return null", busService.updateObject(null));
	}

	@Test
	public void testUpdateIpdsIdEditableWhenNotPublished() {
		MpPublication original = MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId());
		original.setIpdsId("firstSetIpds");
		
		MpPublication firstUpdated = busService.updateObject(original);
		assertDaoTestResults(MpPublication.class, original, firstUpdated, IGNORE_PROPERTIES, true, true);
		assertEquals("Ipdsid should be set", "firstSetIpds", firstUpdated.getIpdsId());
		
		firstUpdated.setIpdsId("updatedIPDS");
		MpPublication newUpdate = busService.updateObject(firstUpdated);
		assertDaoTestResults(MpPublication.class, firstUpdated, newUpdate, IGNORE_PROPERTIES, true, true);
		assertEquals("Ipdsid should be updated", "updatedIPDS", newUpdate.getIpdsId());
	}

	@Test
	public void testUpdateIpdsIdEditableIfBlankWhenPublished() {
		MpPublication original = MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId());
		original.setIpdsId(null);
		MpPublication.getDao().update(original);
		assertNull("IpdsId should be null", original.getIpdsId());

		MpPublication.getDao().publishToPw(original.getId());

		original.setIpdsId("firstSetIpds");
		MpPublication firstUpdated = busService.updateObject(original);
		assertDaoTestResults(MpPublication.class, original, firstUpdated, IGNORE_PROPERTIES, true, true);
		assertEquals("IpdsId should be set", "firstSetIpds", firstUpdated.getIpdsId());
	}

	@Test
	public void testUpdateIpdsIdNotEditableIfSetWhenPublished() {
		MpPublication original = MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId());
		original.setIpdsId("originalIpds");
		
		MpPublication.getDao().publishToPw(original.getId());
		PwPublication published = PwPublication.getDao().getById(original.getId());

		original.setIpdsId("updateIpds");
		MpPublication firstUpdated = busService.updateObject(original);
		assertDaoTestResults(MpPublication.class, original, firstUpdated, IGNORE_PROPERTIES, true, true);
		assertEquals("IpdsId should not be changed", published.getIpdsId(), firstUpdated.getIpdsId());
	}
	
	@Test
	public void testUpdate() {
		MpPublication pub = MpPublicationDaoIT.updatePubProperties(MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId()));
		MpPublication after = busService.updateObject(MpPublicationDaoIT.updatePubProperties(pub));
		assertDaoTestResults(MpPublication.class, pub, after, IGNORE_PROPERTIES, true, true);
		assertEquals(pub.getId().toString(), after.getIndexId());
		assertEquals("doiname2", after.getDoi());

		pub = MpPublicationDaoIT.updatePubProperties(MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId()));
		MpPublication mid = MpPublicationDaoIT.updatePubProperties(pub);
		mid.setIpdsId("12345678901234567890");
		after = busService.updateObject(mid);
		assertDaoTestResults(MpPublication.class, pub, after, IGNORE_PROPERTIES, true, true);
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
		assertEquals(PubsConstantsHelper.DOI_PREFIX + "/" + outPublication.getIndexId(), outPublication.getDoi());
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
		assertEquals(PubsConstantsHelper.DOI_PREFIX + "/" + outPublication.getIndexId(), outPublication.getDoi());

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
	public void getUsgsNumberedSeriesIndexIdFromPub() {
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
	public void getUsgsNumberedSeriesIndexId() {
		assertNull(busService.getUsgsNumberedSeriesIndexId(null, null, null, null));
		assertNull(busService.getUsgsNumberedSeriesIndexId(new PublicationSeries(), null, null, null));
		PublicationSeries pubSeries = new PublicationSeries();
		pubSeries.setCode("ofr");;
		assertNull(busService.getUsgsNumberedSeriesIndexId(pubSeries, null, null, null));
		assertEquals("ofr123456", busService.getUsgsNumberedSeriesIndexId(pubSeries,"1- 2-3,4,5 6", null, null));
		assertEquals("ofr123456ABC", busService.getUsgsNumberedSeriesIndexId(pubSeries,"1- 2-3,4,5 6", "abc", null));
		assertEquals("ofr123456ABC123", busService.getUsgsNumberedSeriesIndexId(pubSeries,"1- 2-3,4,5 6", "abc", "123"));
		assertEquals("ofr123456123", busService.getUsgsNumberedSeriesIndexId(pubSeries,"1- 2-3,4,5 6", null, "123"));
	}

	@Test
	public void doiNameTest() {
		assertNull(MpPublicationBusService.getDoiName(null));
		assertNull(MpPublicationBusService.getDoiName(""));
		assertEquals(PubsConstantsHelper.DOI_PREFIX + "/abc", MpPublicationBusService.getDoiName("abc"));
	}

	@Test
	public void publicationPostProcessingTest() {
		assertNull(busService.publicationPostProcessing(null));
		MpPublication pub = busService.publicationPostProcessing(new MpPublication());
		assertNull(pub);

		//Check CostCenters merged
		pub = busService.getObject(1);
		Collection<PublicationCostCenter<?>> costCenters = pub.getCostCenters();

		Optional<PublicationCostCenter<?>> cc1 = costCenters.parallelStream()
				.filter(x -> x.getId() == 1)
				.findFirst();
		if (cc1.isPresent()) {
			costCenters.remove(cc1.get());
		} else {
			fail("database not set up properly - missing coster center 1");
		}

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

		Optional<PublicationLink<?>> pl1 = links.parallelStream()
				.filter(x -> x.getRank() == 1)
				.findFirst();
		if (pl1.isPresent()) {
			links.remove(pl1.get());
		} else {
			fail("database not set up properly - missing publication link with rank 1");
		}

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

		Optional<PublicationContributor<?>> pc1 = contributors.parallelStream()
				.filter(x -> x.getContributor().getId() == 1)
				.findFirst();
		if (pc1.isPresent()) {
			contributors.remove(pc1.get());
		} else {
			fail("database not set up properly - missing publication contributor 1");
		}

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
		MpPublicationDaoIT.assertMpPub2(mpPub2after, PubsConstantsHelper.ANONYMOUS_USER);
		MpPublicationDaoIT.assertMpPub2Children(mpPub2after);

		//This one is in PW, not MP and should be moved
		MpPublication mpPub4before = MpPublication.getDao().getById(4);
		assertNull(mpPub4before);
		busService.beginPublicationEdit(4);
		MpPublication mpPub4after = MpPublication.getDao().getById(4);
		PwPublicationTest.assertPwPub4(mpPub4after);
		PwPublicationTest.assertPwPub4Children(mpPub4after);
		assertEquals(PubsConstantsHelper.ANONYMOUS_USER, mpPub4after.getLockUsername());
	}

	@Test
	public void checkLocksTest() {
		//nulls = OK (assume this is an add)
		assertNull(busService.checkAvailability(null));

		//No lockedUsername = OK
		assertNull(busService.checkAvailability(3));

		//Not expired = not OK
		assertEquals("drsteini", busService.checkAvailability(1).getValue());
	}

	@Test
	@WithMockUser("drsteini")
	public void checkLocksTest_sameUser() {
		//Same user = OK
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
		MpPublication mpPub = MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId());
		busService.releaseLocksUser(PubsConstantsHelper.ANONYMOUS_USER);
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
		MpPublication mpPub = MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId());
		busService.releaseLocksPub(mpPub.getId());
		mpPub = MpPublication.getDao().getById(mpPub.getId());
		assertNull(mpPub.getLockUsername());

		mpPub = MpPublication.getDao().getById(2);
		assertEquals(PubsConstantsHelper.ANONYMOUS_USER,mpPub.getLockUsername());

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

		mpPub = MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId());
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

		mpPub = MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId());
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
		mpPub = MpPublicationDaoIT.addAPub(4);
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
		busService.setList(null, MpList.IPDS_OTHER_PUBS);
		MpPublication mpPub = new MpPublication();
		busService.setList(mpPub, MpList.IPDS_OTHER_PUBS);
		mpPub.setId(1);
		busService.setList(mpPub, MpList.IPDS_OTHER_PUBS);

		//New List
		mpPub = MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId());
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
			if (MpList.IPDS_JOURNAL_ARTICLES == list.getMpList().getId()) {
				got_journal = true;
			} else if (MpList.IPDS_OTHER_PUBS == list.getMpList().getId()) {
				got_other = true;
			} else if (MpList.PENDING_USGS_SERIES == list.getMpList().getId()) {
				got_pending = true;
			} else if (MpList.IPDS_USGS_NUMBERED_SERIES == list.getMpList().getId()) {
				got_numbered = true;
			} else if (MpList.USGS_DATA_RELEASES == list.getMpList().getId()) {
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

		mpPub = MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId());
		busService.defaultThumbnail(mpPub);
		Map<String, Object> filters = new HashMap<>();
		filters.put(MpPublicationLinkDao.PUB_SEARCH, mpPub.getId());
		List<MpPublicationLink> links = MpPublicationLink.getDao().getByMap(filters);
		assertEquals(1, links.size());
		MpPublicationLink link = links.get(0);
		assertEquals(LinkType.THUMBNAIL, link.getLinkType().getId());
		assertEquals(configurationService.getWarehouseEndpoint() + MpPublicationLink.USGS_THUMBNAIL, link.getUrl());

		mpPub = MpPublicationDaoIT.addAPub(MpPublication.getDao().getNewProdId());
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
		assertEquals(configurationService.getWarehouseEndpoint() + MpPublicationLink.EXTERNAL_THUMBNAIL, link.getUrl());

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
	@ExpectedDatabase(
			value="classpath:/testResult/publish_publication_index.xml",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table=PublicationIndexHelper.TABLE_NAME,
			query=PublicationIndexHelper.QUERY_TEXT)
	public void publishTest() {
		assertTrue(busService.publish(null).isEmpty());
		assertEquals("Field:Publication - Message:Publication does not exist. - Level:FATAL - Value:-1\nValidator Results: 1 result(s)\n",
				busService.publish(-1).toString());

		ValidationResults valRes = busService.publish(2);
		assertTrue(valRes.isEmpty());
		Publication<?> pub = PwPublication.getDao().getById(2);
		MpPublicationDaoIT.assertPwPub2(pub);
		assertEquals(2, pub.getContributors().size());
		//Link count is one more than in the dataset.xml because a default thumbnail is added by the service.
		assertEquals(2, pub.getLinks().size());
		assertEquals(1, pub.getCostCenters().size());

		assertMpPublicationDeleted(2);
	}

	@Test
	@WithMockUser(username=UserDetailTestService.SPN_USER, authorities={PubsUtilitiesTest.SPN_AUTHORITY})
	public void publishSPNTest() {
		//Stuff published by an SPN User should end up in both the warehouse and MyPubs on the USGS Series list.
		ValidationResults valRes = busService.publish(2);
		assertTrue(valRes.isEmpty());
		Publication<?> pub = PwPublication.getDao().getById(2);
		MpPublicationDaoIT.assertPwPub2(pub);
		assertEquals(2, pub.getContributors().size());
		//Link count is one more than in the dataset.xml because a default thumbnail is added by the service.
		assertEquals(2, pub.getLinks().size());
		assertEquals(1, pub.getCostCenters().size());

		//Still in MP
		MpPublication mpPub = MpPublication.getDao().getById(2);
		MpPublicationDaoIT.assertMpPub2(mpPub, UserDetailTestService.SPN_USER);

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
