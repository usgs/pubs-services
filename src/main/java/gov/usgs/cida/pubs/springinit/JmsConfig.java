package gov.usgs.cida.pubs.springinit;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import gov.usgs.cida.pubs.ConfigurationService;

@Configuration
public class JmsConfig {

	@Autowired
	private ConfigurationService configurationService;
	@Autowired
	@Qualifier("ipdsMessageListener")
	private MessageListener ipdsMessageListener;

	@Autowired
	private ConnectionFactory connectionFactory;

	@Bean
	public DefaultMessageListenerContainer mlc() throws NamingException {
		DefaultMessageListenerContainer mlc = new DefaultMessageListenerContainer();
		mlc.setConcurrentConsumers(1);
		mlc.setMaxConcurrentConsumers(1);
		mlc.setConnectionFactory(connectionFactory);
		mlc.setDestinationName(configurationService.getIpdsQueueName());
		mlc.setMessageListener(ipdsMessageListener);
		mlc.setSessionTransacted(true);
		return mlc;
	}

}
