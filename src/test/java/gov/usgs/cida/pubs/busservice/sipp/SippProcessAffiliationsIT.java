package gov.usgs.cida.pubs.busservice.sipp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.CostCenterBusService;
import gov.usgs.cida.pubs.busservice.CrossRefBusService;
import gov.usgs.cida.pubs.busservice.OutsideAffiliationBusService;
import gov.usgs.cida.pubs.busservice.PersonContributorBusService;
import gov.usgs.cida.pubs.busservice.ext.ExtAffiliationBusService;
import gov.usgs.cida.pubs.busservice.ext.ExtPublicationContributorService;
import gov.usgs.cida.pubs.busservice.ext.ExtPublicationService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.intfc.IIpdsPubTypeConvDao;
import gov.usgs.cida.pubs.dao.intfc.IPersonContributorDao;
import gov.usgs.cida.pubs.dao.mp.MpListDao;
import gov.usgs.cida.pubs.dao.mp.MpListPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationContributorDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationCostCenterDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationLinkDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.busservice.mp.MpPublicationBusService;
import gov.usgs.cida.pubs.busservice.mp.MpPublicationContributorBusService;
import gov.usgs.cida.pubs.busservice.mp.MpPublicationCostCenterBusService;
import gov.usgs.cida.pubs.busservice.mp.MpPublicationLinkBusService;
import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.dao.ContributorDao;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.dao.CrossRefLogDao;
import gov.usgs.cida.pubs.dao.LinkTypeDao;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.dao.PublicationTypeDao;
import gov.usgs.cida.pubs.dao.sipp.InformationProductDao;
import gov.usgs.cida.pubs.dao.sipp.IpdsPubTypeConvDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.sipp.InformationProduct;
import gov.usgs.cida.pubs.domain.sipp.IpdsPubTypeConv;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import net.sf.saxon.expr.JPConverter.FromDate;

@ContextConfiguration(classes = FreeMarkerAutoConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { DbTestConfig.class, LocalValidatorFactoryBean.class,
		IpdsPubTypeConvDao.class, IpdsPubTypeConv.class, InformationProduct.class, InformationProductDao.class,
		PublicationDao.class, PublicationTypeDao.class, PublicationSubtypeDao.class, PwPublicationDao.class,
		PwPublication.class, PublicationSeriesDao.class, ExtPublicationService.class, ExtAffiliationBusService.class,
		LinkTypeDao.class, MpPublicationBusService.class, MpPublication.class, MpPublicationDao.class,
		MpListPublicationDao.class, MpListDao.class, MpPublicationLinkDao.class, MpPublicationContributorDao.class,
		MpPublicationCostCenterDao.class, AffiliationDao.class, MpPublicationCostCenterBusService.class,
		MpPublicationContributorBusService.class, MpPublicationLinkBusService.class, ContributorDao.class,
		ContributorTypeDao.class, CostCenterDao.class, CostCenterBusService.class, OutsideAffiliationDao.class,
		OutsideAffiliationBusService.class, ExtPublicationContributorService.class, IPersonContributorDao.class,
		PersonContributorDao.class, PersonContributorBusService.class, CrossRefBusService.class, PubsEMailer.class,
		CrossRefLogDao.class, ConfigurationService.class, SippProcess.class, SippConversionService.class })
public class SippProcessAffiliationsIT extends BaseIT {
	protected static final String IP_NUMBER_ARTICLE = "IP-109296";
	protected static final int NUMBER_USGS_CONTRIBUTORS = 2;
	protected static final int NUMBER_OUTSIDE_CONTRIBUTORS = 6;
	protected static final int NUMBER_CONTRIBUTORS = NUMBER_USGS_CONTRIBUTORS + NUMBER_OUTSIDE_CONTRIBUTORS;

	protected static final List<String> USGS_CONTRIB_NAMES = List.of("Yule", "Gorman");
	protected static final List<String> OUTSIDE_CONTRIB_NAMES = List.of("Dobosenski", "Meyers", "Ebener", "Claramunt",
			"McKenna", "Ketola");

