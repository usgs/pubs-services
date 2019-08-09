package gov.usgs.cida.pubs.busservice.sipp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.dao.ipds.IpdsMessageLogDao;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, IpdsMessageLog.class, IpdsMessageLogDao.class})
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class DisseminationListServiceIT extends BaseIT {

	public static final String MOCK_SIPP_URL = "https://localhost/mock?a=";

	@MockBean(name="pwPublicationDao")
	protected IPwPublicationDao pwPublicationDao;
	@MockBean(name="publicationDao")
	protected IPublicationDao publicationDao;
	@MockBean(name="restTemplate")
	protected RestTemplate restTemplate;
	@MockBean(name="configurationService")
	protected ConfigurationService configurationService;
	@MockBean
	protected IMpPublicationBusService pubBusService;
	@MockBean
	protected PlatformTransactionManager transactionManager;

	protected DisseminationListService service;
	protected SippProcess sippProcess;
	protected IpdsMessageLog ipdsMessageLog;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		sippProcess = new SippProcess(configurationService, restTemplate, pubBusService, transactionManager);
		service = new DisseminationListService(configurationService, sippProcess, restTemplate);
		resetThreadLocals();
		when(restTemplate.getForEntity(MOCK_SIPP_URL+"1", String.class))
			.thenReturn(new ResponseEntity<String>(getFile("testData/sipp/ipdsBureauApproval.xml"), HttpStatus.OK),
					new ResponseEntity<String>(getFile("testData/sipp/ipdsBureauApproval.xml"), HttpStatus.BAD_REQUEST));
		when(configurationService.getDisseminationListUrl()).thenReturn(MOCK_SIPP_URL);
	}

	@Test
	@ExpectedDatabase(
			value="classpath:/testResult/sipp/dissemination/csv/",
			assertionMode=DatabaseAssertionMode.NON_STRICT_UNORDERED,
			table="ipds_message_log",
			query="select ipds_message, regexp_replace(processing_details, '(' || ipds_message_log_id || ')', '123') processing_details, process_type from ipds_message_log")
	public void processDisseminationListTest() {
		service.processDisseminationList(1);
	}

	@Test
	public void getIpdsProductXmlTest() throws Exception {
		assertEquals(getFile("testData/sipp/ipdsBureauApproval.xml"), service.getIpdsProductXml(1));
		assertNull(service.getIpdsProductXml(1));
	}

	@Test
	@DatabaseSetup("classpath:/testData/sipp/csv/")
	public void processLogTest() {
		String expectedMsg = "Summary:\n\tTotal Entries: 2\n\tPublications Added: 0\n\tErrors Encountered: 2\n" + 
				"\n" + 
				"IP-100081:\n" + 
				"	ERROR: Trouble processing pub: IP-100081 - null\n" + 
				"\n" + 
				"IP-110902:\n" + 
				"	ERROR: Trouble processing pub: IP-110902 - null\n\nLog: 1\n";
		assertEquals(expectedMsg, service.processLog(ProcessType.DISSEMINATION, 1));

		//Should be the same message as the ThreadLocals are reset at start of method
		assertEquals(expectedMsg, service.processLog(ProcessType.DISSEMINATION, 1));
	}

	protected void resetThreadLocals() {
		DisseminationListService.setErrors(0);
		DisseminationListService.setStringBuilder(new StringBuilder(""));
		DisseminationListService.setAdditions(0);
	}

}
