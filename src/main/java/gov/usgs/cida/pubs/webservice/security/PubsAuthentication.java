package gov.usgs.cida.pubs.webservice.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class PubsAuthentication implements Authentication {
	private static final long serialVersionUID = 1L;

	public static final String ROLE_PREFIX = "ROLE_";
	public static final String ROLE_ANONYMOUS = ROLE_PREFIX + PubsRoles.ANONYMOUS.name();
	public static final String ROLE_AUTHENTICATED = ROLE_PREFIX + PubsRoles.AUTHENTICATED.name();
	public static final String ROLE_PUBS_ADMIN = ROLE_PREFIX + PubsRoles.PUBS_ADMIN.name();
	public static final String ROLE_PUBS_TAGGING_USER = ROLE_PREFIX + PubsRoles.PUBS_TAGGING_USER.name();
	public static final String ROLE_PUBS_SPN_USER = ROLE_PREFIX + PubsRoles.PUBS_SPN_USER.name();
	public static final String ROLE_PUBS_CATALOGER_USER = ROLE_PREFIX + PubsRoles.PUBS_CATALOGER_USER.name();
	public static final String ROLE_PUBS_SPN_SUPERVISOR = ROLE_PREFIX + PubsRoles.PUBS_SPN_SUPERVISOR.name();
	public static final String ROLE_PUBS_CATALOGER_SUPERVISOR = ROLE_PREFIX + PubsRoles.PUBS_CATALOGER_SUPERVISOR.name();
	
	private Collection<? extends GrantedAuthority> authorities;
	
	public PubsAuthentication(Collection<? extends GrantedAuthority> inAuthorities) {
		authorities = inAuthorities;
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated)
			throws IllegalArgumentException {
		throw new RuntimeException("PubsAuthentication is designed to always be true");
	}

}
