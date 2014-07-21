package gov.usgs.cida.pubs.busservice.ipds;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.utility.PubsEMailer;

import org.apache.http.protocol.BasicHttpContext;
import org.joda.time.LocalDate;

/**
 * @author drsteini
 *
 */
public class SpnProductionMessageService implements IIpdsService<String> {

    private IIpdsProcess ipdsProcess;
    private IpdsWsRequester requester;
    private PubsEMailer pubsEMailer;

    /**
     * {@inheritDoc}
     * @see gov.usgs.cida.mypubsJMS.service.intfc.IService#processIpdsMessage(java.lang.Object)
     */
    @Override
    public void processIpdsMessage(final String targetDate) throws Exception {
        LocalDate asOf = (null == targetDate || 0 == targetDate.length()) ? new LocalDate() : new LocalDate(targetDate);
        requester.setHttpContext(new BasicHttpContext());
        String atomFeed = requester.getSpnProduction(asOf.toString());
        IpdsMessageLog newMessage = new IpdsMessageLog();
        newMessage.setMessageText(atomFeed);
        newMessage.setProcessType(ProcessType.SPN_PRODUCTION);
        IpdsMessageLog msg = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(newMessage));

        String processingDetails = ipdsProcess.processLog(ProcessType.SPN_PRODUCTION, msg.getId());

        if (processingDetails.contains("ERROR")) {
            pubsEMailer.sendMail("Bad Errors processing SPN Production - log:" + msg.getId(), processingDetails);
        }
        msg.setProcessingDetails(processingDetails);
        IpdsMessageLog.getDao().update(msg);
    }

    public void setIpdsProcess(final IIpdsProcess inIpdsProcess) {
        ipdsProcess = inIpdsProcess;
    }
    public void setIpdsWsRequester(final IpdsWsRequester inIpdsWsRequester) {
        requester = inIpdsWsRequester;
    }
    public void setPubsEMailer(final PubsEMailer inPubsEMailer) {
        pubsEMailer = inPubsEMailer;
    }

}
