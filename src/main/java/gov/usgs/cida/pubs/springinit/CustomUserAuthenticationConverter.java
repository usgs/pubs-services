package gov.usgs.cida.pubs.springinit;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

	private static final String KEYCLOAK_USERNAME = "preferred_username";
	private static final String KEYCLOAK_REALM_ACCESS = "realm_access";
	private static final String KEYCLOAK_ROLES = "roles";

	private Collection<? extends GrantedAuthority> defaultAuthorities;

	@Override
	public Authentication extractAuthentication(Map<String, ?> map) {
		if (map.containsKey(KEYCLOAK_USERNAME)) {
			String username = (String) map.get(KEYCLOAK_USERNAME);
			Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
			Object principal = new User(username, "N/A", authorities);
			return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
		}
		return null;
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
		if (!map.containsKey(KEYCLOAK_REALM_ACCESS) || !(map.get(KEYCLOAK_REALM_ACCESS) instanceof Map)) {
			return defaultAuthorities;
		}
		@SuppressWarnings("unchecked")
		Map<String, ?> realmAccess = (Map<String, ?>) map.get(KEYCLOAK_REALM_ACCESS);
		if (!realmAccess.containsKey(KEYCLOAK_ROLES)) {
			return defaultAuthorities;
		}
		Object authorities = realmAccess.get(KEYCLOAK_ROLES);
		if (authorities instanceof String) {
			return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
		}
		if (authorities instanceof Collection) {
			return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
					.collectionToCommaDelimitedString((Collection<?>) authorities));
		}
		throw new IllegalArgumentException("Authorities must be either a String or a Collection");
	}

}
