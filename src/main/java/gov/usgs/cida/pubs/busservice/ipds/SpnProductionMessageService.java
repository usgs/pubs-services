package gov.usgs.cida.pubs.busservice.ipds;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.jms.MessagePayload;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import java.time.format.FormatStyle;

@Service
public class SpnProductionMessageService implements IIpdsService {

	private final IIpdsProcess ipdsProcess;

	private final IpdsWsRequester requester;

	private final PubsEMailer pubsEMailer;
	
	private final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

	@Autowired
	SpnProductionMessageService(final IIpdsProcess ipdsProcess, final IpdsWsRequester requester,
			final PubsEMailer pubsEMailer) {
		this.ipdsProcess = ipdsProcess;
		this.requester = requester;
		this.pubsEMailer = pubsEMailer;
	}

	@Override
	@Transactional
	public void processIpdsMessage(final MessagePayload messagePayload) {
		String atomFeed = requester.getSpnProduction(messagePayload);

		IpdsMessageLog newMessage = new IpdsMessageLog();
		newMessage.setMessageText(atomFeed);
		newMessage.setProcessType(ProcessType.SPN_PRODUCTION);
		IpdsMessageLog msg = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(newMessage));

		String processingDetails = ipdsProcess.processLog(ProcessType.SPN_PRODUCTION, msg.getId(), messagePayload.getContext());

		if (processingDetails.contains("ERROR")) {
			LocalDateTime now = LocalDateTime.now();
			pubsEMailer.sendMail("Bad Errors processing SPN Production - " + now.format(formatter), processingDetails);
		}
		msg.setProcessingDetails(processingDetails);
		IpdsMessageLog.getDao().update(msg);
	}

}
