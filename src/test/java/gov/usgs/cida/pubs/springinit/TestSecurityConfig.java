package gov.usgs.cida.pubs.springinit;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import gov.usgs.cida.pubs.webservice.security.AuthenticationService;

@Configuration
@Import(SecurityConfig.class)
public class TestSecurityConfig {

	@Bean
	public String authService() {
		return "localhost";
	}

	@Bean
	public AuthenticationService authenticationService() {
		return Mockito.mock(AuthenticationService.class);
	}

}
