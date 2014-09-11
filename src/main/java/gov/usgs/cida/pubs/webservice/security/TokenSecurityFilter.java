package gov.usgs.cida.pubs.webservice.security;

import gov.usgs.cida.auth.client.AuthClient;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;

public class TokenSecurityFilter implements Filter  {
	private static final Logger LOG = LoggerFactory.getLogger(TokenSecurityFilter.class);

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String AUTH_BEARER_STRING = "Bearer";
	
	@Autowired
	private AuthClient authClient;
	
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
		
		HttpServletRequest httpReq = (HttpServletRequest) req; //not sure if this cast is safe
		
		if ("OPTIONS".equals(httpReq.getMethod())) {
			setAnonymousRole();
		} else {
			String token = getTokenFromHeader(httpReq);
			
			if(authClient.isValidToken(token)) {
				setAuthorizationRoles(token);
			} else {
				LOG.debug("Invalid token");
				setAnonymousRole();
			}
		}

		filterChain.doFilter(req, resp); //continue down the chain
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
				authHeader.toLowerCase().contains(AUTH_BEARER_STRING.toLowerCase())) {
			token = authHeader;
			token = token.replaceAll(AUTH_BEARER_STRING + "\\s+", "");
			token = token.replaceAll(AUTH_BEARER_STRING.toLowerCase() + "\\s+", "");
			token = token.replaceAll(AUTH_BEARER_STRING.toUpperCase() + "\\s+", "");
		}
		
		return token;
	}
	
	private void setAuthorizationRoles(String token) {
		ArrayList<SimpleGrantedAuthority> auths = new ArrayList<>();
		auths.add(new SimpleGrantedAuthority(PubsAuthentication.ROLE_AUTHENTICATED));
		
		//TODO retrieve authorization roles associated with this token, and add them to lists of authorities
		
        SecurityContextHolder.getContext().setAuthentication(new PubsAuthentication(auths));
	}
	
	private void setAnonymousRole() {
		ArrayList<SimpleGrantedAuthority> auths = new ArrayList<>();
		auths.add(new SimpleGrantedAuthority(PubsAuthentication.ROLE_ANONYMOUS));
        SecurityContextHolder.getContext().setAuthentication(new PubsAuthentication(auths));
	}
}
