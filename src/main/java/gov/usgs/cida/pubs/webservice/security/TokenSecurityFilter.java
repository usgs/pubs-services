package gov.usgs.cida.pubs.webservice.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;

public class TokenSecurityFilter implements Filter  {
	private static final Logger LOG = LoggerFactory.getLogger(TokenSecurityFilter.class);

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String AUTH_BEARER_STRING = "Bearer";

	@Override
	public void destroy() {
		//clean up?
	}

	@Override
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
		String token = getTokenFromHeader((HttpServletRequest) req); //not sure if this cast is safe
		
		if(isValidToken(token)) {
			//TODO set security context with user roles?
			
			filterChain.doFilter(req, resp); //continue down the chain
		} else {
			LOG.debug("Invalid token");
			((HttpServletResponse) resp).setStatus(401);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		//TODO maybe check availablility of token servers?
	}

	/**
	 * Pulls token in the "Authorization" HTTP header. The token
	 * should be of format "Bearer the-auth-token-string" which follows a pattern set by OAUTH2.
	 * 
	 * @param httpRequest
	 * @return
	 */
	private String getTokenFromHeader(HttpServletRequest httpRequest) {
		String token = null;
		
		String authHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
		if(authHeader != null &&
				authHeader.toLowerCase().contains(AUTH_BEARER_STRING.toLowerCase())) {
			token = authHeader;
			token = token.replaceAll(AUTH_BEARER_STRING + "\\s+", "");
			token = token.replaceAll(AUTH_BEARER_STRING.toLowerCase() + "\\s+", "");
			token = token.replaceAll(AUTH_BEARER_STRING.toUpperCase() + "\\s+", "");
		}
		
		return token;
	}
	
	
	private boolean isValidToken(String token) {
		if(token == null) {
			return false;
		}
		
		//TODO verify this token against a cache which has tokens from a CIDA Auth Service
		return token.equals("a-random-token");
	}
}
