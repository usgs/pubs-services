package gov.usgs.cida.pubs.busservice.sipp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.busservice.ext.ExtPublicationService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.dao.intfc.IIpdsPubTypeConvDao;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPublishingServiceCenterDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.dao.sipp.InformationProductDao;
import gov.usgs.cida.pubs.domain.InformationProductHelper;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationIT;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSeriesHelper;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.sipp.InformationProduct;
import gov.usgs.cida.pubs.domain.sipp.IpdsPubTypeConv;
import gov.usgs.cida.pubs.domain.sipp.ProcessSummary;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes= {PwPublication.class, SippProcess.class, PublicationSeries.class,
			IpdsPubTypeConv.class, InformationProduct.class, PublishingServiceCenter.class,
			LocalValidatorFactoryBean.class})
public class SippProcessTest extends BaseTest {

	@Autowired
	protected Validator validator;

	@MockBean
	protected ExtPublicationService extPublicationBusService;
	@MockBean
	protected IMpPublicationBusService pubBusService;
	@MockBean(name="pwPublicationDao")
	protected IPwPublicationDao pwPublicationDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;
	@MockBean(name="publicationSeriesDao")
	protected IDao<PublicationSeries> publicationSeriesDao;
	@MockBean(name="ipdsPubTypeConvDao")
	protected IIpdsPubTypeConvDao ipdsPubTypeConvDao;
	@MockBean(name="informationProductDao")
	protected InformationProductDao informationProductDao;
	@MockBean(name="publishingServiceCenterDao")
	protected IPublishingServiceCenterDao publishingServiceCenterDao;
	@MockBean
	protected SippConversionService sippConversionService;

	protected SippProcess sippProcess;
	protected List<MpPublication> emptyList = new ArrayList<>();
	protected MpPublication mpPublication9;
	protected MpPublication mpPublication11;
	protected PwPublication pwPublication9;
	protected PwPublication pwPublication11;

