package gov.usgs.cida.pubs.jms;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;

public class IpdsMessageListenerTest extends BaseSpringTest {

	private class Isms implements IIpdsService {
		public MessagePayload messagePayload;
		@Override
		public void processIpdsMessage(MessagePayload messagePayload) {
			this.messagePayload = messagePayload;
		}
	}

	private Isms isms;

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
		isms = new Isms();
		spms = new Spms();
		connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
		mc = new IpdsMessageListener(isms, spms);
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
				assertNull(isms.messagePayload);
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
				assertNull(isms.messagePayload);
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
				assertNotNull(isms.messagePayload);
				assertEquals(LocalDate.now().toString(), isms.messagePayload.getAsOfString());
				assertNull(spms.messagePayload);
			} catch (JMSException e) {
				e.printStackTrace();
				fail();
			}
		}

		@Test
		public void testSparseDisseminationMessage() {
			try {
				TextMessage message = sess.createTextMessage("{\"type\":\"DISSEMINATIO\"}");
				mc.onMessage(message);
				assertNotNull(isms.messagePayload);
				assertEquals("DISSEMINATIO", isms.messagePayload.getType());
				assertEquals(LocalDate.now().toString(), isms.messagePayload.getAsOfString());
				assertNull(spms.messagePayload);
			} catch (JMSException e) {
				e.printStackTrace();
				fail();
			}
		}

		@Test
		public void testFullDisseminationMessage() {
			try {
				TextMessage message = sess.createTextMessage("{\"type\":\"DISSEMINATION\",\"asOfDate\":\"2012-01-01\",\"context\":\"one\"}");
				mc.onMessage(message);
				assertNotNull(isms.messagePayload);
				assertEquals("DISSEMINATION", isms.messagePayload.getType());
				assertEquals("2012-01-01", isms.messagePayload.getAsOfString());
				assertEquals("one", isms.messagePayload.getContext());
				assertNull(spms.messagePayload);
			} catch (JMSException e) {
				e.printStackTrace();
				fail();
			}
		}

}
