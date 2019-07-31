package gov.usgs.cida.pubs.springinit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import gov.usgs.cida.pubs.ConfigurationService;

@Configuration
@EnableResourceServer
public class SecurityConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private CustomUserAuthenticationConverter customUserAuthenticationConverter;

	@Bean
	public TokenStore jwkTokenStore() {
		DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
		tokenConverter.setUserTokenConverter(customUserAuthenticationConverter);
		return new JwkTokenStore(configurationService.getKeySetUri(), tokenConverter);
	}

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
					.antMatchers("/lookup/**", "/publication/**", "/version", "/about/**").permitAll()
					.antMatchers("/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs", "/v3/api-docs").permitAll()
					//authenticated
					.antMatchers("/mppublications/*/preview*", "/auth/logout").fullyAuthenticated()
					//authorized
					.antMatchers("/**").hasAnyAuthority(configurationService.getAuthorizedAuthorities())
//			.and()
//				.requiresChannel().anyRequest().requiresSecure()
			.and()
				.anonymous()
			.and()
				.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		;
	}
}