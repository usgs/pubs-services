package gov.usgs.cida.pubs.springinit;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Profile("insecure")
@Configuration
public class InsecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.authorizeRequests()
					.antMatchers("/**").permitAll()
			.and()
				.anonymous()
				.and().cors()
			.and()
				.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		;
	}
}
