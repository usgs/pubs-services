package gov.usgs.cida.pubs.webservice.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class TokenSecurityFilter extends GenericFilterBean {
	private static final Logger LOG = LoggerFactory.getLogger(TokenSecurityFilter.class);

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String AUTH_BEARER_STRING = "Bearer";
	
	protected final AuthenticationService authenticationService;

	@Autowired
	public TokenSecurityFilter(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	/**
	 * Filter checks for a valid authentication token in the "Authorization" HTTP header. The token
	 * should be of format "Bearer the-auth-token-string" which follows a pattern set by OAUTH2.
	 * 
	 * @param req
	 * @param resp
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain filterChain) throws IOException, ServletException {
		
		HttpServletRequest httpReq = (HttpServletRequest) req; 
		
		String token = getTokenFromHeader(httpReq);
		
		if (null != token) {
			try {
				authenticationService.checkToken(token);
			} catch (UnauthorizedException e) {
				//We do not need to do anything here - the user will not be authenticated here and the remaining filters 
				//will apply - be they anonymous or a rejection (http status 401)
				LOG.debug(e.getMessage());
			}
		}
				
		filterChain.doFilter(req, resp); 
	}

	/**
	 * Pulls token in the "Authorization" HTTP header. The token
	 * should be of format "Bearer the-auth-token-string" which follows a pattern set by OAUTH2.
	 * 
	 * @param httpRequest
	 * @return
	 */
	public static String getTokenFromHeader(HttpServletRequest httpRequest) {
		String token = null;
		
		String authHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
		if(authHeader != null &&
				authHeader.toLowerCase().startsWith(AUTH_BEARER_STRING.toLowerCase() + " ")) {
			token = authHeader;
			token = token.replaceAll(AUTH_BEARER_STRING + "\\s+", "");
			token = token.replaceAll(AUTH_BEARER_STRING.toLowerCase() + "\\s+", "");
			token = token.replaceAll(AUTH_BEARER_STRING.toUpperCase() + "\\s+", "");
		}
		
		return token;
	}

}
