package gov.usgs.cida.pubs.busservice.ipds;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.utility.PubsEscapeXML10;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class IpdsStringMessageService implements IIpdsService {

    private final IIpdsProcess ipdsProcess;

    private final IpdsWsRequester requester;

    @Autowired
    IpdsStringMessageService(final IIpdsProcess ipdsProcess, final IpdsWsRequester requester) {
        this.ipdsProcess = ipdsProcess;
        this.requester = requester;
    }

    /** {@inheritDoc}
     * @throws Exception 
     * @see gov.usgs.cida.mypubsJMS.service.intfc.IService#processIpdsMessage(java.lang.Object)
     */
    @Override
    @Transactional
    public void processIpdsMessage(final String targetDate) throws Exception {
        LocalDate asOf = (null == targetDate || 0 == targetDate.length()) ? new LocalDate() : new LocalDate(targetDate);
        String inMessageText = requester.getIpdsProductXml(asOf.toString());
        IpdsMessageLog newMessage = new IpdsMessageLog();
        newMessage.setMessageText(PubsEscapeXML10.ESCAPE_XML10.translate(inMessageText));
        newMessage.setProcessType(ProcessType.DISSEMINATION);
        IpdsMessageLog msg = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(newMessage));

        String processingDetails = ipdsProcess.processLog(ProcessType.DISSEMINATION, msg.getId());

        msg.setProcessingDetails(processingDetails);
        IpdsMessageLog.getDao().update(msg);
    }

}
