package gov.usgs.cida.pubs.webservice.security;

import gov.usgs.cida.auth.client.IAuthClient;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenSecurityFilter implements Filter  {
	private static final Logger LOG = LoggerFactory.getLogger(TokenSecurityFilter.class);

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String AUTH_BEARER_STRING = "Bearer";
	
	@Autowired
	protected IAuthClient authClient;
	
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
		
		HttpServletRequest httpReq = (HttpServletRequest) req; 
		
		if ("OPTIONS".equals(httpReq.getMethod())) {
			filterChain.doFilter(req, resp); 
		} else {
			String token = getTokenFromHeader(httpReq);
			
			if(token != null && authClient.isValidToken(token)) {
				setContextAuthorization(token);
				
				if(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
					filterChain.doFilter(req, resp); 
				} else {
					LOG.debug("User is not authorized to use Publications Warehouse");
					((HttpServletResponse) resp).setStatus(401);
				}
			} else {
				LOG.debug("Invalid token or no token provided");
				if(SecurityContextHolder.getContext().getAuthentication() != null) {
					LOG.debug("Anonymous role set for this request, proceeding down filter chain");
					filterChain.doFilter(req, resp); 
				} else {
					LOG.debug("This end point not authenticated anonymously");
					((HttpServletResponse) resp).setStatus(401);
				}
			}
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
	
	private void setContextAuthorization(String token) {
        SecurityContextHolder.getContext().setAuthentication(
    		new PubsAuthentication(authClient.getToken(token).getUsername(), authClient.getRolesByToken(token))
        );
	}
}
