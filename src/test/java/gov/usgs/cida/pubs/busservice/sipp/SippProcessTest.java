package gov.usgs.cida.pubs.busservice.sipp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.ipds.IpdsPubTypeConv;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.sipp.Author;
import gov.usgs.cida.pubs.domain.sipp.InformationProduct;
import gov.usgs.cida.pubs.domain.sipp.Reviewer;
import gov.usgs.cida.pubs.domain.sipp.SpecialProductAlert;
import gov.usgs.cida.pubs.domain.sipp.Task;
import gov.usgs.cida.pubs.domain.sipp.USGSProgram;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes= {PwPublication.class, SippProcess.class})
public class SippProcessTest extends BaseTest {

	public static final String MOCK_SIPP_URL = "https://localhost/mock?a=";

	@MockBean(name="restTemplate")
	protected RestTemplate restTemplate;
	@MockBean(name="configurationService")
	protected ConfigurationService configurationService;
	@MockBean
	protected IMpPublicationBusService pubBusService;
	@MockBean(name="pwPublicationDao")
	protected IPwPublicationDao pwPublicationDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;
	@MockBean
	protected PlatformTransactionManager transactionManager;

	protected SippProcess sippProcess;
	protected List<MpPublication> emptyList = new ArrayList<>();
	protected PublicationSeries publicationSeries;
	protected PublicationSubtype publicationSubtype;
	protected PublicationType publicationType;
	protected MpPublication mpPublication9;
	protected MpPublication mpPublication11;
	protected PwPublication pwPublication9;
	protected PwPublication pwPublication11;


	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		sippProcess = new SippProcess(configurationService, restTemplate, pubBusService, transactionManager);
		publicationType = buildPublicationType();
		publicationSubtype = buildPublicationSubtype();
		publicationSeries = buildPublicationSeries();
		mpPublication9 = buildMpPublication9();
		mpPublication11 = buildMpPublication(11);
		pwPublication9 = buildPwPublication(9);
		pwPublication11 = buildPwPublication(11);

		reset(pwPublicationDao, publicationDao, pubBusService);

