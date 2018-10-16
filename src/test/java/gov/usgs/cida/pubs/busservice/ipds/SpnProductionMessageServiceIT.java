package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.cida.pubs.dao.ipds.IpdsMessageLogDao;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.springinit.DbTestConfig;
import gov.usgs.cida.pubs.utility.PubsEMailer;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, IpdsMessageLog.class, IpdsMessageLogDao.class})
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class SpnProductionMessageServiceIT extends BaseMessageServiceTest {

	@Autowired
	IpdsMessageLogDao ipdsMessageLogDao;
	@MockBean
	private PubsEMailer pubsEMailer;

	private SpnProductionMessageService service;
	private static final String PROCESSING_DETAILS = "Did ERROR Processing";

	@Before
	public void setUp() throws Exception {
		super.setUp();
		service = new SpnProductionMessageService(ipdsProcess, requester, pubsEMailer);
	}

	@Test
	public void testADate() {
		try {
			service.processIpdsMessage(getPayload());
			List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
			assertNotNull(logs);
			assertEquals(1, logs.size());
			assertEquals(EXPECTED_MESSAGE_TEXT, logs.get(0).getMessageText());
			assertEquals("Did Processing", logs.get(0).getProcessingDetails());
		} catch (Exception e) {
			LOG.info(e.getLocalizedMessage());
			fail(e.getMessage());
		}
	}

	@Test
	public void testErrors() throws Exception {
		when(ipdsProcess.processLog(any(ProcessType.class), anyInt(), anyString())).thenReturn(PROCESSING_DETAILS);
		try {
			service.processIpdsMessage(getPayload());
			List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
			assertNotNull(logs);
			assertEquals(1, logs.size());
			assertEquals(EXPECTED_MESSAGE_TEXT, logs.get(0).getMessageText());
			assertEquals(PROCESSING_DETAILS, logs.get(0).getProcessingDetails());
			verify(pubsEMailer, times(1)).sendMail(service.buildSubject(), PROCESSING_DETAILS);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
