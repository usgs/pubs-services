package gov.usgs.cida.pubs.jms;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.domain.ProcessType;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class MessageConsumer implements MessageListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private IIpdsService<String> ipdsStringMessageService;

    private IIpdsService<String> spnProductionMessageService;

    @Transactional
    public void onMessage(final Message message) {
        log.info("Starting Processing the Message");

        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String messageText = textMessage.getText();
                if (null != messageText && messageText.startsWith(ProcessType.SPN_PRODUCTION.toString())) {
                    spnProductionMessageService.processIpdsMessage(messageText.replace(ProcessType.SPN_PRODUCTION.toString(), ""));
                } else {
                    ipdsStringMessageService.processIpdsMessage(messageText);
                }
            } else {
                throw new IllegalArgumentException("Invalid Message");
            }
        } catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Bad JMS Karma", e);
        }

        log.info("Done Processing the Message");
    }

    public void setIpdsStringMessageService(final IIpdsService<String> inIpdsStringMessageService) {
        ipdsStringMessageService = inIpdsStringMessageService;
    }

    public void setSpnProductionMessageService(final IIpdsService<String> inSpnProductionMessageService) {
        spnProductionMessageService = inSpnProductionMessageService;
    }

}
