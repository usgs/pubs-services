package gov.usgs.cida.pubs.busservice.sipp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.busservice.intfc.ISippProcess;
import gov.usgs.cida.pubs.dao.sipp.IpdsBureauApprovalDao;
import gov.usgs.cida.pubs.dao.sipp.SippProcessLogDao;
import gov.usgs.cida.pubs.domain.IpdsBureauApprovalHelper;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.sipp.IpdsBureauApproval;
import gov.usgs.cida.pubs.domain.sipp.SippProcessLog;
import gov.usgs.cida.pubs.validation.ValidatorResult;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={IpdsBureauApproval.class, SippProcessLog.class})
public class DisseminationListServiceTest extends BaseTest {

	@MockBean(name="ipdsBureauApprovalDao")
	protected IpdsBureauApprovalDao ipdsBureauApprovalDao;
	@MockBean(name="sippProcessLogDao")
	protected SippProcessLogDao sippProcessLogDao;
	@MockBean
	protected ISippProcess sippProcess;

	protected DisseminationListService service;

	@Before
	public void setUp() throws Exception {
		service = new DisseminationListService(sippProcess);
		reset(ipdsBureauApprovalDao, sippProcessLogDao);
	}

	@Test
	public void processDisseminationListTest() {
		when(ipdsBureauApprovalDao.getIpdsBureauApprovals(1))
			.thenReturn(List.of(IpdsBureauApprovalHelper.getIpdsBureauApproval("IP-1234"), IpdsBureauApprovalHelper.getIpdsBureauApproval("IP-5678")));
		when(sippProcess.processInformationProduct(ProcessType.DISSEMINATION, "IP-1234"))
			.thenReturn(getPub1234());
		when(sippProcess.processInformationProduct(ProcessType.DISSEMINATION, "IP-5678"))
			.thenThrow(new RuntimeException("oops"));
		when(sippProcessLogDao.add(any(SippProcessLog.class)))
			.thenReturn(6);
		ArgumentCaptor<SippProcessLog> valueCapture = ArgumentCaptor.forClass(SippProcessLog.class);
		doNothing().when(sippProcessLogDao).update(valueCapture.capture());

		service.processDisseminationList(1);

		verify(sippProcessLogDao).add(any(SippProcessLog.class));
		verify(sippProcessLogDao).update(any(SippProcessLog.class));
		verify(sippProcess).processInformationProduct(ProcessType.DISSEMINATION, "IP-1234");
		verify(sippProcess).processInformationProduct(ProcessType.DISSEMINATION, "IP-5678");
		verify(ipdsBureauApprovalDao).getIpdsBureauApprovals(1);

		assertEquals(6, valueCapture.getValue().getId().intValue());
		assertEquals(ProcessType.DISSEMINATION, valueCapture.getValue().getProcessType());
		assertEquals(2, valueCapture.getValue().getTotalEntries().intValue());
		assertEquals(1, valueCapture.getValue().getPublicationsAdded().intValue());
		assertEquals(1, valueCapture.getValue().getErrorsEncountered().intValue());
		assertEquals("\n\n(IP-1234) added to MyPubs as ProdId: 1234\n" + "Error processing IPNumber 'IP-5678': oops", valueCapture.getValue().getProcessingDetails());
	}

	@Test
	public void logProcessStartTest() {
		when(sippProcessLogDao.add(any(SippProcessLog.class)))
			.thenReturn(1);

		SippProcessLog sippProcessLog = service.logProcessStart();

		verify(sippProcessLogDao).add(any(SippProcessLog.class));
		assertEquals(1, sippProcessLog.getId().intValue());
		assertEquals(ProcessType.DISSEMINATION, sippProcessLog.getProcessType());
		assertNull(sippProcessLog.getTotalEntries());
		assertNull(sippProcessLog.getPublicationsAdded());
		assertNull(sippProcessLog.getErrorsEncountered());
		assertNull(sippProcessLog.getProcessingDetails());
	}

	@Test
	public void logProcessEndTest() {
		ArgumentCaptor<SippProcessLog> valueCapture = ArgumentCaptor.forClass(SippProcessLog.class);
		doNothing().when(sippProcessLogDao).update(valueCapture.capture());

		SippProcessLog sippProcessLog = new SippProcessLog();
		sippProcessLog.setId(99);
		sippProcessLog.setProcessType(ProcessType.SPN_PRODUCTION);

		service.logProcessEnd(sippProcessLog, 20, 15, 33, new StringBuilder("bl;ah"));

		verify(sippProcessLogDao).update(any(SippProcessLog.class));
		assertEquals(99, valueCapture.getValue().getId().intValue());
		assertEquals(ProcessType.SPN_PRODUCTION, valueCapture.getValue().getProcessType());
		assertEquals(20, valueCapture.getValue().getTotalEntries().intValue());
		assertEquals(15, valueCapture.getValue().getPublicationsAdded().intValue());
		assertEquals(33, valueCapture.getValue().getErrorsEncountered().intValue());
		assertEquals("bl;ah", valueCapture.getValue().getProcessingDetails());
	}

	@Test
	public void buildPublicationProcessSummaryTest() {
		MpPublication mpPublication = new MpPublication();
		mpPublication.setId(1234);
		mpPublication.setTitle("test");
		mpPublication.setIndexId("ds1");

		ProcessSummary processSummary = service.buildPublicationProcessSummary("IP-12345", mpPublication);

		assertEquals(0, processSummary.getErrors());
		assertEquals(1, processSummary.getAdditions());
		assertEquals("\n\n(IP-12345) added to MyPubs as ProdId: 1234", processSummary.getProcessingDetails());

		mpPublication.addValidatorResult(new ValidatorResult("publicationType", "must not be null", SeverityLevel.FATAL, null));
		processSummary = service.buildPublicationProcessSummary("IP-12345", mpPublication);

		assertEquals(1, processSummary.getErrors());
		assertEquals(0, processSummary.getAdditions());
		assertEquals("\n\n(IP-12345) not added:\n" +
				"\tField:publicationType - Message:must not be null - Level:FATAL - Value:null\n" +
				"\tValidator Results: 1 result(s)\n\t", processSummary.getProcessingDetails());
	}


	private MpPublication getPub1234() {
		MpPublication pub = new MpPublication();
		pub.setId(1234);

		return pub;
	}
}
