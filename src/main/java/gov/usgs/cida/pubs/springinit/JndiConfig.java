package gov.usgs.cida.pubs.springinit;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class JndiConfig {
	
	private final Context ctx;
	
	public JndiConfig() throws NamingException {
		ctx = new InitialContext();
	}
	
	@Bean
	public DataSource dataSource() throws Exception {
		return (DataSource) ctx.lookup("java:comp/env/jdbc/mypubsDS");
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
	public String displayProtocol() {
		String displayProtocol = "https";
		try {
			String tmp = (String) ctx.lookup("java:comp/env/pubs/displayProtocol");
			if (null != tmp) {
				displayProtocol = tmp;
			}
		} catch (Exception e) {
			LOG.info("Using default displayProtocol");
		}
		return displayProtocol;
	}

	@Bean
	public String displayHost() throws NamingException {
		String displayHost = "localhost:8443";
		try {
			String tmp = (String) ctx.lookup("java:comp/env/pubs/displayHost");
			if (null != tmp) {
				displayHost = tmp;
			}
		} catch (Exception e) {
			LOG.info("Using default displayHost");
		}
		return displayHost;
	}

	@Bean
	public String displayPath() throws NamingException {
		String displayPath = "/pubs-services";
		try {
			String tmp = (String) ctx.lookup("java:comp/env/pubs/displayPath");
			if (null != tmp) {
				displayPath = tmp;
			}
		} catch (Exception e) {
			LOG.info("Using default displayPath");
		}
		return displayPath;
	}

}