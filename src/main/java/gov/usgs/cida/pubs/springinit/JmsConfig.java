package gov.usgs.cida.pubs.springinit;

import javax.jms.MessageListener;
import javax.naming.NamingException;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
public class JmsConfig {

	@Autowired
	String brokerURL;
	@Autowired
	String queueName;
	@Autowired
	@Qualifier("ipdsMessageListener")
	MessageListener ipdsMessageListener;
	@Autowired
	String costCenterQueueName;
	@Autowired
	@Qualifier("costCenterMessageListener")
	MessageListener costCenterListener;

	@Bean
	public ActiveMQConnectionFactory connectionFactory() throws NamingException {
		ActiveMQConnectionFactory amqFactory = new ActiveMQConnectionFactory();
		amqFactory.setBrokerURL(brokerURL);
		return amqFactory;
	}

//	@Bean
//	public BrokerService broker() throws NamingException, Exception {
//		//This will prevent the never ending logs from the BrokerService saying there is not enough Temporary Store space...
//		BrokerService broker = new BrokerService();
//		broker.addConnector(brokerURL());
//		broker.setPersistent(false);
//		SystemUsage systemUsage = broker.getSystemUsage();
//		systemUsage.getStoreUsage().setLimit(1024 * 1024 * 8);
//		systemUsage.getTempUsage().setLimit(1024 * 1024 * 8);
//		broker.start();
//		return broker;
//	}

	@Bean
	public DefaultMessageListenerContainer mlc() throws NamingException {
		DefaultMessageListenerContainer mlc = new DefaultMessageListenerContainer();
		mlc.setConcurrentConsumers(1);
		mlc.setMaxConcurrentConsumers(1);
		mlc.setConnectionFactory(connectionFactory());
		mlc.setDestinationName(queueName);
		mlc.setMessageListener(ipdsMessageListener);
		mlc.setSessionTransacted(true);
		return mlc;
	}

	@Bean
	public DefaultMessageListenerContainer ccmlc() throws NamingException {
		DefaultMessageListenerContainer ccmlc = new DefaultMessageListenerContainer();
		ccmlc.setConcurrentConsumers(1);
		ccmlc.setMaxConcurrentConsumers(1);
		ccmlc.setConnectionFactory(connectionFactory());
		ccmlc.setDestinationName(costCenterQueueName);
		ccmlc.setMessageListener(costCenterListener);
		ccmlc.setRecoveryInterval(50000);
		ccmlc.setSessionTransacted(true);
		return ccmlc;
	}

}
