package gov.usgs.cida.pubs.busservice.sipp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
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
import gov.usgs.cida.pubs.dao.PublicationSeriesDaoIT;
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
import gov.usgs.cida.pubs.domain.ProcessType;
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
public class SippProcessIT extends BaseIT {
	protected static final String IP_NUMBER_ARTICLE = "IP-117219";
	protected static final String IP_NUMBER_USGS_SERIES = "IP-114268";
	protected static final String USGS_PRODUCT_DESCRIPTION = "Enwright, N.M., SooHoo, W.M., Dugas, J.L., Lee, D.M., and Borrok, P.S.," +
			" 2018, Louisiana Barrier Island Comprehensive Monitoring Program – habitat mapping: U.S. Geological Survey data release," +
			" https://doi:10.5066/F7XP7440. (release date, 2/05/2018)";

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

	@Test
	@DatabaseSetups({ @DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
			@DatabaseSetup("classpath:/testData/publicationType.xml"),
			@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
			@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
			@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml") })
	public void sippPublicationExistingSeriesTest() throws Exception {
		when(informationProductDao.getInformationProduct(IP_NUMBER_ARTICLE))
				.thenReturn(getDisseminationWithExistingPubSeries());
		when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(transactionStatus);
		when(transactionStatus.isRollbackOnly()).thenReturn(false);
		String seriesTitle = "Iowa Highway Research Board Bulletin";
		PublicationSeries publicationsSeries = getSeriesByName(seriesTitle, null, 1);
		assertNotNull(publicationsSeries);
		assertEquals(5105, publicationsSeries.getId());
		assertPublicationSeriesCount(PublicationSeriesDaoIT.pubSeriesCnt);

		MpPublication mpPublication = sippProcess.processInformationProduct(ProcessType.DISSEMINATION, IP_NUMBER_ARTICLE);
		publicationsSeries = getSeriesByName(seriesTitle, null, 1);
		assertPublicationSeriesCount(PublicationSeriesDaoIT.pubSeriesCnt);
		assertPublication(mpPublication, publicationsSeries, 10, seriesTitle);
		assertEquals(5105, publicationsSeries.getId());
		assertEquals(mpPublication.getId().toString(), mpPublication.getIndexId());

		assertPublication(mpPublication, publicationsSeries, 10, seriesTitle);
		MpPublication pubFromDb = pubBusService.getByIndexId(mpPublication.getIndexId());
		assertPublication(pubFromDb, publicationsSeries, 10, seriesTitle);
		assertEquals(pubFromDb.getId(), mpPublication.getId());
		assertEquals(pubFromDb.getIndexId(), mpPublication.getIndexId());
		assertEquals(pubFromDb.getId().toString(), mpPublication.getIndexId());

	}

	@Test
	@DatabaseSetups({ @DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
			@DatabaseSetup("classpath:/testData/publicationType.xml"),
			@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
			@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
			@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml") })
	public void sippPublicationNewSeriesTest() throws Exception {
		when(informationProductDao.getInformationProduct(IP_NUMBER_ARTICLE))
				.thenReturn(getDisseminationFromXml("dissemination-article.xml"));
		when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(transactionStatus);
		when(transactionStatus.isRollbackOnly()).thenReturn(false);
		String seriesTitle = "Eos--Earth and Space Science News";
		PublicationSeries publicationsSeries = getSeriesByName(seriesTitle, null, 0);
		assertNull(publicationsSeries);
		assertPublicationSeriesCount(PublicationSeriesDaoIT.pubSeriesCnt);

		MpPublication mpPublication = sippProcess.processInformationProduct(ProcessType.DISSEMINATION, IP_NUMBER_ARTICLE);
		assertNotNull(mpPublication);

		publicationsSeries = getSeriesByName(seriesTitle, null, 1);
		assertPublicationSeriesCount(PublicationSeriesDaoIT.pubSeriesCnt + 1);

		assertPublication(mpPublication, publicationsSeries, 10, seriesTitle);
		assertEquals(mpPublication.getId().toString(), mpPublication.getIndexId());
		assertEquals(publicationsSeries.getPublicationSubtype().getId(), mpPublication.getPublicationSubtype().getId());

		MpPublication pubFromDb = pubBusService.getByIndexId(mpPublication.getIndexId());
		assertPublication(pubFromDb, publicationsSeries, 10, seriesTitle);
		assertEquals(pubFromDb.getId(), mpPublication.getId());
		assertEquals(pubFromDb.getIndexId(), mpPublication.getIndexId());
	}

	@Test
	@DatabaseSetups({ @DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
			@DatabaseSetup("classpath:/testData/publicationType.xml"),
			@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
			@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
			@DatabaseSetup("classpath:/testData/ipdsPubsTypeConv.xml") })
	public void sippPublicationUSGSSeriesTest() throws Exception {
		when(informationProductDao.getInformationProduct(IP_NUMBER_USGS_SERIES))
				.thenReturn(getDisseminationFromXml("dissemination-usgs-series.xml"));
		when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(transactionStatus);
		when(transactionStatus.isRollbackOnly()).thenReturn(false);
		String seriesTitle = "Open-File Report";
		PublicationSeries publicationsSeries = getSeriesByName(seriesTitle, null, 1);
		assertNotNull(publicationsSeries);
		assertEquals(seriesTitle, publicationsSeries.getText());
		assertPublicationSeriesCount(PublicationSeriesDaoIT.pubSeriesCnt);

		MpPublication mpPublication = sippProcess.processInformationProduct(ProcessType.DISSEMINATION, IP_NUMBER_USGS_SERIES);
		assertNotNull(mpPublication);

		publicationsSeries = getSeriesByName(seriesTitle, null, 1);
		assertPublicationSeriesCount(PublicationSeriesDaoIT.pubSeriesCnt);

		assertPublication(mpPublication, publicationsSeries, 5, seriesTitle);
		assertUsgsSeriesPublication(mpPublication);

		MpPublication pubFromDb = pubBusService.getByIndexId(mpPublication.getIndexId());
		assertPublication(pubFromDb, publicationsSeries, 5, seriesTitle);
		assertEquals(pubFromDb.getId(), mpPublication.getId());
		assertEquals(pubFromDb.getIndexId(), mpPublication.getIndexId());
		assertUsgsSeriesPublication(pubFromDb);
	}

	private void assertPublication(MpPublication mpPublication, PublicationSeries publicationsSeries, int pubSubtypeId, String seriesTitle) {
		assertNotNull(mpPublication);
		assertTrue(mpPublication.isValid(),
				"Validation errors in Sipp processed publication: " + mpPublication.getValidationErrors());
		assertNotNull(mpPublication.getId());
		assertTrue(mpPublication.getId() > 0);
		assertEquals("Disseminated", mpPublication.getIpdsReviewProcessState());

		assertNotNull(mpPublication.getSeriesTitle());
		assertNotNull(publicationsSeries);
		assertEquals(publicationsSeries.getId(), mpPublication.getSeriesTitle().getId());
		assertNotNull(publicationsSeries.getPublicationSubtype());
		assertNotNull(mpPublication.getPublicationSubtype());
		assertEquals(publicationsSeries.getPublicationSubtype().getId(), mpPublication.getPublicationSubtype().getId());
		assertEquals(seriesTitle, publicationsSeries.getText());
		assertEquals(seriesTitle, mpPublication.getSeriesTitle().getText());
		assertNull(mpPublication.getLargerWorkTitle());
		assertNotNull(mpPublication.getSeriesTitle().getPublicationSubtype());
		assertEquals(pubSubtypeId, mpPublication.getSeriesTitle().getPublicationSubtype().getId());
		assertNotNull(mpPublication.getSeriesTitle().isActive());
		assertTrue(mpPublication.getSeriesTitle().isActive());
	}
	
	private void assertUsgsSeriesPublication(MpPublication mpPublication) {
		assertFalse(mpPublication.isNoUsgsAuthors());
		assertEquals("ofr20201030", mpPublication.getIndexId());
		assertEquals("2020-1030", mpPublication.getSeriesNumber());
		assertEquals(IP_NUMBER_USGS_SERIES,mpPublication.getIpdsId());
		assertEquals(USGS_PRODUCT_DESCRIPTION, mpPublication.getProductDescription());
		assertEquals("U.S. Geological Survey", mpPublication.getPublisher());
		assertEquals("Reston VA", mpPublication.getPublisherLocation());
	}

	private void assertPublicationSeriesCount(int expectedCount) {
		int count = publicationSeriesDao.getObjectCount(Collections.emptyMap());
		assertEquals(expectedCount, count);
	}

	private InformationProduct getDisseminationFromXml(String xmlFile) throws Exception {
		XmlMapper xmlMapper = new XmlMapper();
		return xmlMapper.readValue(getFile("testData/sipp/" + xmlFile), InformationProduct.class);
	}

	private InformationProduct getDisseminationWithExistingPubSeries() throws Exception {
		InformationProduct informationProduct = getDisseminationFromXml("dissemination-article.xml");
		PublicationSeries publicationSeries = publicationSeriesDao.getById(5105);
		assertNotNull(publicationSeries);
		informationProduct.setJournalTitle(publicationSeries.getText());
		return informationProduct;
	}

	private PublicationSeries getSeriesByName(String name, Integer pubSubtypeId, int numExpected) {
		PublicationSeries ret = null;
		Map<String, Object> filters = new HashMap<>();
		if (pubSubtypeId != null) {
			filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, pubSubtypeId);
		}
		filters.put(PublicationSeriesDao.TEXT_SEARCH, name);
		List<PublicationSeries> pubSeries = PublicationSeries.getDao().getByMap(filters);
		assertEquals(numExpected, pubSeries.size());
		if (numExpected > 0) {
			ret = pubSeries.get(0);
		}
		return ret;
	}

}