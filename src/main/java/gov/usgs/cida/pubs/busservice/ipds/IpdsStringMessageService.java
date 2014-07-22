package gov.usgs.cida.pubs.busservice.ipds;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;

import org.apache.http.protocol.BasicHttpContext;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class IpdsStringMessageService implements IIpdsService<String> {

    @Autowired
    private IIpdsProcess ipdsProcess;

    @Autowired
    private IpdsWsRequester requester;

    /** {@inheritDoc}
     * @throws Exception 
     * @see gov.usgs.cida.mypubsJMS.service.intfc.IService#processIpdsMessage(java.lang.Object)
     */
    @Override
    @Transactional
    public void processIpdsMessage(final String targetDate) throws Exception {
        LocalDate asOf = (null == targetDate || 0 == targetDate.length()) ? new LocalDate() : new LocalDate(targetDate);
        requester.setHttpContext(new BasicHttpContext());
        String inMessageText = requester.getIpdsProductXml(asOf.toString());
        IpdsMessageLog newMessage = new IpdsMessageLog();
        newMessage.setMessageText(inMessageText);
        newMessage.setProcessType(ProcessType.DISSEMINATION);
        IpdsMessageLog msg = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(newMessage));

        String processingDetails = ipdsProcess.processLog(ProcessType.DISSEMINATION, msg.getId());

        msg.setProcessingDetails(processingDetails);
        IpdsMessageLog.getDao().update(msg);
    }

//    /**
//     * Set the service.
//     * @param inIpdsProcess .
//     */
//    public void setIpdsProcess(final IIpdsProcess inIpdsProcess) {
//        ipdsProcess = inIpdsProcess;
//    }
//
//    /**
//     * Set the requester.
//     * @param IpdsWsRequester .
//     */
//    public void setIpdsWsRequester(final IpdsWsRequester inIpdsWsRequester) {
//        requester = inIpdsWsRequester;
//    }
//
}
