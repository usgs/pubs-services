package gov.usgs.cida.pubs.springinit;

import javax.jms.MessageListener;
import javax.naming.NamingException;

import org.apache.activemq.RedeliveryPolicy;
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

	@Bean
	public ActiveMQConnectionFactory connectionFactory() throws NamingException {
		ActiveMQConnectionFactory amqFactory = new ActiveMQConnectionFactory();
		amqFactory.setBrokerURL(brokerURL);
		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		redeliveryPolicy.setMaximumRedeliveries(0);
		amqFactory.setRedeliveryPolicy(redeliveryPolicy);
		return amqFactory;
	}

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

}