	protected static final String AFFILIATION_COMMON_NAME = "Great Lakes Science Center";
	protected static final Map<String, String> usgsContributorAffiliations = usgsContribMap();
	protected static final Map<String, String> outsideContributorAffiliations = outsideContribMap();

	@Autowired
	public Validator validator;

	@Autowired
	@Qualifier("freeMarkerConfiguration")
	public Configuration templateConfig;

	@Autowired
	protected PublicationSeriesDao publicationSeriesDao;

	@MockBean(name = "informationProductDao")
	public InformationProductDao informationProductDao;

	@Autowired
	protected IIpdsPubTypeConvDao ipdsPubTypeConvDao;

	@Autowired
	protected IpdsPubTypeConv ipdsPubTypeConv;

	@Autowired
	public PwPublicationDao pwPublicationDao;

	@Autowired
	public PwPublication pwPublication;

	@MockBean
	protected PlatformTransactionManager transactionManager;

	@MockBean
	protected TransactionStatus transactionStatus;

	@Autowired
	protected ExtPublicationService extPublicationService;

	@Autowired()
	@Qualifier("personContributorDao")
	protected PersonContributorDao personContributorDao;

	@Autowired
	@Qualifier("contributorDao")
	protected ContributorDao contributorDao;

	@Autowired
	@Qualifier("contributorTypeDao")
	protected ContributorTypeDao contributorTypeDao;

	@Autowired
	@Qualifier("costCenterDao")
	protected CostCenterDao costCenterDao;

	@Autowired
	@Qualifier("linkTypeDao")
	protected LinkTypeDao linkTypeDao;

	@Autowired
	@Qualifier("mpListDao")
	protected MpListDao mpListDao;

	@Autowired
	@Qualifier("mpListPublicationDao")
	protected MpListPublicationDao mpListPublicationDao;

	@Autowired
	@Qualifier("mpPublicationContributorDao")
	protected MpPublicationContributorDao mpPublicationContributorDao;

	@Autowired
	@Qualifier("mpPublicationCostCenterDao")
	protected MpPublicationCostCenterDao mpPublicationCostCenterDao;

	@Autowired
	@Qualifier("mpPublicationLinkDao")
	protected MpPublicationLinkDao mpPublicationLinkDao;

	@Autowired
	@Qualifier("outsideAffiliationDao")
	protected OutsideAffiliationDao outsideAffiliationDao;

	@Autowired
	@Qualifier("publicationTypeDao")
	protected PublicationTypeDao publicationTypeDao;

	@Autowired
	@Qualifier("publicationSubtypeDao")
	protected PublicationSubtypeDao publicationSubtypeDao;

	@Autowired
	@Qualifier("affiliationDao")
	protected AffiliationDao<?> affiliationDao;

	@Autowired
	protected IMpPublicationBusService pubBusService;

	@Autowired
	protected SippConversionService sippConversionService;

	protected SippProcess sippProcess;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeEach
	public void setUp() throws Exception {
		sippProcess = new SippProcess(extPublicationService, pubBusService, sippConversionService, transactionManager);
		new UsgsContributor().setPersonContributorDao(personContributorDao);
		new CostCenter().setCostCenterDao(costCenterDao);
		new LinkType().setLinkTypeDao(linkTypeDao);
		new MpList().setMpListDao(mpListDao);
		new MpListPublication().setMpListPublicationDao(mpListPublicationDao);
		new MpPublicationContributor().setMpPublicationContributorDao(mpPublicationContributorDao);
		new MpPublicationCostCenter().setMpPublicationCostCenterDao(mpPublicationCostCenterDao);
		new MpPublicationLink().setMpPublicationLinkDao(mpPublicationLinkDao);
		new OutsideAffiliation().setOutsideAffiliationDao(outsideAffiliationDao);
		new PublicationSeries().setPublicationSeriesDao(publicationSeriesDao);
		new PublicationType().setPublicationTypeDao(publicationTypeDao);
		new PublicationSubtype().setPublicationSubtypeDao(publicationSubtypeDao);
		new Contributor().setContributorDao(contributorDao);
		new ContributorType().setContributorTypeDao(contributorTypeDao);
		new InformationProduct().setInformationProductDao(informationProductDao);
		new Affiliation().setAffiliationDao(affiliationDao);
	}

