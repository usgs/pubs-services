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

	private final User principal;
	private final Collection<GrantedAuthority> authorities = new ArrayList<>();

	public PubsAuthentication(String username, List<String> roles) {
		for(String role : roles) {
			try {
				PubsRoles pubsRole = PubsRoles.valueOf(role);
				SimpleGrantedAuthority pubsAuthority = new SimpleGrantedAuthority(pubsRole.getSpringRole());
				addAuthority(pubsAuthority);
			} catch (Exception e) {
				LOG.debug(MessageFormat.format("Role {0} ignored.", role), e);
			}
		}
		int authCheckThreshold = isAuthenticated() ? 1 : 0;
		if (getAuthorities().size() > authCheckThreshold) {
			SimpleGrantedAuthority pubsAdminRole = new SimpleGrantedAuthority(PubsRoles.PUBS_AUTHORIZED.getSpringRole());
			addAuthority(pubsAdminRole);
		}
		principal = new User(username, "******", getAuthorities());
	}

	private void addAuthority(SimpleGrantedAuthority pubsAuthority) {
		if (!authorities.contains(pubsAuthority)) {
			authorities.add(pubsAuthority);
			LOG.debug(MessageFormat.format("Role {0} added.", pubsAuthority.getAuthority()));
		}
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
		return authorities.contains(new SimpleGrantedAuthority(PubsRoles.AD_AUTHENTICATED.getSpringRole()));
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated)
			throws IllegalArgumentException {
		throw new RuntimeException("PubsAuthentication calculates authenticated status based on roles");
	}
}
