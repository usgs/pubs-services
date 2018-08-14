package gov.usgs.cida.pubs.utility;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.ConfigurationService;

@Component
public class PubsEMailer {

	private static final Log LOG = LogFactory.getLog(PubsEMailer.class);
	private static final String PUBSV2_NO_REPLY_NAME = "PUBSV2_NO_REPLY";
	private static final String PUBSV2_NO_REPLY_ADDRESS = "pubsv2_no_reply@usgs.gov";

	private final ConfigurationService configurationService;

	@Autowired
	public PubsEMailer(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void sendMail(final String subject, final String msg) {
		try {
			InternetAddress addressFrom = new InternetAddress(PUBSV2_NO_REPLY_ADDRESS, PUBSV2_NO_REPLY_NAME);
			InternetAddress[] addressTo = new InternetAddress[]{new InternetAddress(configurationService.getPubsEmailList())};
			Properties props = new Properties();
			props.put("mail.smtp.host", configurationService.getMailHost());
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(session);

			message.setFrom(addressFrom);
			message.setRecipients(Message.RecipientType.TO, addressTo);
			message.setSubject(subject);
			message.setText(msg);

			Transport.send(message);
		} catch (Exception e) {
			LOG.info("Couldn't send mail:", e);
		}
	}

}
