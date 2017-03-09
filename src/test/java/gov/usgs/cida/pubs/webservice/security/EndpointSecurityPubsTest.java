package gov.usgs.cida.pubs.webservice.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class EndpointSecurityPubsTest extends EndpointSecurityAuthTest {

	@Before
	public void setup() {
		super.setup();
	}

	@Test
	public void adAuthenticatedTest() throws Exception {
		mockSetup();
		ArrayList<String> authRoles = new ArrayList<String>();
		authRoles.add(PubsRoles.AD_AUTHENTICATED.name());
		authRoles.add(PubsRoles.PUBS_ADMIN.name());
		when(mockAuthClient.getRolesByToken("a-token-string")).thenReturn(authRoles);

		publicTest(httpHeaders, status().isOk());
		authenticatedTest(httpHeaders, status().isOk());
		pubsAuthorizedTestGetsDeletes(httpHeaders, status().isOk(), true);
		pubsAuthorizedTestPosts(httpHeaders, status().isCreated(), true);
		pubsAuthorizedTestPuts(httpHeaders, status().isOk(), true);
		adAuthenticatedOrPubsAuthorizedTest(httpHeaders, status().isOk());
	}

}