		when(restTemplate.getForEntity(MOCK_SIPP_URL+"1", InformationProduct.class))
			.thenReturn(new ResponseEntity<InformationProduct>(getDisseminationFromXml(), HttpStatus.OK),
					new ResponseEntity<InformationProduct>(getDisseminationFromXml(), HttpStatus.BAD_REQUEST));
		when(configurationService.getInfoProductUrl()).thenReturn(MOCK_SIPP_URL);
	}

	@Test
	public void getIpdsProductTest() throws Exception {
		assertDaoTestResults(InformationProduct.class, getInformationProduct108541(), sippProcess.getIpdsProduct("1"), null, false, false, true);
		assertNull(sippProcess.getIpdsProduct("1"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void getFromMpTest() {
		when(pubBusService.getObjects(anyMap())).thenReturn(null, null, null, Arrays.asList(mpPublication9,mpPublication11), null, Arrays.asList(mpPublication11,mpPublication9), emptyList, emptyList);

		//This time we don't find it by IPDS ID and we do not attempt by index ID
		assertNull(sippProcess.getFromMp("IPDS_123", null));
		verify(pubBusService).getObjects(anyMap());

		//This time we don't find it by either ID
		assertNull(sippProcess.getFromMp("IPDS_123", "abc123"));
		verify(pubBusService, times(3)).getObjects(anyMap());

		//This time is by IPDS ID
		assertEquals(9, sippProcess.getFromMp("IPDS_123", "abc123").getId().intValue());
		verify(pubBusService, times(4)).getObjects(anyMap());

		//This time is by Index ID
		assertEquals(11, sippProcess.getFromMp("IPDS_123", "abc123").getId().intValue());
		verify(pubBusService, times(6)).getObjects(anyMap());

		//Again not found with either - empty lists
		assertNull(sippProcess.getFromMp("IPDS_123", "abc123"));
		verify(pubBusService, times(8)).getObjects(anyMap());
	}

	@Test
	public void getFromPwTest() {
		when(pwPublicationDao.getByIpdsId(anyString())).thenReturn(null, null, pwPublication9, null);
		when(pwPublicationDao.getByIndexId(anyString())).thenReturn(null, pwPublication11);

		//This time we don't find it by IPDS ID and we do not attempt by index ID
		assertNull(sippProcess.getFromPw("IPDS_123", null));
		verify(pwPublicationDao).getByIpdsId(anyString());
		verify(pwPublicationDao, never()).getByIndexId(anyString());

		//This time we don't find it by either ID
		assertNull(sippProcess.getFromPw("IPDS_123", "abc123"));
		verify(pwPublicationDao, times(2)).getByIpdsId(anyString());
		verify(pwPublicationDao).getByIndexId(anyString());

		//This time is by IPDS ID
		assertEquals(9, sippProcess.getFromPw("IPDS_123", "abc123").getId().intValue());
		verify(pwPublicationDao, times(3)).getByIpdsId(anyString());
		verify(pwPublicationDao).getByIndexId(anyString());

		//This time is by Index ID
		assertEquals(11, sippProcess.getFromPw("IPDS_123", "abc123").getId().intValue());
		verify(pwPublicationDao, times(4)).getByIpdsId(anyString());
		verify(pwPublicationDao, times(2)).getByIndexId(anyString());
	}

	@Test
	public void okToProcessTest() {
		when(pwPublicationDao.getByIpdsId(null)).thenReturn(null);
		when(pwPublicationDao.getByIndexId(null)).thenReturn(null);
		when(pwPublicationDao.getByIpdsId("IPDS-1")).thenReturn(new PwPublication());

		InformationProduct informationProduct = new InformationProduct();

		//NPE tests
		assertFalse(sippProcess.okToProcess(null, null, false, null, null));
		assertFalse(sippProcess.okToProcess(ProcessType.DISSEMINATION, null, false, null, null));
		assertFalse(sippProcess.okToProcess(null, informationProduct, false, null, null));
		assertFalse(sippProcess.okToProcess(null, null, false, new MpPublication(), null));
		assertFalse(sippProcess.okToProcess(null, informationProduct, false, new MpPublication(), null));
		assertFalse(sippProcess.okToProcess(ProcessType.DISSEMINATION, informationProduct, false, new MpPublication(), null));

		informationProduct.setProductType("abc");
		assertFalse(sippProcess.okToProcess(null, informationProduct, false, new MpPublication(), null));

		//Good Dissemination (brand new)
		assertTrue(sippProcess.okToProcess(ProcessType.DISSEMINATION, informationProduct, false, new MpPublication(), null));
		//Bad Dissemination (in warehouse)
		informationProduct.setIpNumber("IPDS-1");
		assertFalse(sippProcess.okToProcess(ProcessType.DISSEMINATION, informationProduct, false, new MpPublication(), null));

		//Good SPN Production
		informationProduct = new InformationProduct();
		informationProduct.setProductType("abc");
		informationProduct.setTask(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(sippProcess.okToProcess(ProcessType.SPN_PRODUCTION, informationProduct, true, new MpPublication(), null));
		//Bad SPN Production
		informationProduct.setTask("garbage");
		assertFalse(sippProcess.okToProcess(ProcessType.SPN_PRODUCTION, informationProduct, true, new MpPublication(), null));
	}

	@Test
	public void okToProcessDisseminationTest() {
		when(pwPublicationDao.getByIpdsId(null)).thenReturn(null);
		when(pwPublicationDao.getByIndexId(null)).thenReturn(null);
		when(pwPublicationDao.getByIpdsId("IPDS-1")).thenReturn(new PwPublication());

		InformationProduct informationProduct = new InformationProduct();
		MpPublication mpPublication = new MpPublication();

		//Do not process if new data is null
		assertFalse(sippProcess.okToProcessDissemination(null, false, null, null));
		assertFalse(sippProcess.okToProcessDissemination(null, false, mpPublication, null));

		//Do not process if already in Pubs Warehouse.
		informationProduct.setIpNumber("IPDS-1");
		assertFalse(sippProcess.okToProcessDissemination(informationProduct, false, mpPublication, null));

		//Do not process USGS numbered series without an actual series.
		informationProduct = new InformationProduct();
		assertFalse(sippProcess.okToProcessDissemination(informationProduct, true, null, null));

		//OK to process USGS Numbered Series when new
		informationProduct.setUsgsSeriesLetter("abc");
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, true, null, null));

		//OK to process USGS Numbered Series in MyPubs if has no review state
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, true, mpPublication, null));

		//OK to process USGS Numbered Series in MyPubs if in the SPN Production state
		mpPublication.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, true, mpPublication, null));

		//Do not process USGS Numbered Series if already in MyPubs (with a Dissemination state).
		mpPublication.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
		assertFalse(sippProcess.okToProcessDissemination(informationProduct, true, mpPublication, null));

		//OK to process other than USGS Numbered Series when new
		informationProduct = new InformationProduct();
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, false, null, null));

		//OK to process other than USGS Numbered Series in MyPubs if has no review state
		mpPublication = new MpPublication();
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, false, mpPublication, null));

		//OK to process other than USGS Numbered Series in MyPubs if in the SPN Production state
		mpPublication.setIpdsReviewProcessState(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertTrue(sippProcess.okToProcessDissemination(informationProduct, false, mpPublication, null));

		//Do not process other than USGS Numbered Series if already in MyPubs (with a Dissemination state).
		mpPublication.setIpdsReviewProcessState(ProcessType.DISSEMINATION.getIpdsValue());
		assertFalse(sippProcess.okToProcessDissemination(informationProduct, false, mpPublication, null));
	}

	@Test
	public void okToProcessSpnProductionTest() {
		//Do not process if new data is null
		assertFalse(sippProcess.okToProcessSpnProduction(null, false));

		//Skip if we have already assigned a DOI (shouldn't happen as we are querying for null DOI publications)
		InformationProduct informationProduct = new InformationProduct();
		informationProduct.setDigitalObjectIdentifier("something");
		assertFalse(sippProcess.okToProcessSpnProduction(informationProduct, false));

		//Skip if not in SPN Production
		informationProduct = new InformationProduct();
		assertFalse(sippProcess.okToProcessSpnProduction(informationProduct, false));
		informationProduct.setTask("garbage");
		assertFalse(sippProcess.okToProcessSpnProduction(informationProduct, false));

		//Skip if not USGS number series
		informationProduct.setTask(ProcessType.SPN_PRODUCTION.getIpdsValue());
		assertFalse(sippProcess.okToProcessSpnProduction(informationProduct, false));

		//Process USGS numbered series in SPN Production
		assertTrue(sippProcess.okToProcessSpnProduction(informationProduct, true));

		//Otherwise Skip
		informationProduct.setTask("garbage");
		assertFalse(sippProcess.okToProcessSpnProduction(informationProduct, true));
	}

	@Test
	public void processPublicationTest() {
		ProcessSummary processSummary = sippProcess.processPublication(ProcessType.DISSEMINATION, "IP-12344354");
		assertEquals(0, processSummary.getAdditions());
		assertEquals(1, processSummary.getErrors());
		assertEquals("IP-12344354:\n\tERROR: Trouble processing pub: IP-12344354 - null\n\n", processSummary.getProcessingDetails());
	}

	public InformationProduct getDisseminationFromXml() throws Exception {
		XmlMapper xmlMapper = new XmlMapper();
		return xmlMapper.readValue(getFile("testData/sipp/dissemination.xml"), InformationProduct.class);
	}

	public InformationProduct getInformationProduct108541() {
		InformationProduct informationProduct = new InformationProduct();
		informationProduct.setIpNumber("IP-108541");
		informationProduct.setAbstractText("There is an increasing need to provide actionable science for water management planning and ecological drought preparedness. However, questions remain regarding the most effective and efficient methods for extending scientific knowledge and products into management action and decision-making. This study analyzed two unique cases of water management in the context of ecologically available water to understand the translation of scientific knowledge into management. In particular, the research examined and compared (1) characteristics of the science being assessed and applied and (2) ideal types of scientific knowledge or products that facilitate the translation process towards action, management, and decision-making. The first case, beaver mimicry, is an emerging nature-based solution used to restore riparian areas, increase groundwater infiltration, and slow surface water flow that is rapidly being adopted by the natural resource management community. The second, Colorado Dust on Snow, is an established research program funded by several agencies and water conservation districts that provides water managers with scientific information regarding how movement of dust influences hydrology and timing of water runoff in critical water sources. For each case, ethnographic conversations with scientists and practitioners were used to understand how scientific knowledge translates into action and decision making. Conversations were transcribed and analyzed using qualitative methods. Results explore how the salience, credibility, and legitimacy of information is viewed differently by scientists and practitioners. A manuscript describing these results is in preparation for the journal Environmental Management.");
		informationProduct.setBasisNumber("RB00H5X.2");
		informationProduct.setCostCenter("Fort Collins Science Center");
		informationProduct.setIppaNumber("");
		informationProduct.setCooperators("North Central Climate Adaptation Science Center");
		informationProduct.setEditionNumber("");
		informationProduct.setFinalTitle("Identifying Characteristics of Actionable Science for Drought Planning and Adaptation: Final Report to the North Central Climate Adaptation Science Center");
		informationProduct.setJournalTitle("ScienceBase");
		informationProduct.setNumberOfMapsOrPlates("");
		informationProduct.setPageRange("");
		informationProduct.setPhysicalDescription("");
		informationProduct.setPlannedDisseminationDate("2019-07-01T05:00:00");
		informationProduct.setSupersedesIPNumber("");
		informationProduct.setTeamProjectName("");
		informationProduct.setViSpecialist("");
		informationProduct.setVolume("");
		informationProduct.setWorkingTitle("Identifying Characteristics of Actionable Science for Drought Planning and Adaptation: Final Report to the North Central Climate Adaptation Science Center");
		informationProduct.setUsgsSeriesNumber("");
		informationProduct.setUsgsRegion("Southwest Region");
		informationProduct.setUsgsProgram("NCCWSC/DOI Climate Science Centers; Status and Trends");
		informationProduct.setUsgsSeriesType("");
		informationProduct.setSeniorUSGSAuthor("Cravens, Amanda Emily");
		informationProduct.setLocationOfSupportingData("");
		informationProduct.setPscChief("Larson, Tania M.");
		informationProduct.setPublishedURL("https://www.sciencebase.gov/catalog/item/5d2cf2bbe4b038fabe22cff2");
		informationProduct.setCostCenterChief("Schuster, Rudy");
		informationProduct.setAuthorsSupervisor("Schuster, Rudy");
		informationProduct.setBureauApprovingOfficial("Carter, Janet M.");
		informationProduct.setCitation("Adam Wilke and Amanda Cravens, 2019, Identifying Characteristics of Actionable Science for Drought Planning and Adaptation. US Geological Survey:");
		informationProduct.setDigitalObjectIdentifier("");
		informationProduct.setInterpretivePublication("");
		informationProduct.setIssue("");
		informationProduct.setProductType("Cooperator publication");
		informationProduct.setDataManagementPlan("");
		informationProduct.setUsgsFunded("Yes");
		informationProduct.setUsgsMissionArea("Ecosystems");
		informationProduct.setProductSummary("There is an increasing need to provide actionable science for water management planning and ecological drought preparedness. However, questions remain regarding the most effective and efficient methods for extending scientific knowledge and products into management action and decision-making. This study analyzed two unique cases of water management in the context of ecologically available water to understand the translation of scientific knowledge into management. In particular, the research examined and compared (1) characteristics of the science being assessed and applied and (2) ideal types of scientific knowledge or products that facilitate the translation process towards action, management, and decision-making. The first case, beaver mimicry, is an emerging nature-based solution used to restore riparian areas, increase groundwater infiltration, and slow surface water flow that is rapidly being adopted by the natural resource management community. The second, Colorado Dust on Snow, is an established research program funded by several agencies and water conservation districts that provides water managers with scientific information regarding how movement of dust influences hydrology and timing of water runoff in critical water sources. For each case, ethnographic conversations with scientists and practitioners were used to understand how scientific knowledge translates into action and decision making. Conversations were transcribed and analyzed using qualitative methods. Results explore how the salience, credibility, and legitimacy of information is viewed differently by scientists and practitioners. A manuscript describing these results is in preparation for the journal Environmental Management.");
		informationProduct.setTaskAssignedTo("Cravens, Amanda Emily");
		informationProduct.setNonUSGSPublisher("North Central Climate Adaptation Science Center");
		informationProduct.setRelatedIPNumber("");
		informationProduct.setUsgsSeriesLetter("");
		informationProduct.setTask("Dissemination");
		informationProduct.setTaskStartDate("2019-06-25T22:44:26");
		informationProduct.setGeologicalNames("No");
		informationProduct.setEditor("");
		informationProduct.setPublishingServiceCenter("Denver PSC");
		informationProduct.setCreated("2019-05-22T17:04:04");
		informationProduct.setCreatedBy("Cravens, Amanda Emily");
		informationProduct.setModified("2019-08-05T21:41:54");
		informationProduct.setModifiedBy("Cravens, Amanda Emily");
		informationProduct.setAuthors(getAuthors108541());
		informationProduct.setUsgsPrograms(getUsgsPrograms108541());
		informationProduct.setSpecialProductAlerts(getSpecialProductAlerts108541());
		informationProduct.setReviewers(getReviewers108541());
		informationProduct.setTaskHistory(getTaskHistory108541());
		return informationProduct;
	}

	public List<Author> getAuthors108541() {
		List<Author> rtn = new ArrayList<>();
		Author one = new Author();
		one.setIpNumber("IP-108541");
		one.setAuthorName("");
		one.setAuthorNameText("Wilke, Adam");
		one.setOrcid("");
		one.setCostCenter("");
		one.setContributorRole("1");
		one.setNonUSGSAffiliation("former USGS employee");
		one.setNonUSGSContributor("Wilke, Adam");
		one.setRank("1");
		one.setCreated("2019-05-22T17:32:13");
		one.setCreatedBy("Cravens, Amanda Emily");
		one.setModified("2019-05-22T17:56:09");
		one.setModifiedBy("Cravens, Amanda Emily");
		rtn.add(one);

		Author two = new Author();
		two.setIpNumber("IP-108541");
		two.setAuthorName("Cravens, Amanda Emily");
		two.setAuthorNameText("Cravens, Amanda Emily");
		two.setOrcid("0000-0002-0271-7967");
		two.setCostCenter("Fort Collins Science Center");
		two.setContributorRole("1");
		two.setNonUSGSAffiliation("");
		two.setNonUSGSContributor("");
		two.setRank("2");
		two.setCreated("2019-05-22T17:04:05");
		two.setCreatedBy("Cravens, Amanda Emily");
		two.setModified("2019-05-22T17:56:01");
		two.setModifiedBy("Cravens, Amanda Emily");
		rtn.add(two);
		return rtn;
	}

	public List<USGSProgram> getUsgsPrograms108541() {
		List<USGSProgram> rtn = new ArrayList<>();
		USGSProgram one = new USGSProgram();
		one.setIpNumber("IP-108541");
		one.setUsgsProgram("NCCWSC/DOI Climate Science Centers");
		rtn.add(one);

		USGSProgram two = new USGSProgram();
		two.setIpNumber("IP-108541");
		two.setUsgsProgram("Status and Trends");
		rtn.add(two);
		return rtn;
	}

	public List<SpecialProductAlert> getSpecialProductAlerts108541() {
		List<SpecialProductAlert> rtn = new ArrayList<>();
		SpecialProductAlert one = new SpecialProductAlert();
		one.setIpNumber("IP-108541");
		one.setSpecialProductAlert("None");
		rtn.add(one);
		return rtn;
	}

	public List<Reviewer> getReviewers108541() {
		List<Reviewer> rtn = new ArrayList<>();
		Reviewer one = new Reviewer();
		one.setIpNumber("IP-108541");
		one.setReviewerType("USGS-Selected Peer");
		one.setReviewerName("Hailey Wilmer");
		one.setReviewerAffiliation("Agricultural Research Service");
		one.setCreated("2019-05-22T22:57:41");
		one.setCreatedBy("Cravens, Amanda Emily");
		one.setModified("2019-05-22T22:57:41");
		one.setModifiedBy("Cravens, Amanda Emily");
		rtn.add(one);

		Reviewer two = new Reviewer();
		two.setIpNumber("IP-108541");
		two.setReviewerType("USGS-Selected Peer");
		two.setReviewerName("Jamie McEvoy");
		two.setReviewerAffiliation("Montana State University");
		two.setCreated("2019-05-22T22:57:22");
		two.setCreatedBy("Cravens, Amanda Emily");
		two.setModified("2019-05-22T22:57:22");
		two.setModifiedBy("Cravens, Amanda Emily");
		rtn.add(two);
		return rtn;
	}

	public List<Task> getTaskHistory108541() {
		List<Task> rtn = new ArrayList<>();
		Task one = new Task();
		one.setIpNumber("IP-108541");
		one.setComments("The subject manuscript is approved as a cooperator report on 06/25/2019 by Janet Carter. The tradename disclaimer must be added because the report contains tradenames. A few clarifications/corrections are needed in manuscript, which are summarized in first comment bubble in the 'Final BAO approved manuscript' file. Additional housekeeping items are included in comments in the 'Final BAO approved manuscript' file. The comments must be addressed before the appropriately revised report is submitted to the cooperators for publishing. Please contact me if you have any questions.");
		one.setTaskStartDate("2019-06-25T15:12:31");
		one.setTaskCompletionDate("2019-06-25T22:44:26");
		one.setStatus("Approve");
		one.setTaskName("Bureau Approval");
		one.setTaskAssignedTo("Cravens, Amanda Emily");
		one.setNextTask("Dissemination");
		one.setTaskApprover("Carter, Janet M.");
		one.setCreated("2019-06-25T22:44:27");
		one.setCreatedBy("Carter, Janet M.");
		one.setModified("2019-06-25T22:44:27");
		one.setModifiedBy("Carter, Janet M.");
		rtn.add(one);

		Task two = new Task();
		two.setIpNumber("IP-108541");
		two.setComments("Approver changed");
		two.setTaskStartDate("2019-06-24T18:06:03");
		two.setTaskCompletionDate("2019-06-25T15:12:31");
		two.setStatus("Reassign");
		two.setTaskName("Bureau Approval");
		two.setTaskAssignedTo("Carter, Janet M.");
		two.setNextTask("Bureau Approval");
		two.setTaskApprover("Carter, Janet M.");
		two.setCreated("2019-06-25T15:12:32");
		two.setCreatedBy("Carter, Janet M.");
		two.setModified("2019-06-25T15:12:32");
		two.setModifiedBy("Carter, Janet M.");
		rtn.add(two);

		Task three = new Task();
		three.setIpNumber("IP-108541");
		three.setComments("");
		three.setTaskStartDate("2019-06-24T18:05:58");
		three.setTaskCompletionDate("2019-06-24T18:06:03");
		three.setStatus("Approve");
		three.setTaskName("Center Approval");
		three.setTaskAssignedTo("Powell, Janine");
		three.setNextTask("Bureau Approval");
		three.setTaskApprover("Schuster, Rudy");
		three.setCreated("2019-06-24T18:06:03");
		three.setCreatedBy("Schuster, Rudy");
		three.setModified("2019-06-24T18:06:03");
		three.setModifiedBy("Schuster, Rudy");
		rtn.add(three);

		Task four = new Task();
		four.setIpNumber("IP-108541");
		four.setComments("");
		four.setTaskStartDate("2019-06-22T21:36:14");
		four.setTaskCompletionDate("2019-06-24T18:05:58");
		four.setStatus("Approve");
		four.setTaskName("Supervisory Approval");
		four.setTaskAssignedTo("Schuster, Rudy");
		four.setNextTask("Center Approval");
		four.setTaskApprover("Schuster, Rudy");
		four.setCreated("2019-06-24T18:06:00");
		four.setCreatedBy("Schuster, Rudy");
		four.setModified("2019-06-24T18:06:00");
		four.setModifiedBy("Schuster, Rudy");
		rtn.add(four);

		Task five = new Task();
		five.setIpNumber("IP-108541");
		five.setComments("");
		five.setTaskStartDate("2019-05-24T17:09:37");
		five.setTaskCompletionDate("2019-06-22T21:36:14");
		five.setStatus("Approve");
		five.setTaskName("Reconcile Peer Review");
		five.setTaskAssignedTo("Schuster, Rudy");
		five.setNextTask("Supervisory Approval");
		five.setTaskApprover("Cravens, Amanda Emily");
		five.setCreated("2019-06-22T21:36:15");
		five.setCreatedBy("Cravens, Amanda Emily");
		five.setModified("2019-06-22T21:36:15");
		five.setModifiedBy("Cravens, Amanda Emily");
		rtn.add(five);

		Task six = new Task();
		six.setIpNumber("IP-108541");
		six.setComments("");
		six.setTaskStartDate("2019-05-22T19:14:09");
		six.setTaskCompletionDate("2019-05-24T17:09:37");
		six.setStatus("Approve");
		six.setTaskName("Accept for Peer Review");
		six.setTaskAssignedTo("Cravens, Amanda Emily");
		six.setNextTask("Reconcile Peer Review");
		six.setTaskApprover("Schuster, Rudy");
		six.setCreated("2019-05-24T17:09:37");
		six.setCreatedBy("Schuster, Rudy");
		six.setModified("2019-05-24T17:09:37");
		six.setModifiedBy("Schuster, Rudy");
		rtn.add(six);

		Task seven = new Task();
		seven.setIpNumber("IP-108541");
		seven.setComments("");
		seven.setTaskStartDate("2019-05-22T17:04:05");
		seven.setTaskCompletionDate("2019-05-22T17:04:05");
		seven.setStatus("Create");
		seven.setTaskName("");
		seven.setTaskAssignedTo("Cravens, Amanda Emily");
		seven.setNextTask("Request Peer Review");
		seven.setTaskApprover("");
		seven.setCreated("2019-05-22T17:04:05");
		seven.setCreatedBy("Cravens, Amanda Emily");
		seven.setModified("2019-05-22T17:04:05");
		seven.setModifiedBy("Cravens, Amanda Emily");
		rtn.add(seven);

		Task eight = new Task();
		eight.setIpNumber("IP-108541");
		eight.setComments("");
		eight.setTaskStartDate("2019-05-22T17:04:04");
		eight.setTaskCompletionDate("2019-05-22T19:14:09");
		eight.setStatus("Approve");
		eight.setTaskName("Request Peer Review");
		eight.setTaskAssignedTo("Schuster, Rudy");
		eight.setNextTask("Accept for Peer Review");
		eight.setTaskApprover("Cravens, Amanda Emily");
		eight.setCreated("2019-05-22T19:14:09");
		eight.setCreatedBy("Cravens, Amanda Emily");
		eight.setModified("2019-05-22T19:14:09");
		eight.setModifiedBy("Cravens, Amanda Emily");
		rtn.add(eight);

		return rtn;
	}

	protected PublicationType buildPublicationType() {
		PublicationType pubType = new PublicationType();
		pubType.setId(1);
		pubType.setText("Test Type");
		return pubType;
	}

	protected PublicationSubtype buildPublicationSubtype() {
		PublicationSubtype pubSubtype = new PublicationSubtype();
		pubSubtype.setId(2);
		pubSubtype.setText("Test Subtype");
		return pubSubtype;
	}

	protected PublicationSeries buildPublicationSeries() {
		PublicationSeries pubSeries = new PublicationSeries();
		pubSeries.setText("Test Series");
		pubSeries.setCode("tst");
		return pubSeries;
	}

	protected MpPublication buildMpPublication9() {
		MpPublication mpPub = buildMpPublication(9);
		mpPub.setPublicationType(publicationType);
		mpPub.setPublicationSubtype(publicationSubtype);
		mpPub.setSeriesTitle(publicationSeries);
		mpPub.setIpdsReviewProcessState("Test State");
		mpPub.setDoi("Test Doi");
		return mpPub;
	}

	protected MpPublication buildMpPublication(Integer id) {
		MpPublication mpPub = new MpPublication();
		mpPub.setId(id);
		return mpPub;
	}

	protected PwPublication buildPwPublication(Integer id) {
		PwPublication pwPub = new PwPublication();
		pwPub.setId(id);
		return pwPub;
	}
}