	@Before
	public void setUp() throws Exception {
		sippProcess = new SippProcess(extPublicationBusService, pubBusService, sippConversionService);
		mpPublication9 = (MpPublication) PublicationIT.buildAPub(new MpPublication(), 9);
		mpPublication11 = (MpPublication) PublicationIT.buildAPub(new MpPublication(), 11);
		mpPublication11.setId(11);
		pwPublication9 = (PwPublication) PublicationIT.buildAPub(new PwPublication(), 9);
		pwPublication11 = (PwPublication) PublicationIT.buildAPub(new PwPublication(), 11);

		reset(pwPublicationDao, publicationDao, pubBusService, publicationSeriesDao, ipdsPubTypeConvDao);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void processInformationProductTest() {
		MpPublication mpPublicationx = new MpPublication();
		mpPublicationx.setId(123);
		when(informationProductDao.getInformationProduct("IP-12344354"))
			.thenReturn(
				InformationProductHelper.getExtendedInformationProduct108541(null),
				InformationProductHelper.getExtendedInformationProduct108541(null),
				InformationProductHelper.getExtendedInformationProduct108541(PublicationSeriesHelper.THREE_THIRTY_FOUR)
				);
		when(pubBusService.getObjects(anyMap())).thenReturn(emptyList, List.of(mpPublicationx), List.of(mpPublication9));
		when(sippConversionService.buildMpPublication(any(InformationProduct.class), isNull())).thenReturn(new MpPublication());
		when(sippConversionService.buildMpPublication(any(InformationProduct.class), anyInt())).thenReturn(new MpPublication());
		when(extPublicationBusService.create(any(MpPublication.class))).thenReturn(mpPublicationx);

		ProcessSummary processSummary = sippProcess.processInformationProduct(ProcessType.DISSEMINATION, "IP-12344354");
		assertEquals(1, processSummary.getAdditions());
		assertEquals(0, processSummary.getErrors());
		assertEquals("IP-12344354:\n\tAdded to MyPubs as ProdId: 123\n\n", processSummary.getProcessingDetails());

		processSummary = sippProcess.processInformationProduct(ProcessType.DISSEMINATION, "IP-12344354");
		assertEquals(1, processSummary.getAdditions());
		assertEquals(0, processSummary.getErrors());
		assertEquals("IP-12344354:\n\tAdded to MyPubs as ProdId: 123\n\n", processSummary.getProcessingDetails());

		processSummary = sippProcess.processInformationProduct(ProcessType.DISSEMINATION, "IP-12344354");
		assertEquals(0, processSummary.getAdditions());
		assertEquals(0, processSummary.getErrors());
		assertEquals("IP-12344354:\n\tIPDS record not processed (\"DISSEMINATION\") - ProductType: Cooperator publication Publication Type: Report PublicationSubtype: USGS Numbered Series Series: Scientific Investigations Report Process State: Dissemination DOI: \n\n", processSummary.getProcessingDetails());
	}

	@Test
	public void getInformationProductTest() {
		when(informationProductDao.getInformationProduct("IPDS_123"))
			.thenReturn(
				(InformationProduct) null,
				InformationProductHelper.getInformationProduct108541(),
				InformationProductHelper.getExtendedInformationProduct108541(null),
				InformationProductHelper.getExtendedInformationProduct108541(PublicationSeriesHelper.THREE_THIRTY_FOUR)
				);
		when(ipdsPubTypeConvDao.getByIpdsValue("Cooperator publication")).thenReturn(null, getIpdsPubTypeConv6(), getIpdsPubTypeConv13());
		when(publicationSeriesDao.getByMap(anyMap())).thenReturn(List.of(PublicationSeriesHelper.THREE_THIRTY_FOUR));
		when(pubBusService.getUsgsNumberedSeriesIndexId(any(PublicationSeries.class), anyString(), anyString(), isNull())).thenReturn("sir1bc123");

		//Didn't find InformationProduct
		assertNull(sippProcess.getInformationProduct("IPDS_123"));
		verify(informationProductDao).getInformationProduct("IPDS_123");
		verify(ipdsPubTypeConvDao, never()).getByIpdsValue(anyString());
		verify(publicationSeriesDao, never()).getByMap(anyMap());
		verify(pubBusService, never()).getUsgsNumberedSeriesIndexId(any(PublicationSeries.class), anyString(), anyString(), isNull());

		//Didn't find IpdsPubTypeConv
		assertDaoTestResults(InformationProduct.class, InformationProductHelper.getInformationProduct108541(), sippProcess.getInformationProduct("IPDS_123"), null, false, false, true);
		verify(informationProductDao, times(2)).getInformationProduct("IPDS_123");
		verify(ipdsPubTypeConvDao).getByIpdsValue("Cooperator publication");
		verify(publicationSeriesDao, never()).getByMap(anyMap());
		verify(pubBusService, never()).getUsgsNumberedSeriesIndexId(any(PublicationSeries.class), anyString(), anyString(), isNull());

		//Found both, not USGS Series
		assertDaoTestResults(InformationProduct.class, InformationProductHelper.getExtendedInformationProduct108541(null), sippProcess.getInformationProduct("IPDS_123"), null, false, false, true);
		verify(informationProductDao, times(3)).getInformationProduct("IPDS_123");
		verify(ipdsPubTypeConvDao, times(2)).getByIpdsValue("Cooperator publication");
		verify(publicationSeriesDao, never()).getByMap(anyMap());
		verify(pubBusService, never()).getUsgsNumberedSeriesIndexId(any(PublicationSeries.class), anyString(), anyString(), isNull());

		//Found both, is USGS Number Series
		assertDaoTestResults(InformationProduct.class, InformationProductHelper.getExtendedInformationProduct108541(PublicationSeriesHelper.THREE_THIRTY_FOUR), sippProcess.getInformationProduct("IPDS_123"), null, false, false, true);
		verify(informationProductDao, times(4)).getInformationProduct("IPDS_123");
		verify(ipdsPubTypeConvDao, times(3)).getByIpdsValue("Cooperator publication");
		verify(publicationSeriesDao).getByMap(anyMap());
		verify(pubBusService).getUsgsNumberedSeriesIndexId(any(PublicationSeries.class), anyString(), anyString(), isNull());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getSeriesTitleTest() {
		when(publicationSeriesDao.getByMap(anyMap())).thenReturn(List.of(), List.of(PublicationSeriesHelper.THREE_ZERO_NINE, PublicationSeriesHelper.THREE_THIRTY_FOUR));

		//this batch should not call database
		String text = null;
		PublicationSubtype subtype = new PublicationSubtype();
		assertNull(sippProcess.getSeriesTitle(null, text));
		verify(publicationSeriesDao, never()).getByMap(anyMap());

		assertNull(sippProcess.getSeriesTitle(subtype, text));
		verify(publicationSeriesDao, never()).getByMap(anyMap());

		text = "";
		assertNull(sippProcess.getSeriesTitle(subtype, text));
		verify(publicationSeriesDao, never()).getByMap(anyMap());

		assertNull(sippProcess.getSeriesTitle(null, text));
		verify(publicationSeriesDao, never()).getByMap(anyMap());

		subtype.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		assertNull(sippProcess.getSeriesTitle(subtype, text));
		verify(publicationSeriesDao, never()).getByMap(anyMap());

		//Not found
		text = "abc";
		assertNull(sippProcess.getSeriesTitle(subtype, text));
		verify(publicationSeriesDao).getByMap(anyMap());

		//success
		text = "Coal Map";
		assertEquals(309, sippProcess.getSeriesTitle(subtype, text).getId().intValue());
		verify(publicationSeriesDao, times(2)).getByMap(anyMap());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getMpPublicationTest() {
		when(pubBusService.getObjects(anyMap())).thenReturn(null, null, null, List.of(mpPublication9,mpPublication11), null, List.of(mpPublication11,mpPublication9), emptyList, emptyList);
		InformationProduct informationProduct = new InformationProduct();
		informationProduct.setIpNumber("IPDS_123");

		//This time we don't find it by IPDS ID and we do not attempt by index ID
		assertNull(sippProcess.getMpPublication(informationProduct));
		verify(pubBusService).getObjects(anyMap());

		informationProduct.setIndexId("abc123");
		//This time we don't find it by either ID
		assertNull(sippProcess.getMpPublication(informationProduct));
		verify(pubBusService, times(3)).getObjects(anyMap());

		//This time is by IPDS ID
		assertEquals(9, sippProcess.getMpPublication(informationProduct).getId().intValue());
		verify(pubBusService, times(4)).getObjects(anyMap());

		//This time is by Index ID
		assertEquals(11, sippProcess.getMpPublication(informationProduct).getId().intValue());
		verify(pubBusService, times(6)).getObjects(anyMap());

		//Again not found with either - empty lists
		assertNull(sippProcess.getMpPublication(informationProduct));
		verify(pubBusService, times(8)).getObjects(anyMap());
	}

	@Test
	public void getPwPublicationTest() {
		when(pwPublicationDao.getByIpdsId(anyString())).thenReturn(null, null, pwPublication9, null);
		when(pwPublicationDao.getByIndexId(anyString())).thenReturn(null, pwPublication11);
		InformationProduct informationProduct = new InformationProduct();
		informationProduct.setIpNumber("IPDS_123");

		//This time we don't find it by IPDS ID and we do not attempt by index ID
		assertNull(sippProcess.getPwPublication(informationProduct));
		verify(pwPublicationDao).getByIpdsId(anyString());
		verify(pwPublicationDao, never()).getByIndexId(anyString());

		informationProduct.setIndexId("abc123");
		//This time we don't find it by either ID
		assertNull(sippProcess.getPwPublication(informationProduct));
		verify(pwPublicationDao, times(2)).getByIpdsId(anyString());
		verify(pwPublicationDao).getByIndexId(anyString());

		//This time is by IPDS ID
		assertEquals(9, sippProcess.getPwPublication(informationProduct).getId().intValue());
		verify(pwPublicationDao, times(3)).getByIpdsId(anyString());
		verify(pwPublicationDao).getByIndexId(anyString());

		//This time is by Index ID
		assertEquals(11, sippProcess.getPwPublication(informationProduct).getId().intValue());
		verify(pwPublicationDao, times(4)).getByIpdsId(anyString());
		verify(pwPublicationDao, times(2)).getByIndexId(anyString());
	}

	@Test
	public void okToProcessTest() {
		when(pwPublicationDao.getByIpdsId(null)).thenReturn(null);
		when(pwPublicationDao.getByIndexId(null)).thenReturn(null);
		when(pwPublicationDao.getByIpdsId("IPDS-1")).thenReturn(new PwPublication());

		InformationProduct informationProduct = new InformationProduct();
		MpPublication mpPublication = new MpPublication();
		PublicationType pubType = new PublicationType();

		//NPE tests
		assertFalse(sippProcess.okToProcess(null, null, null));
		assertFalse(sippProcess.okToProcess(ProcessType.DISSEMINATION, null, null));
		assertFalse(sippProcess.okToProcess(null, informationProduct, null));
		assertFalse(sippProcess.okToProcess(null, null, mpPublication));
		assertFalse(sippProcess.okToProcess(null, informationProduct, mpPublication));
		assertFalse(sippProcess.okToProcess(ProcessType.DISSEMINATION, informationProduct, mpPublication));

		informationProduct.setPublicationType(pubType);
		assertFalse(sippProcess.okToProcess(null, informationProduct, mpPublication));

		//Good Dissemination (brand new)
		assertTrue(sippProcess.okToProcess(ProcessType.DISSEMINATION, informationProduct, mpPublication));
		//Bad Dissemination (in warehouse)
		informationProduct.setIpNumber("IPDS-1");
		assertFalse(sippProcess.okToProcess(ProcessType.DISSEMINATION, informationProduct, mpPublication));

		//Good SPN Production
		informationProduct = new InformationProduct();
		informationProduct.setPublicationType(pubType);
		informationProduct.setTask(ProcessType.SPN_PRODUCTION.getIpdsValue());
		informationProduct.setUsgsNumberedSeries(true);
		assertTrue(sippProcess.okToProcess(ProcessType.SPN_PRODUCTION, informationProduct, mpPublication));
		//Bad SPN Production
		informationProduct.setTask("garbage");
		assertFalse(sippProcess.okToProcess(ProcessType.SPN_PRODUCTION, informationProduct, mpPublication));
	}

	@Test
	public void okToProcessDisseminationTest() {
		when(pwPublicationDao.getByIpdsId(null)).thenReturn(null);
		when(pwPublicationDao.getByIndexId(null)).thenReturn(null);
		when(pwPublicationDao.getByIpdsId("IPDS-1")).thenReturn(new PwPublication());

		InformationProduct informationProduct = new InformationProduct();
		MpPublication mpPublication = new MpPublication();

		//Do not process if new data is null
		assertFalse(sippProcess.okToProcessDissemination(null, null));
		assertFalse(sippProcess.okToProcessDissemination(null, mpPublication));

		//Do not process if already in Pubs Warehouse.
		informationProduct.setIpNumber("IPDS-1");
		assertFalse(sippProcess.okToProcessDissemination(informationProduct, mpPublication));

		//Do not process USGS numbered series without an actual series.
		informationProduct = new InformationProduct();
		informationProduct.setUsgsNumberedSeries(true);
		assertFalse(sippProcess.okToProcessDissemination(informationProduct, null));

		//OK to process USGS Numbered Series when new
		informationProduct.setUsgsSeriesTitle(new PublicationSeries());
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, null));

		//OK to process USGS Numbered Series in MyPubs if has no review state
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, mpPublication));

