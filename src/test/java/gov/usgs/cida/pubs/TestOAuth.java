package gov.usgs.cida.pubs;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@Component
@EnableAuthorizationServer
public class TestOAuth extends AuthorizationServerConfigurerAdapter {

	public static final String AUTHENTICATED_USER = "authenticated";
	public static final String AUTHORIZED_USER = "authorized";
	public static final String SPN_USER = "spn";

	public static final String SPN_AUTHORITY = "SPN_AUTHORITY";
	public static final String AUTHORIZED_AUTHORITY = "PUBS_AUTHORITY";

	@Autowired
	AuthorizationServerTokenServices tokenservice;

	@Autowired
	ClientDetailsService clientDetailsService;

	@Autowired
	ConfigurationService configurationService;

	public RequestPostProcessor anonymous() {
		return mockRequest -> {
			return mockRequest;
		};
	}

	public RequestPostProcessor bearerToken(final String username) {
		return mockRequest -> {
			OAuth2AccessToken token = createAccessToken(username);
			mockRequest.addHeader("Authorization", "Bearer " + token.getValue());
			return mockRequest;
		};
	}

	OAuth2AccessToken createAccessToken(final String username) {
		ClientDetails client = clientDetailsService.loadClientByClientId(username);
		Collection<GrantedAuthority> authorities = client.getAuthorities();
		Set<String> resourceIds = client.getResourceIds();
		Set<String> scopes = client.getScope();

		Map<String, String> requestParameters = Collections.emptyMap();
		boolean approved = true;
		String redirectUrl = null;
		Set<String> responseTypes = Collections.emptySet();
		Map<String, Serializable> extensionProperties = Collections.emptyMap();

		OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, username, authorities,
				approved, scopes, resourceIds, redirectUrl, responseTypes, extensionProperties);

		User userPrincipal = new User(username, "", true, true, true, true, authorities);
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
		OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, authenticationToken);

		return tokenservice.createAccessToken(auth);
	}

	@Override
	public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
			.withClient(AUTHENTICATED_USER)
		.and()
			.withClient(AUTHORIZED_USER)
			.authorities(AUTHORIZED_AUTHORITY, SPN_AUTHORITY)
		.and()
			.withClient(SPN_USER)
			.authorities(SPN_AUTHORITY);
	}

}
