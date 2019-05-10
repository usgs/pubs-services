package gov.usgs.cida.pubs.webservice.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import gov.usgs.cida.pubs.TestOAuth;

//TODO Reactivate
@Ignore
public class EndpointSecurityTest extends BaseEndpointSecurityTest {

	RequestPostProcessor requestPostProcessor;

	@Autowired
	private TestOAuth testOAuth;

	@Before
	public void setup() {
		mockSetup();
	}

	@Test
	public void anonymousTest() throws Exception {
		requestPostProcessor = testOAuth.anonymous();
		publicTest(requestPostProcessor, status().isOk());
		authenticatedTest(requestPostProcessor, status().isUnauthorized());
		pubsAuthorizedTestGets(requestPostProcessor, status().isUnauthorized());
		pubsAuthorizedTestDeletes(requestPostProcessor, status().isUnauthorized());
		pubsAuthorizedTestPosts(requestPostProcessor, status().isUnauthorized());
		pubsAuthorizedTestPuts(requestPostProcessor, status().isUnauthorized());
	}

	@Test
	public void authorizedTest() throws Exception {
		requestPostProcessor = testOAuth.bearerToken(TestOAuth.AUTHORIZED_USER);
		publicTest(requestPostProcessor, status().isOk());
		authenticatedTest(requestPostProcessor, status().isOk());
		pubsAuthorizedTestGets(requestPostProcessor, status().isOk());
		pubsAuthorizedTestDeletes(requestPostProcessor, status().isOk());
		pubsAuthorizedTestPosts(requestPostProcessor, status().isCreated());
		pubsAuthorizedTestPuts(requestPostProcessor, status().isOk());
	}

	@Test
	public void authenticatedTest() throws Exception {
		requestPostProcessor = testOAuth.bearerToken(TestOAuth.AUTHENTICATED_USER);
		publicTest(requestPostProcessor, status().isOk());
		authenticatedTest(requestPostProcessor, status().isOk());
		pubsAuthorizedTestGets(requestPostProcessor, status().isForbidden());
		pubsAuthorizedTestDeletes(requestPostProcessor, status().isForbidden());
		pubsAuthorizedTestPosts(requestPostProcessor, status().isForbidden());
		pubsAuthorizedTestPuts(requestPostProcessor, status().isForbidden());
	}

}
