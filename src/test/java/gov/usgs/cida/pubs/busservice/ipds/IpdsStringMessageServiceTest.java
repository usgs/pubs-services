package gov.usgs.cida.pubs.busservice.ipds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.jms.MessagePayload;

@Category(IntegrationTest.class)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class IpdsStringMessageServiceTest extends BaseSpringTest {

	@Mock
	private IpdsProcess ipdsProcess;

	@Mock
	private IpdsWsRequester requester;

	private IpdsStringMessageService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(requester.getIpdsProductXml(anyString(), anyString())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return "<root>" + (String) args[0] + "</root>";
			}
		});
		when(ipdsProcess.processLog(any(ProcessType.class), anyInt(), anyString())).thenReturn("Did Processing");
		service = new IpdsStringMessageService(ipdsProcess, requester);
	}

	@Test
	public void testADate() {
		try {
			MessagePayload payload = new MessagePayload();
			payload.setAsOfDate("2013-10-31");
			payload.setContext(IpdsProcessTest.TEST_IPDS_CONTEXT);
			service.processIpdsMessage(payload);
			List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
			assertNotNull(logs);
			assertEquals(1, logs.size());
			assertEquals("<root>2013-10-31</root>", logs.get(0).getMessageText());
			assertEquals("Did Processing", logs.get(0).getProcessingDetails());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
