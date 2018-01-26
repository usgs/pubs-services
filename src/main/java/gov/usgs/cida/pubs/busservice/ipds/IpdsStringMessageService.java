package gov.usgs.cida.pubs.busservice.ipds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.jms.MessagePayload;

@Service
public class IpdsStringMessageService implements IIpdsService {

	private final IIpdsProcess ipdsProcess;

	private final IpdsWsRequester requester;

	@Autowired
	IpdsStringMessageService(final IIpdsProcess ipdsProcess, final IpdsWsRequester requester) {
		this.ipdsProcess = ipdsProcess;
		this.requester = requester;
	}

	@Override
	@Transactional
	public void processIpdsMessage(final MessagePayload messagePayload) {
		String messageText = requester.getIpdsProductXml(messagePayload);

		IpdsMessageLog newMessage = new IpdsMessageLog();
		newMessage.setProcessType(ProcessType.DISSEMINATION);
		newMessage.setMessageText(messageText);
		IpdsMessageLog msg = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(newMessage));

		String processingDetails = ipdsProcess.processLog(ProcessType.DISSEMINATION, msg.getId(), messagePayload.getContext());
		msg.setProcessingDetails(processingDetails);
		IpdsMessageLog.getDao().update(msg);
	}
}
