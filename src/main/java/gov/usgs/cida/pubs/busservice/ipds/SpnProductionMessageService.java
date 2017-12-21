package gov.usgs.cida.pubs.busservice.ipds;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsProcess;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.utility.PubsEMailer;
import gov.usgs.cida.pubs.utility.PubsEscapeXML10;

/**
 * @author drsteini
 *
 */
@Service
public class SpnProductionMessageService implements IIpdsService {

	private final IIpdsProcess ipdsProcess;

	private final IpdsWsRequester requester;

	private final PubsEMailer pubsEMailer;

	@Autowired
	SpnProductionMessageService(final IIpdsProcess ipdsProcess, final IpdsWsRequester requester,
			final PubsEMailer pubsEMailer) {
		this.ipdsProcess = ipdsProcess;
		this.requester = requester;
		this.pubsEMailer = pubsEMailer;
	}

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.mypubsJMS.service.intfc.IService#processIpdsMessage(java.lang.Object)
	 */
	@Override
	@Transactional
	public void processIpdsMessage(final String message) {
		IpdsMessageLog newMessage = new IpdsMessageLog();
		newMessage.setProcessType(ProcessType.SPN_PRODUCTION);
		
		String date = null;
		String context = null;

		try {
			JSONObject json = new JSONObject(message);
			date = json.getString("date");
			context = json.getString("context");
			String atomFeed = requester.getIpdsProductXml(date, context);
			newMessage.setMessageText(PubsEscapeXML10.ESCAPE_XML10.translate(atomFeed));
			IpdsMessageLog msg = IpdsMessageLog.getDao().getById(IpdsMessageLog.getDao().add(newMessage));
			String processingDetails = ipdsProcess.processLog(ProcessType.SPN_PRODUCTION, msg.getId(), context);
			if (processingDetails.contains("ERROR")) {
				pubsEMailer.sendMail("Bad Errors processing SPN Production - log:" + msg.getId(), processingDetails);
			}
			msg.setProcessingDetails(processingDetails);
			IpdsMessageLog.getDao().update(msg);
		} catch (Exception e) {
			String errorMessage = "<root>Error parsing JSON from message: " + e.getMessage() + "</root>";
			newMessage.setMessageText(PubsEscapeXML10.ESCAPE_XML10.translate(errorMessage));
			IpdsMessageLog.getDao().add(newMessage);
		}
	}

}
