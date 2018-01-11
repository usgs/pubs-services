package gov.usgs.cida.pubs.jms;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.domain.ProcessType;

@Component
public class IpdsMessageListener implements MessageListener {

	private static final Log LOG = LogFactory.getLog(IpdsMessageListener.class);

	protected final IIpdsService ipdsStringMessageService;

	protected final IIpdsService spnProductionMessageService;

	@Autowired
	public IpdsMessageListener(@Qualifier("ipdsStringMessageService") final IIpdsService ipdsStringMessageService,
			@Qualifier("spnProductionMessageService") final IIpdsService spnProductionMessageService) {
		this.ipdsStringMessageService = ipdsStringMessageService;
		this.spnProductionMessageService = spnProductionMessageService;
	}

	@Transactional
	public void onMessage(final Message message) {
		LOG.info("Starting Processing the Message");

		try {
			if (message instanceof TextMessage) {
				processMessage((TextMessage) message);
			} else {
				throw new IllegalArgumentException("Invalid Message Type");
			}
		} catch (Exception e) {
			LOG.info(e);
			throw new RuntimeException("Bad JMS Karma", e);
		}

		LOG.info("Done Processing the Message");
	}

	protected void processMessage(final TextMessage message) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		if (StringUtils.hasText(message.getText())) {
			MessagePayload messagePayload = mapper.readValue(message.getText(), MessagePayload.class);
			if (null != messagePayload.getType() && messagePayload.getType().equalsIgnoreCase(ProcessType.SPN_PRODUCTION.toString())) {
				spnProductionMessageService.processIpdsMessage(messagePayload);
			} else {
				ipdsStringMessageService.processIpdsMessage(messagePayload);
			}
		} else {
			throw new IllegalArgumentException("Invalid Message - No Content");
		}
	}

}
