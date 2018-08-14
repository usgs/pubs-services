package gov.usgs.cida.pubs.springinit;

import java.net.URISyntaxException;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gov.usgs.cida.auth.client.CachingAuthClient;
import gov.usgs.cida.auth.client.IAuthClient;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.webservice.security.AuthenticationService;
import gov.usgs.cida.pubs.webservice.security.PubsAuthenticationProvider;
import gov.usgs.cida.pubs.webservice.security.TokenSecurityFilter;
import gov.usgs.cida.pubs.webservice.security.UnauthorizedEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private PubsAuthenticationProvider pubsAuthenticationProvider;

	@Autowired
	private UnauthorizedEntryPoint unauthorizedEntryPoint;

	@Autowired
	ConfigurationService configurationService;

	@Bean
	public IAuthClient authClient() throws URISyntaxException, NamingException {
		return new CachingAuthClient(configurationService.getAuthServiceUrl());
	}

	@Bean
	public AuthenticationService authenticationService() throws URISyntaxException, NamingException {
		return new AuthenticationService(authClient());
	}

	@Bean TokenSecurityFilter tokenSecurityFilter() throws URISyntaxException, NamingException {
		return new TokenSecurityFilter(authenticationService());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.addFilterBefore(tokenSecurityFilter(), UsernamePasswordAuthenticationFilter.class)
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.authorizeRequests()
					.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					.antMatchers("/auth/logout").authenticated()
					.antMatchers("/mppublications/*/preview*").hasAnyRole("AD_AUTHENTICATED", "PUBS_AUTHORIZED")
					.antMatchers("/auth/token", "/lookup/**", "/publication/**", "/version").permitAll()
					.antMatchers("/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
					.antMatchers("/actuator/info", "/actuator/health").permitAll()
					.antMatchers("/**").hasRole("PUBS_AUTHORIZED")
			.and()
				.requiresChannel().anyRequest().requiresSecure()
			.and()
				.anonymous()
			.and()
				.httpBasic().authenticationEntryPoint(unauthorizedEntryPoint);
	}	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(pubsAuthenticationProvider);
	}

}
