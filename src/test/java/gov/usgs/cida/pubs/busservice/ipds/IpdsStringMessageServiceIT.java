package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.cida.pubs.dao.ipds.IpdsMessageLogDao;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, IpdsMessageLog.class, IpdsMessageLogDao.class})
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class IpdsStringMessageServiceIT extends BaseMessageServiceTest {

	private IpdsStringMessageService service;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		service = new IpdsStringMessageService(ipdsProcess, requester);
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
			fail(e.getMessage());
		}
	}

}
