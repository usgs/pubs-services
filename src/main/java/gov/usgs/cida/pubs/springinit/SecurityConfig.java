package gov.usgs.cida.pubs.springinit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;

import gov.usgs.cida.pubs.ConfigurationService;

@Profile("!insecure")
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private CustomUserAuthenticationConverter customUserAuthenticationConverter;

	public static final String[] ADMIN_PATHS = new String[] {"/mppublications/*/purge*"};
	public static final String[] AUTHORIZED_PATHS = new String[] {"/**"};
	public static final String[] PUBLIC_PATHS = new String[] {"/lookup/**", "/publication/**",
			"/version", "/about/**", "/swagger", "/swagger-ui/**", "/v3/api-docs/**"};
	public static final String[] SPN_PATHS = new String[] {"/mppublications/*/preview*"};

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
					.antMatchers(PUBLIC_PATHS).permitAll()
					//authenticated
					.antMatchers("/auth/logout").fullyAuthenticated()
					//spn
					.antMatchers(SPN_PATHS).fullyAuthenticated()
					//admin
					.antMatchers(ADMIN_PATHS).hasAnyAuthority(configurationService.getAdminAuthorities())
					//authorized
					.antMatchers(AUTHORIZED_PATHS).hasAnyAuthority(configurationService.getAuthorizedAuthorities())
			.and()
				.anonymous()
				.and().cors()
			.and()
				.csrf().disable()
		;
	}
}