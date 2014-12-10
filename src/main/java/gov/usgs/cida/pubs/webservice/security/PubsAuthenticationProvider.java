package gov.usgs.cida.pubs.webservice.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/** 
 * Dummy class to stub out the Spring Security AuthenticationProvider.
 * @author drsteini
 *
 */
public class PubsAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return false;
	}

}
