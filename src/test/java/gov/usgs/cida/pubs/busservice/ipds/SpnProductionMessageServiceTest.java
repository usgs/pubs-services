package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.utility.PubsEMailer;

import java.util.List;

import javax.annotation.Resource;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author drsteini
 *
 */

public class SpnProductionMessageServiceTest extends BaseSpringTest {

    @Mock
    private IpdsProcess ipdsProcess;

    @Mock
    private IpdsWsRequester requester;

    @Mock
    private PubsEMailer pubsEMailer;

    public SpnProductionMessageService service;

    @Resource(name="feedXml")
    public String feedXml;

    @Resource(name="badXml")
    public String badXml;

    @Before
    public void setUp() throws Exception {
       MockitoAnnotations.initMocks(this);
       when(requester.getSpnProduction(anyString())).thenAnswer(new Answer<String>() {
           @Override
           public String answer(InvocationOnMock invocation) throws Throwable {
             Object[] args = invocation.getArguments();
             return "<root>" + (String) args[0] + "</root>";
           }
         });
       when(ipdsProcess.processLog(any(ProcessType.class), anyInt())).thenReturn("Did Processing");
       service = new SpnProductionMessageService(ipdsProcess, requester, pubsEMailer);
    }

    @Test
    public void testNoDate() throws Exception {
        try {
            service.processIpdsMessage(null);
            List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
            assertNotNull(logs);
            assertEquals(1, logs.size());
            assertEquals("<root>" + new LocalDate() + "</root>", logs.get(0).getMessageText());
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
            assertEquals("<root>" + new LocalDate() + "</root>", logs.get(0).getMessageText());
            assertEquals("Did Processing", logs.get(0).getProcessingDetails());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testADate() {
        try {
            service.processIpdsMessage("2013-10-31");
            List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
            assertNotNull(logs);
            assertEquals(1, logs.size());
            assertEquals("<root>2013-10-31</root>", logs.get(0).getMessageText());
            assertEquals("Did Processing", logs.get(0).getProcessingDetails());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testErrors() throws Exception {
        when(ipdsProcess.processLog(any(ProcessType.class), anyInt())).thenReturn("Did ERROR Processing");
        try {
            service.processIpdsMessage("2013-10-31");
            List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
            assertNotNull(logs);
            assertEquals(1, logs.size());
            assertEquals("<root>2013-10-31</root>", logs.get(0).getMessageText());
            assertEquals("Did ERROR Processing", logs.get(0).getProcessingDetails());
            verify(pubsEMailer, times(1)).sendMail(anyString(), anyString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void quickAndDirty() throws Exception {
      when(requester.getSpnProduction(anyString())).thenReturn(badXml);
      service.processIpdsMessage(null);    	
    }

}
