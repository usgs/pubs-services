package gov.usgs.cida.pubs.webservice.security;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class PubsAuthentication implements Authentication {
	private static final Logger LOG = LoggerFactory.getLogger(PubsAuthentication.class);
	private static final long serialVersionUID = 1L;

	public static final String ROLE_PREFIX = "ROLE_";
	public static final String ROLE_PUBS_ADMIN = ROLE_PREFIX + PubsRoles.PUBS_ADMIN.name();
	public static final String ROLE_PUBS_TAGGING_USER = ROLE_PREFIX + PubsRoles.PUBS_TAGGING_USER.name();
	public static final String ROLE_PUBS_SPN_USER = ROLE_PREFIX + PubsRoles.PUBS_SPN_USER.name();
	public static final String ROLE_PUBS_CATALOGER_USER = ROLE_PREFIX + PubsRoles.PUBS_CATALOGER_USER.name();
	public static final String ROLE_PUBS_SPN_SUPERVISOR = ROLE_PREFIX + PubsRoles.PUBS_SPN_SUPERVISOR.name();
	public static final String ROLE_PUBS_CATALOGER_SUPERVISOR = ROLE_PREFIX + PubsRoles.PUBS_CATALOGER_SUPERVISOR.name();
	
	private Collection<? extends GrantedAuthority> authorities;
	private User principal;
	
	public PubsAuthentication(Collection<? extends GrantedAuthority> inAuthorities) {
		authorities = inAuthorities;
	}
	
	public PubsAuthentication(String username, Collection<? extends GrantedAuthority> inAuthorities) {
		authorities = inAuthorities;
		principal = new User(username, "******", authorities);
	}
	
	public PubsAuthentication(String username, List<String> rawRoles) {
		ArrayList<SimpleGrantedAuthority> auths = new ArrayList<>();
		
		for(String role : rawRoles) {
			try {
				PubsRoles.valueOf(role); //role validation
				auths.add(new SimpleGrantedAuthority(PubsAuthentication.ROLE_PREFIX + role));
			} catch (Exception e) {
				LOG.debug(MessageFormat.format("Role {0} ignored.", role), e);
			}
		}
		authorities = auths;
		principal = new User(username, "******", authorities);
	}
	
	@Override
	public String getName() {
		return principal.getUsername();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return principal;
	}

	@Override
	public Object getDetails() {
		return principal;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public boolean isAuthenticated() {
		return authorities.size() > 0;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated)
			throws IllegalArgumentException {
		throw new RuntimeException("PubsAuthentication calculates authenticated status based on roles");
	}

}
