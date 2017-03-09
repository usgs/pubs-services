package gov.usgs.cida.pubs.webservice.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class EndpointSecurityAdTest extends EndpointSecurityAuthTest {

	@Before
	public void setup() {
		super.setup();
	}

	@Test
	public void adAuthenticatedTest() throws Exception {
		mockSetup();
		ArrayList<String> authRoles = new ArrayList<String>();
		authRoles.add(PubsRoles.AD_AUTHENTICATED.name());
		when(mockAuthClient.getRolesByToken("a-token-string")).thenReturn(authRoles);

		publicTest(httpHeaders, status().isOk());
		authenticatedTest(httpHeaders, status().isOk());
		pubsAuthorizedTestGetsDeletes(httpHeaders, status().isForbidden(), false);
		pubsAuthorizedTestPosts(httpHeaders, status().isForbidden(), false);
		pubsAuthorizedTestPuts(httpHeaders, status().isForbidden(), false);
		adAuthenticatedOrPubsAuthorizedTest(httpHeaders, status().isOk());
	}

}
