package gov.usgs.cida.pubs.jms;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.busservice.intfc.IIpdsService;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author drsteini
 *
 */
public class CostCenterMessageListenerTest extends BaseSpringTest {

	@Mock
	public IIpdsService service;

	private CostCenterMessageListener mc;
	
	@Before
	public void initTest() throws Exception {
		MockitoAnnotations.initMocks(this);
		mc = new CostCenterMessageListener(service);
	}

	@Test
	public void testOnMessage() {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
		try {
			Connection conn = connectionFactory.createConnection();
			Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			TextMessage message = sess.createTextMessage(null);
			mc.onMessage(message);
			verify(service, times(1)).processIpdsMessage(null);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testOnMessageErrorThown() {
		try {
			doThrow(new RuntimeException()).when(service).processIpdsMessage(null);

			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
			Connection conn = connectionFactory.createConnection();
			Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			TextMessage message = sess.createTextMessage(null);
			mc.onMessage(message);
			verify(service, times(1)).processIpdsMessage(null);
		} catch (Exception e) {
			assertTrue(e instanceof RuntimeException);
			assertEquals("Bad JMS Karma - CostCenter", e.getMessage());
		}

	}

}
