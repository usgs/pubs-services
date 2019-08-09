package gov.usgs.cida.pubs.jms;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Test;

import gov.usgs.cida.pubs.BaseTest;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;

public class IpdsMessageListenerTest extends BaseTest {

	private class Spms implements IIpdsService {
		public MessagePayload messagePayload;
		@Override
		public void processIpdsMessage(MessagePayload messagePayload) {
			this.messagePayload = messagePayload;
		}
	}

	private Spms spms;
	private ConnectionFactory connectionFactory;
	private IpdsMessageListener mc;
	private Connection conn;
	private Session sess;

	@Before
	public void setUp() throws Exception {
		spms = new Spms();
		connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
		mc = new IpdsMessageListener(spms);
		conn = connectionFactory.createConnection();
		sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	@Test
	public void testNullMessage() {
		try {
			mc.onMessage(null);
			fail("should have gotten \"Invalid Message\"");
		} catch (Exception e) {
			assertEquals("Bad JMS Karma", e.getMessage());
			assertEquals("Invalid Message Type", e.getCause().getMessage());
		}
	}

		@Test
		public void testNullTextMessage() {
			try {
				TextMessage message = sess.createTextMessage(null);
				mc.onMessage(message);
				fail("should have gotten \"Invalid Message\"");
			} catch (Exception e) {
				assertEquals("Bad JMS Karma", e.getMessage());
				assertEquals("Invalid Message - No Content", e.getCause().getMessage());
			}
	}

		@Test
		public void testEmptyTextMessage() {
			try {
				TextMessage message = sess.createTextMessage("");
				mc.onMessage(message);
				fail("should have gotten \"Invalid Message\"");
			} catch (Exception e) {
				assertEquals("Bad JMS Karma", e.getMessage());
				assertEquals("Invalid Message - No Content", e.getCause().getMessage());
			}
	}

		@Test
		public void testNonJsonTextMessage() {
			try {
				TextMessage message = sess.createTextMessage("abc");
				mc.onMessage(message);
				fail("should have gotten \"Unrecognized token\"");
			} catch (Exception e) {
				assertEquals("Bad JMS Karma", e.getMessage());
				assertTrue(e.getCause().getMessage().startsWith("Unrecognized token"));
			}
	}

		@Test
		public void testWrongMessageType() {
			try {
				MapMessage mmessage = sess.createMapMessage();
				mc.onMessage(mmessage);
				fail("should have gotten \"Invalid Message\"");
			} catch (Exception e){
				assertEquals("Bad JMS Karma", e.getMessage());
				assertEquals("Invalid Message Type", e.getCause().getMessage());
			}
		}

		@Test
		public void testSparseSpnMessage() {
			try {
				TextMessage message = sess.createTextMessage("{\"type\":\"SPN_PRODUCTION\"}");
				mc.onMessage(message);
				assertNotNull(spms.messagePayload);
				assertEquals("SPN_PRODUCTION", spms.messagePayload.getType());
				assertEquals(LocalDate.now().toString(), spms.messagePayload.getAsOfString());
			} catch (JMSException e) {
				e.printStackTrace();
				fail();
			}
		}

		@Test
		public void testFullSpnMessage() {
			try {
				TextMessage message = sess.createTextMessage("{\"type\":\"SPN_PRODUCTION\",\"asOfDate\":\"2012-01-01\",\"context\":\"one\"}");
				mc.onMessage(message);
				assertNotNull(spms.messagePayload);
				assertEquals("SPN_PRODUCTION", spms.messagePayload.getType());
				assertEquals("2012-01-01", spms.messagePayload.getAsOfString());
				assertEquals("one", spms.messagePayload.getContext());
			} catch (JMSException e) {
				e.printStackTrace();
				fail();
			}
		}

		@Test
		public void testSparseMessage() {
			try {
				TextMessage message = sess.createTextMessage("{}");
				mc.onMessage(message);
				fail("should have gotten \"Invalid Message\"");
			} catch (Exception e) {
				assertEquals("Bad JMS Karma", e.getMessage());
				assertEquals("Invalid Message - Not SPN Production", e.getCause().getMessage());
			}
		}

}