		//OK to process USGS Numbered Series in MyPubs if in the SPN Production state
		mpPublication.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, mpPublication));

		//Do not process USGS Numbered Series if already in MyPubs (with a Dissemination state).
		mpPublication.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
		assertFalse(sippProcess.okToProcessDissemination(informationProduct,  mpPublication));

		//OK to process other than USGS Numbered Series when new
		informationProduct = new InformationProduct();
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, null));

		//OK to process other than USGS Numbered Series in MyPubs if has no review state
		mpPublication = new MpPublication();
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, mpPublication));

		//OK to process other than USGS Numbered Series in MyPubs if in the SPN Production state
		mpPublication.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, mpPublication));

		//Do not process other than USGS Numbered Series if already in MyPubs (with a Dissemination state).
		mpPublication.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
		assertFalse(sippProcess.okToProcessDissemination(informationProduct, mpPublication));
	}

	@Test
	public void okToProcessSpnProductionTest() {
		//Do not process if new data is null
		assertFalse(sippProcess.okToProcessSpnProduction(null));

		//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
		InformationProduct informationProduct = new InformationProduct();
		informationProduct.setDigitalObjectIdentifier("something");
		assertFalse(sippProcess.okToProcessSpnProduction(informationProduct));

		//Skip if not in SPN Production
		informationProduct = new InformationProduct();
		assertFalse(sippProcess.okToProcessSpnProduction(informationProduct));
		informationProduct.setTask("garbage");
		assertFalse(sippProcess.okToProcessSpnProduction(informationProduct));

		//Skip if not USGS number series
		informationProduct.setTask(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertFalse(sippProcess.okToProcessSpnProduction(informationProduct));

		informationProduct.setUsgsNumberedSeries(true);
		//Process USGS numbered series in SPN Production
		assertTrue(sippProcess.okToProcessSpnProduction(informationProduct));

		//Otherwise Skip
		informationProduct.setTask("garbage");
		assertFalse(sippProcess.okToProcessSpnProduction(informationProduct));
	}

	@Test
	public void processPublicationTest() {
		when(sippConversionService.buildMpPublication(any(InformationProduct.class), isNull())).thenReturn(new MpPublication());
		when(sippConversionService.buildMpPublication(any(InformationProduct.class), anyInt())).thenReturn(new MpPublication());
		when(extPublicationBusService.create(any(MpPublication.class))).thenThrow(new RuntimeException("testing error")).thenReturn(new MpPublication());

		ProcessSummary processSummary = sippProcess.processPublication(null, new InformationProduct(), null);

		assertEquals(1, processSummary.getErrors());
		assertEquals(0, processSummary.getAdditions());
		assertEquals("\n" + 
				"ERROR: Failed validation.\n" + 
				"\tField:MpPublication - Message:testing error - Level:FATAL - Value:null\n" + 
				"\tValidator Results: 1 result(s)\n" + 
				"\t", processSummary.getProcessingDetails());
		verify(pubBusService, never()).deleteObject(anyInt());
		verify(sippConversionService).buildMpPublication(any(InformationProduct.class), isNull());
		verify(extPublicationBusService).create(any(MpPublication.class));

		processSummary = sippProcess.processPublication(null, new InformationProduct(), null);

		assertEquals(0, processSummary.getErrors());
		assertEquals(1, processSummary.getAdditions());
		assertEquals("\n\tAdded to MyPubs as ProdId: null", processSummary.getProcessingDetails());
		verify(pubBusService, never()).deleteObject(anyInt());
		verify(sippConversionService, times(2)).buildMpPublication(any(InformationProduct.class), isNull());
		verify(extPublicationBusService, times(2)).create(any(MpPublication.class));

		processSummary = sippProcess.processPublication(null, new InformationProduct(), 1234);

		assertEquals(0, processSummary.getErrors());
		assertEquals(1, processSummary.getAdditions());
		assertEquals("\n\tAdded to MyPubs as ProdId: null", processSummary.getProcessingDetails());
		verify(pubBusService).deleteObject(anyInt());
		verify(sippConversionService).buildMpPublication(any(InformationProduct.class), anyInt());
		verify(extPublicationBusService, times(3)).create(any(MpPublication.class));
	}

	@Test
	public void buildPublicationProcessSummaryTest() {
		MpPublication mpPublication = new MpPublication();
		mpPublication.setId(1234);
		mpPublication.setTitle("test");
		mpPublication.setIndexId("ds1");

		ProcessSummary processSummary = sippProcess.buildPublicationProcessSummary(mpPublication);

		assertEquals(0, processSummary.getErrors());
		assertEquals(1, processSummary.getAdditions());
		assertEquals("\n\tAdded to MyPubs as ProdId: 1234", processSummary.getProcessingDetails());

		mpPublication.setValidationErrors(validator.validate(mpPublication));
		processSummary = sippProcess.buildPublicationProcessSummary(mpPublication);

		assertEquals(1, processSummary.getErrors());
		assertEquals(0, processSummary.getAdditions());
		assertEquals("\nERROR: Failed validation.\n" +
				"\tField:publicationType - Message:must not be null - Level:FATAL - Value:null\n" + 
				"\tValidator Results: 1 result(s)\n" + 
				"\t", processSummary.getProcessingDetails());
	}

	@Test
	public void buildNotOkDetailsTest() {
		assertEquals("\n\tIPDS record not processed (\"DISSEMINATION\") - ProductType: null Process State: null DOI: null", sippProcess.buildNotOkDetails(ProcessType.DISSEMINATION, new InformationProduct()));

		InformationProduct informationProduct = InformationProductHelper.getExtendedInformationProduct108541(PublicationSeriesHelper.THREE_ZERO_NINE);
		informationProduct.setDigitalObjectIdentifier("http://dx.doi.org/10.1016/j.gca.2003.11.028");
		assertEquals("\n\tIPDS record not processed (\"DISSEMINATION\") - ProductType: Cooperator publication Publication Type: Report PublicationSubtype: USGS Numbered Series Series: Coal Map Process State: Dissemination DOI: http://dx.doi.org/10.1016/j.gca.2003.11.028", sippProcess.buildNotOkDetails(ProcessType.DISSEMINATION, informationProduct));
	}

	private IpdsPubTypeConv getIpdsPubTypeConv6() {
		IpdsPubTypeConv ipdsPubTypeConv = new IpdsPubTypeConv();
		ipdsPubTypeConv.setId(6);
		ipdsPubTypeConv.setIpdsValue("Cooperator publication");
		PublicationType publicationType = new PublicationType();
		publicationType.setId(18);
		ipdsPubTypeConv.setPublicationType(publicationType);
		PublicationSubtype publicationSubtype = new PublicationSubtype();
		publicationSubtype.setId(4);
		ipdsPubTypeConv.setPublicationSubtype(publicationSubtype);
		return ipdsPubTypeConv;
	}

	private IpdsPubTypeConv getIpdsPubTypeConv13() {
		IpdsPubTypeConv ipdsPubTypeConv = new IpdsPubTypeConv();
		ipdsPubTypeConv.setId(13);
		ipdsPubTypeConv.setIpdsValue("Cooperator publication");
		PublicationType publicationType = new PublicationType();
		publicationType.setId(18);
		ipdsPubTypeConv.setPublicationType(publicationType);
		PublicationSubtype publicationSubtype = new PublicationSubtype();
		publicationSubtype.setId(5);
		ipdsPubTypeConv.setPublicationSubtype(publicationSubtype);
		return ipdsPubTypeConv;
	}}
