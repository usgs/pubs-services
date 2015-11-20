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

	private Collection<GrantedAuthority> authorities;
	private User principal;
	
	public PubsAuthentication(String username, List<String> rawRoles) {
		Collection<GrantedAuthority> auths = new ArrayList<>();
		
		for(String role : rawRoles) {
			try {
				//role validation - we only add roles here that are valid for pubs.
				PubsRoles pubsRole = PubsRoles.valueOf(role);
				auths.add(new SimpleGrantedAuthority(pubsRole.getSpringRole()));
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
		return !authorities.isEmpty();
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated)
			throws IllegalArgumentException {
		throw new RuntimeException("PubsAuthentication calculates authenticated status based on roles");
	}

	public void addAuthority(PubsRoles role) {
		authorities.add(new SimpleGrantedAuthority(role.getSpringRole()));
	}

}
