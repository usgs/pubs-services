package gov.usgs.cida.pubs.jms;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;
import gov.usgs.cida.pubs.domain.ProcessType;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class MessageConsumerTest extends BaseSpringTest {

    private class Isms implements IIpdsService<String> {
        public String msgText;
        @Override
        public void processIpdsMessage(String ipdsMessage) throws Exception {
            msgText = null == ipdsMessage ? "nullInput" : ipdsMessage;
        }
    }

    private class Spms implements IIpdsService<String> {
        public String msgText;
        @Override
        public void processIpdsMessage(String ipdsMessage) throws Exception {
            msgText = null == ipdsMessage ? "nullInput" : ipdsMessage;
        }
    }

    private class TMessageConsumer extends MessageConsumer {
        public void setIpdsStringMessageService(IIpdsService<String> isms) {
            ipdsStringMessageService = isms;
        }
        public void setSpnProductionMessageService(IIpdsService<String> spms) {
            spnProductionMessageService = spms;
        }
    }

    @Test
    public void testOnMessage() {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        TMessageConsumer mc = new TMessageConsumer();
        Isms isms = new Isms();
        Spms spms = new Spms();
        mc.setIpdsStringMessageService(isms);
        mc.setSpnProductionMessageService(spms);
        try {
            Connection conn = connectionFactory.createConnection();
            Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            TextMessage message = sess.createTextMessage("hi");
            mc.onMessage(message);
            assertEquals("hi", isms.msgText);
            assertNull(spms.msgText);

            message = sess.createTextMessage("2013-10-25");
            mc.onMessage(message);
            assertEquals("2013-10-25", isms.msgText);
            assertNull(spms.msgText);

            message = sess.createTextMessage(null);
            mc.onMessage(message);
            assertEquals("nullInput", isms.msgText);
            assertNull(spms.msgText);

            isms.msgText = null;
            message = sess.createTextMessage(ProcessType.SPN_PRODUCTION.toString()+"abc");
            mc.onMessage(message);
            assertNull(isms.msgText);
            assertEquals("abc", spms.msgText);

            try {
                MapMessage mmessage = sess.createMapMessage();
                mc.onMessage(mmessage);
                fail("should have gotten \"Invalid Message\"");
            } catch (Exception e){
                assertEquals("Bad JMS Karma", e.getMessage());
                assertEquals("Invalid Message", e.getCause().getMessage());
            }
        } catch (JMSException e) {
            e.printStackTrace();
            fail();
        }

    }

}
