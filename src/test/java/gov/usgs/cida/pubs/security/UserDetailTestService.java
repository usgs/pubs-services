package gov.usgs.cida.pubs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import gov.usgs.cida.pubs.ConfigurationService;

@TestConfiguration
public class UserDetailTestService {
	@Autowired
	private ConfigurationService configurationService;

	public static final String ADMIN_USER = "admin";
	public static final String ANONOMOUS_USER = "anonymous";
	public static final String AUTHENTICATED_USER = "authenticated";
	public static final String AUTHORIZED_USER = "authorized";
	public static final String SPN_USER = "spn";

	@Bean
	public UserDetailsService userDetailsService() {
		//This deprecated method is safe to use here. See the method's javadoc for full explanation.
		User.UserBuilder users = User.withDefaultPasswordEncoder();
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(users.username("user").password("password").roles("USER").build());
		manager.createUser(users.username(ANONOMOUS_USER).password("password").build());
		manager.createUser(users.username(AUTHENTICATED_USER).password("password").build());
		manager.createUser(users.username(AUTHORIZED_USER).password("password").authorities(configurationService.getAuthorizedAuthorities()).build());
		manager.createUser(users.username(SPN_USER).password("password").authorities(configurationService.getSpnAuthorities()).build());
		manager.createUser(users.username(ADMIN_USER).password("password").authorities(configurationService.getAdminAuthorities()).build());
		return manager;
	}
}