	/*
	 * The jist of this test is to make sure a cost center (usgs) can have the same name as an 
	 * outside affiliation. Both records end up in the affiliation table.
	 */
	@Test
	@DatabaseSetups({ @DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
			@DatabaseSetup("classpath:/testData/publicationType.xml"),
			@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
			@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
			@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml") })
	public void sippPublicationContributorsTest() throws Exception {
		when(informationProductDao.getInformationProduct(IP_NUMBER_ARTICLE))
				.thenReturn(getDisseminationFromXml("dissemination-affiliations.xml"));
		when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(transactionStatus);
		when(transactionStatus.isRollbackOnly()).thenReturn(false);

		MpPublication mpPublication = sippProcess.processInformationProduct(ProcessType.DISSEMINATION,
				IP_NUMBER_ARTICLE);
		assertNotNull(mpPublication);

		// retrieve from Database
		MpPublication pubFromDb = pubBusService.getByIndexId(mpPublication.getIndexId());
		assertNotNull(pubFromDb);
		assertEquals(pubFromDb.getId(), mpPublication.getId());
		assertEquals(pubFromDb.getIndexId(), mpPublication.getIndexId());

		Collection<PublicationContributor<?>> contributors = pubFromDb.getContributors();
		assertNotNull(contributors);
		assertEquals(NUMBER_CONTRIBUTORS, contributors.size(),
				String.format("Expected %d Authors in publication", NUMBER_CONTRIBUTORS));
		assertPublication(pubFromDb);
		assertContributors(contributors);
		assertPublicationCostCenter(pubFromDb);

	}

	private void assertPublication(MpPublication mpPublication) {
		assertNotNull(mpPublication);
		assertTrue(mpPublication.isValid(),
				"Validation errors in Sipp processed publication: " + mpPublication.getValidationErrors());
		assertNotNull(mpPublication.getId());
		assertTrue(mpPublication.getId() > 0);
		assertEquals("Disseminated", mpPublication.getIpdsReviewProcessState());

		assertEquals("Does Fecundity of Cisco Vary in the Upper Great Lakes?", mpPublication.getTitle());
		assertNotNull(mpPublication.getPublicationType());
		assertNotNull(mpPublication.getPublicationSubtype());

		assertNull(mpPublication.getLargerWorkTitle());
		assertEquals("North American Journal of Fisheries Management", mpPublication.getSeriesTitle().getText());

		assertNotNull(mpPublication.getSeriesTitle().getPublicationSubtype());
	}

	private void assertContributors(Collection<PublicationContributor<?>> contributors) {
		ArrayList<String> usgsContributorNames = new ArrayList<>();
		ArrayList<String> outsideContributorNames = new ArrayList<>();
		for (PublicationContributor<?> contributor : contributors) {
			assertNotNull(contributor.getContributor());
			assertNotNull(contributor.getContributor().getId());
			int contributorId = contributor.getContributor().getId();
			Contributor<?> fromDb = contributorDao.getById(contributorId);
			assertNotNull(fromDb);
			assertEquals(fromDb.getId(), contributorId);
			if (fromDb instanceof UsgsContributor) {
				UsgsContributor usgsContributor = (UsgsContributor) fromDb;
				assertTrue(USGS_CONTRIB_NAMES.contains(usgsContributor.getFamily()));
				assertFalse(usgsContributorNames.contains(usgsContributor.getFamily()));
				assertUsgsContributor(usgsContributor);
				usgsContributorNames.add(usgsContributor.getFamily());
			} else if (fromDb instanceof OutsideContributor) {
				OutsideContributor outsideContributor = (OutsideContributor) fromDb;
				assertTrue(OUTSIDE_CONTRIB_NAMES.contains(outsideContributor.getFamily()));
				assertFalse(outsideContributorNames.contains(outsideContributor.getFamily()));
				assertOutsideContributor(outsideContributor);
				outsideContributorNames.add(outsideContributor.getFamily());
			} else {
				fail("Unexpected contributor type" + fromDb.getClass());
			}
		}
		assertEquals(NUMBER_USGS_CONTRIBUTORS, usgsContributorNames.size());
		assertEquals(NUMBER_OUTSIDE_CONTRIBUTORS, outsideContributorNames.size());
	}

	private void assertUsgsContributor(UsgsContributor contributor) {
		Collection<Affiliation<? extends Affiliation<?>>> affiliations = contributor.getAffiliations();
		assertNotNull(affiliations);
		assertEquals(1, affiliations.size());
		Object[] affiliationObjs = affiliations.toArray();
		assertTrue(affiliationObjs[0] instanceof CostCenter);
		CostCenter costCenter = (CostCenter) affiliationObjs[0];
		assertTrue(costCenter.isValid());
		assertTrue(costCenter.isUsgs());
		assertTrue(usgsContributorAffiliations.containsKey(contributor.getFamily()));
		String affiliation = usgsContributorAffiliations.get(contributor.getFamily());
		assertNotNull(affiliation);
		assertEquals(affiliation, costCenter.getText());
	}

	private void assertOutsideContributor(OutsideContributor contributor) {
		Collection<Affiliation<? extends Affiliation<?>>> affiliations = contributor.getAffiliations();
		assertNotNull(affiliations);
		assertEquals(1, affiliations.size());
		Object[] affiliationObjs = affiliations.toArray();
		assertTrue(affiliationObjs[0] instanceof OutsideAffiliation);
		OutsideAffiliation outsideAffiliation = (OutsideAffiliation) affiliationObjs[0];
		assertTrue(outsideAffiliation.isValid());
		assertFalse(outsideAffiliation.isUsgs());
		assertTrue(outsideContributorAffiliations.containsKey(contributor.getFamily()));
		String affiliation = outsideContributorAffiliations.get(contributor.getFamily());
		assertNotNull(affiliation);
		assertEquals(affiliation, outsideAffiliation.getText());
	}

	private void assertPublicationCostCenter(MpPublication mpPublication) {
		assertNotNull(mpPublication);
		Collection<PublicationCostCenter<?>> costCenters = mpPublication.getCostCenters();
		assertNotNull(costCenters);
		assertEquals(1, costCenters.size());
		Object[] costCenterObjs = costCenters.toArray();
		assertTrue(costCenterObjs[0] instanceof MpPublicationCostCenter);

		MpPublicationCostCenter mpPublicationCostCenter = (MpPublicationCostCenter) costCenterObjs[0];
		assertEquals(mpPublicationCostCenter.getPublicationId(), mpPublication.getId());

		CostCenter costCenter = mpPublicationCostCenter.getCostCenter();
		assertNotNull(costCenter);
		assertTrue(costCenter.isActive());
		assertTrue(costCenter.isUsgs());
		assertTrue(costCenter.isValid());
		assertEquals(AFFILIATION_COMMON_NAME, costCenter.getText());
	}

	private InformationProduct getDisseminationFromXml(String xmlFile) throws Exception {
		XmlMapper xmlMapper = new XmlMapper();
		return xmlMapper.readValue(getFile("testData/sipp/" + xmlFile), InformationProduct.class);
	}

	// map of author last name --> affiliation name
	private static Map<String, String> usgsContribMap() {
		Map<String, String> affiliationMap = new HashMap<>();

		affiliationMap.put("Yule", AFFILIATION_COMMON_NAME);
		affiliationMap.put("Gorman", AFFILIATION_COMMON_NAME);

		return affiliationMap;
	}

	// map of author last name --> affiliation name
	private static Map<String, String> outsideContribMap() {
		Map<String, String> affiliationMap = new HashMap<>();

		affiliationMap.put("Dobosenski", "Wisconsin Department of Natural Resources");
		affiliationMap.put("Meyers", "U.S. Fish and Wildlife Service");
		affiliationMap.put("Ebener", "The Fresh Lake Whitefish Company");
		affiliationMap.put("Claramunt", "Michigan Department of Natural Resources");
		affiliationMap.put("McKenna", AFFILIATION_COMMON_NAME);
		affiliationMap.put("Ketola", AFFILIATION_COMMON_NAME);

		return affiliationMap;
	}

}
