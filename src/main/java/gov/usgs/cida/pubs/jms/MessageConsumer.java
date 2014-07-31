package gov.usgs.cida.pubs.jms;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.domain.ProcessType;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MessageConsumer implements MessageListener {

    public static final Log LOG = LogFactory.getLog(JmsExceptionListener.class);

    @Autowired
    protected IIpdsService<String> ipdsStringMessageService;

    @Autowired
    protected IIpdsService<String> spnProductionMessageService;

    @Transactional
    public void onMessage(final Message message) {
        LOG.info("Starting Processing the Message");

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
            LOG.info(e);
            throw new RuntimeException("Bad JMS Karma", e);
        }

        LOG.info("Done Processing the Message");
    }

//    public void setIpdsStringMessageService(final IIpdsService<String> inIpdsStringMessageService) {
//        ipdsStringMessageService = inIpdsStringMessageService;
//    }
//
//    public void setSpnProductionMessageService(final IIpdsService<String> inSpnProductionMessageService) {
//        spnProductionMessageService = inSpnProductionMessageService;
//    }
//
}
