package gov.usgs.cida.pubs.busservice.ipds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;

import java.util.List;

import javax.annotation.Resource;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * @author drsteini
 *
 */
@Category(IntegrationTest.class)
@DatabaseSetup("classpath:/testCleanup/clearAll.xml")
public class IpdsStringMessageServiceTest extends BaseSpringTest {

	@Mock
	private IpdsProcess ipdsProcess;

	@Mock
	private IpdsWsRequester requester;

	public IpdsStringMessageService service;

	@Resource(name="badXml")
	public String badXml;
	
	private static final String DATEVAR = "DATEVAR";
	private static final String CONTEXTVAR = "CONTEXTVAR";
	private static final String JSON = "{\"date\":\"DATEVAR\",\"context\":\"CONTEXTVAR\"}";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(requester.getIpdsProductXml(anyString(), null)).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return "<root>" + (String) args[0] + "</root>";
			}
		});
		when(ipdsProcess.processLog(any(ProcessType.class), anyInt(), null)).thenReturn("Did Processing");
		service = new IpdsStringMessageService(ipdsProcess, requester);
	}

	@Test
	public void quickAndDirty() throws Exception {
		when(requester.getIpdsProductXml(anyString(), null)).thenReturn(badXml);
		service.processIpdsMessage(null);
	}

	@Test
	public void testNoDate() throws Exception {
		try {
			service.processIpdsMessage(null);
			List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
			assertNotNull(logs);
			assertEquals(1, logs.size());
			assertEquals("<root>" + LocalDate.now() + "</root>", logs.get(0).getMessageText());
			assertEquals("Did Processing", logs.get(0).getProcessingDetails());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testEmptyStringDate() {
		try {
			service.processIpdsMessage("");
			List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
			assertNotNull(logs);
			assertEquals(1, logs.size());
			assertEquals("<root>" + LocalDate.now() + "</root>", logs.get(0).getMessageText());
			assertEquals("Did Processing", logs.get(0).getProcessingDetails());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testADate() {
		try {
			service.processIpdsMessage(JSON.replace(DATEVAR, "2013-10-31").replace(CONTEXTVAR, "Content0"));
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
