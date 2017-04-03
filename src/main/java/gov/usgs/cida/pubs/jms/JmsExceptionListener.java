package gov.usgs.cida.pubs.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import gov.usgs.cida.pubs.utility.PubsEMailer;

public class JmsExceptionListener implements ExceptionListener {
	private static final Log LOG = LogFactory.getLog(JmsExceptionListener.class);
	private final PubsEMailer pubsEMailer;

	@Autowired
	JmsExceptionListener(final PubsEMailer pubsEMailer) {
		this.pubsEMailer = pubsEMailer;
	}

	public void onException( final JMSException e ) {
		pubsEMailer.sendMail("Bad Errors in Messaging:" + e.getLocalizedMessage(), "");
		LOG.info(e);
	}

}
