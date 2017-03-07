package gov.usgs.cida.pubs.webservice.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import gov.usgs.cida.auth.client.IAuthClient;
import gov.usgs.cida.auth.model.AuthToken;

@Service
public class AuthenticationService {
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

	protected IAuthClient authClient;
	
	@Autowired
	public AuthenticationService(final IAuthClient authClient) {
		this.authClient = authClient;
	}

	/**
	 * Authenticates and Authorizes the user and returns valid token. If anything fails, {@code null} is returned instead.
	 * Prepares {@link org.springframework.security.core.context.SecurityContext} if authentication/authorization succeeded.
	 * @throws UnauthorizedException when unable to authenticate or authorize
	 */
	AuthToken authenticate(String login, String password) throws UnauthorizedException {
		AuthToken token = authClient.getNewToken(login, password);

		if (null == token || null == token.getTokenId() || token.getTokenId().isEmpty()) {
			throw new UnauthorizedException("Invalid username/password");
		}

		authorizeToken(token.getTokenId());
		
		return token;
	};

	/**
	 * Checks the authentication token and if it is valid prepares
	 * {@link org.springframework.security.core.context.SecurityContext} and returns true.
	 * @throws UnauthorizedException when unable to authenticate or authorize
	 */
	boolean checkToken(String token) throws UnauthorizedException {
		LOG.debug("checkToken: " + token);
		boolean isValid = authClient.isValidToken(token);
		if (isValid) {
			authorizeToken(token);
		}
		return isValid;
	};

	/** 
	 * Pass through to the authClient
	 * @param token
	 * @return
	 */
	boolean invalidateToken(String token) {
		LOG.debug("invalidate: " + token);
		return authClient.invalidateToken(token);
	}

	/** 
	 * Attempts to build a PubsAuthentication complete with the user's roles.
	 * Prepares {@link org.springframework.security.core.context.SecurityContext} if authorization succeeded.
	 * @param token
	 * @throws UnauthorizedException when unable to authorize
	 */
	protected void authorizeToken(String token) throws UnauthorizedException  {
		LOG.debug("authorizeToken: " + token);
		String username = authClient.getToken(token).getUsername();
		LOG.debug("username: " + username);
		List<String> rolesByToken = authClient.getRolesByToken(token);
		for (String role : rolesByToken) {
			LOG.debug("role: " + role);
		}
		PubsAuthentication authentication = new PubsAuthentication(username, rolesByToken);

		if (authentication.isAuthenticated()) {
			authentication.addAuthority(PubsRoles.PUBS_AUTHORIZED);
		}
		authentication.addAuthority(PubsRoles.AD_AUTHENTICATED);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

}
