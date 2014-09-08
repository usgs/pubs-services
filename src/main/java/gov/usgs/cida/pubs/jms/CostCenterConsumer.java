package gov.usgs.cida.pubs.jms;

import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

public class CostCenterConsumer implements MessageListener {

    private static final Log LOG = LogFactory.getLog(MessageConsumer.class);

    protected final IIpdsService costCenterMessageService;

    @Autowired
    public CostCenterConsumer(@Qualifier("costCenterMessageService") final IIpdsService costCenterMessageService) {
        this.costCenterMessageService = costCenterMessageService;
     }

    @Transactional
    public void onMessage(final Message message) {
        LOG.info("Starting Processing the Message");

        try {
        	costCenterMessageService.processIpdsMessage(null);
        } catch (final Exception e) {
            LOG.info(e);
            throw new RuntimeException("Bad JMS Karma - CostCenter", e);
        }

        LOG.info("Done Processing the CostCenter Message");
    }

}
