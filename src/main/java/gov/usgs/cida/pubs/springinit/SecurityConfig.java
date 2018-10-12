package gov.usgs.cida.pubs.springinit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

import gov.usgs.cida.pubs.ConfigurationService;

@Configuration
public class SecurityConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	ConfigurationService configurationService;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(configurationService.getResourceId());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.authorizeRequests()
					//anonymous (public)
					.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					.antMatchers("/lookup/**", "/publication/**", "/version", "/actuator/info").permitAll()
					.antMatchers("/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
					//authenticated
					.antMatchers("/mppublications/*/preview*", "/auth/logout").fullyAuthenticated()
					//authorized
					.antMatchers("/**").hasAnyAuthority(configurationService.getAuthorizedAuthorities())
			.and()
				.requiresChannel().anyRequest().requiresSecure()
			.and()
				.anonymous()
			.and()
				.csrf()
		;
	}
}