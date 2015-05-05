package gov.usgs.cida.pubs.springinit;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.json.ViewAwareJsonMessageConverter;
import gov.usgs.cida.pubs.utility.CustomStringToArrayConverter;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
@ComponentScan(basePackages="gov.usgs.cida.pubs")
@ImportResource("classpath:spring/applicationContext.xml")
@EnableWebMvc
@EnableTransactionManagement
public class SpringConfig extends WebMvcConfigurerAdapter {
	
	private final Context ctx;
	
	@Autowired
	CustomStringToArrayConverter customStringToArrayConverter;
	
	public SpringConfig() throws NamingException {
		super();
        ctx = new InitialContext();
	}
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(customStringToArrayConverter);
	}
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
        	.favorPathExtension(false)
        	.favorParameter(true)
        	.parameterName(PubsConstants.CONTENT_PARAMETER_NAME)
        	.defaultContentType(MediaType.APPLICATION_JSON)
        	.mediaType(PubsConstants.MEDIA_TYPE_CSV_EXTENSION, PubsConstants.MEDIA_TYPE_CSV)
        	.mediaType(PubsConstants.MEDIA_TYPE_TSV_EXTENSION, PubsConstants.MEDIA_TYPE_TSV)
         	.mediaType(PubsConstants.MEDIA_TYPE_XML_EXTENSION, MediaType.APPLICATION_XML)
        	.mediaType(PubsConstants.MEDIA_TYPE_JSON_EXTENSION, MediaType.APPLICATION_JSON)
        	.mediaType(PubsConstants.MEDIA_TYPE_XLSX_EXTENSION, PubsConstants.MEDIA_TYPE_XLSX)
        	;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    	converters.add(new ViewAwareJsonMessageConverter());
    	extendMessageConverters(converters);
    }
    
    @Bean
    public DataSource dataSource() throws Exception {
        return (DataSource) ctx.lookup("java:comp/env/jdbc/mypubsDS");
    }
    
    @Bean
	public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		Resource mybatisConfig = new ClassPathResource("mybatis/mybatisConfig.xml");
		sqlSessionFactory.setConfigLocation(mybatisConfig);
		sqlSessionFactory.setDataSource(dataSource());
		return sqlSessionFactory;
	}

    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
    	DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
    	transactionManager.setDataSource(dataSource());
    	return transactionManager;
    }

	@Bean
	public String ipdsPubsWsPwd() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/ipds/pubsWsPwd");
	}
	
	@Bean
	public String ipdsPubsWsUser() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/ipds/pubsWsUser");
	}

	@Bean
	public String brokerURL() throws NamingException {
		return (String) ctx.lookup("java:comp/env/jms/ipdsBrokerURL");
	}

	@Bean
	public String queueName() throws NamingException {
		return (String) ctx.lookup("java:comp/env/jms/ipdsQueueName");
	}

	@Bean
	public String costCenterQueueName() throws NamingException {
		return (String) ctx.lookup("java:comp/env/jms/costCenterQueueName");
	}

	@Bean
	public String ipdsEndpoint() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/ipds/endpoint");
	}

	@Bean
	public String crossRefProtocol() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/crossRef/protocol");
	}

	@Bean
	public String crossRefHost() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/crossRef/host");
	}

	@Bean
	public String crossRefUrl() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/crossRef/url");
	}

	@Bean
	public Integer crossRefPort() throws NamingException {
		return (Integer) ctx.lookup("java:comp/env/pubs/crossRef/port");
	}

	@Bean
	public String crossRefUser() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/crossRef/user");
	}
	
	@Bean
	public String crossRefPwd() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/crossRef/pwd");
	}
	
	@Bean
	public String pubsEmailList() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/emailList");
	}

	@Bean
	public String mailHost() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/mailHost");
	}

	@Bean
	public String authService() throws NamingException {
		return (String) ctx.lookup("java:comp/env/cida/authService");
	}

	@Bean
	public Integer lockTimeoutHours() throws NamingException {
		return (Integer) ctx.lookup("java:comp/env/pubs/lockTimeoutHours");
	}

	@Bean
	public String crossRefDepositorEmail() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/crossRef/depositorEmail");
	}

	@Bean
	public String warehouseEndpoint() throws NamingException {
		return (String) ctx.lookup("java:comp/env/pubs/warehouseEndpoint");
	}
	
    @Bean
    public ActiveMQConnectionFactory connectionFactory() throws NamingException {
    	ActiveMQConnectionFactory amqFactory = new ActiveMQConnectionFactory();
    	amqFactory.setBrokerURL(brokerURL());
    	return amqFactory;
    }

//    @Bean
//    public BrokerService broker() throws NamingException, Exception {
//    	//This will prevent the never ending logs from the BrokerService saying there is not enough Temporary Store space...
//    	BrokerService broker = new BrokerService();
//	    broker.addConnector(brokerURL());
//	    broker.setPersistent(false);
//	    SystemUsage systemUsage = broker.getSystemUsage();
//	    systemUsage.getStoreUsage().setLimit(1024 * 1024 * 8);
//	    systemUsage.getTempUsage().setLimit(1024 * 1024 * 8);
//	    broker.start();
//	    return broker;
//    }

}