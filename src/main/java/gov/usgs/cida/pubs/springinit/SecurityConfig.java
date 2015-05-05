package gov.usgs.cida.pubs.springinit;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@ImportResource("classpath:spring/securityContext.xml")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

}
