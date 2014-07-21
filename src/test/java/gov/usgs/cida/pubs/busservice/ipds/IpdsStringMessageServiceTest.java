package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author drsteini
 *
 */
//TODO Why am I not rolling back tests?
@Ignore
public class IpdsStringMessageServiceTest extends BaseSpringTest {

    private class TIpdsProcess implements IIpdsProcess {
        @Override
        public String processLog(ProcessType inProcessType, int logId) throws Exception {
            return "Did Processing";
        }
    }

    private class TIpdsWsRequester extends IpdsWsRequester {
        @Override
        public String getIpdsProductXml(final String asOf) {
            return "<root>" + asOf + "</root>";
        }
    }

    @Test
    public void testNoDate() {
        IpdsStringMessageService ms = new IpdsStringMessageService();
        IIpdsProcess ipdsProcess = new TIpdsProcess();
        IpdsWsRequester requester = new TIpdsWsRequester();
        ms.setIpdsProcess(ipdsProcess);
        ms.setIpdsWsRequester(requester);
        try {
            ms.processIpdsMessage(null);
            List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getAll();
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
        IpdsStringMessageService ms = new IpdsStringMessageService();
        IIpdsProcess ipdsProcess = new TIpdsProcess();
        IpdsWsRequester requester = new TIpdsWsRequester();
        ms.setIpdsProcess(ipdsProcess);
        ms.setIpdsWsRequester(requester);
        try {
            ms.processIpdsMessage("");
            List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getAll();
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
        IpdsStringMessageService ms = new IpdsStringMessageService();
        IIpdsProcess ipdsProcess = new TIpdsProcess();
        IpdsWsRequester requester = new TIpdsWsRequester();
        ms.setIpdsProcess(ipdsProcess);
        ms.setIpdsWsRequester(requester);
        try {
            ms.processIpdsMessage("2013-10-31");
            List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getAll();
            assertNotNull(logs);
            assertEquals(1, logs.size());
            assertEquals("<root>2013-10-31</root>", logs.get(0).getMessageText());
            assertEquals("Did Processing", logs.get(0).getProcessingDetails());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
