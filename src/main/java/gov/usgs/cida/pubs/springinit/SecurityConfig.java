package gov.usgs.cida.pubs.springinit;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@ImportResource("classpath:spring/securityContext.xml")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public String authService() throws NamingException {
		Context ctx = new InitialContext();
		return (String) ctx.lookup("java:comp/env/cida/authService");
	}

}
