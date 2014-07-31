package gov.usgs.cida.pubs.busservice.ipds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author drsteini
 *
 */
//TODO Why am I not rolling back tests?
@Ignore
public class SpnProductionMessageServiceTest extends BaseSpringTest {

    private class TIpdsProcess implements IIpdsProcess {
        @Override
        public String processLog(ProcessType inProcessType, int logId) throws Exception {
            return "Did Processing";
        }
    }

    private class TIpdsWsRequester extends IpdsWsRequester {
        @Override
        public String getSpnProduction(final String targetDate) {
            return "<root>SPN</root>";
        }
    }

    @Test
    public void testIt() {
        SpnProductionMessageService ms = new SpnProductionMessageService();
        IIpdsProcess ipdsProcess = new TIpdsProcess();
        IpdsWsRequester requester = new TIpdsWsRequester();
//        ms.setIpdsProcess(ipdsProcess);
//        ms.setIpdsWsRequester(requester);
        try {
            ms.processIpdsMessage(null);
            List<IpdsMessageLog> logs = IpdsMessageLog.getDao().getByMap(null);
            assertNotNull(logs);
            assertEquals(1, logs.size());
            assertEquals("<root>SPN</root>", logs.get(0).getMessageText());
            assertEquals("Did Processing", logs.get(0).getProcessingDetails());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
