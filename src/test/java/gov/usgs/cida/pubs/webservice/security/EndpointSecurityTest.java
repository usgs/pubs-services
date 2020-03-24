package gov.usgs.cida.pubs.webservice.security;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import gov.usgs.cida.pubs.security.UserDetailTestService;

public class EndpointSecurityTest extends BaseEndpointSecurityTest {

	private RequestPostProcessor requestPostProcessor;

	@Before
	public void setup() throws Exception {
		mockSetup();
	}

	@Test
	public void anonymousTest() throws Exception {
		requestPostProcessor = anonymous();
		publicTest(requestPostProcessor, status().isOk());
		authenticatedTest(requestPostProcessor, status().isUnauthorized());
		pubsAuthorizedTestGets(requestPostProcessor, status().isUnauthorized());
		pubsAuthorizedTestDeletes(requestPostProcessor, status().isUnauthorized());
		pubsAuthorizedTestPosts(requestPostProcessor, status().isUnauthorized());
		pubsAuthorizedTestPuts(requestPostProcessor, status().isUnauthorized());
		administratorTest(requestPostProcessor, status().isUnauthorized());
	}

	@Test
	public void authorizedTest() throws Exception {
		requestPostProcessor = bearerToken(UserDetailTestService.AUTHORIZED_USER);
		publicTest(requestPostProcessor, status().isOk());
		authenticatedTest(requestPostProcessor, status().isOk());
		pubsAuthorizedTestGets(requestPostProcessor, status().isOk());
		pubsAuthorizedTestDeletes(requestPostProcessor, status().isOk());
		pubsAuthorizedTestPosts(requestPostProcessor, status().isCreated());
		pubsAuthorizedTestPuts(requestPostProcessor, status().isOk());
		administratorTest(requestPostProcessor, status().isForbidden());
	}

	@Test
	public void authenticatedTest() throws Exception {
		requestPostProcessor = bearerToken(UserDetailTestService.AUTHENTICATED_USER);
		publicTest(requestPostProcessor, status().isOk());
		authenticatedTest(requestPostProcessor, status().isOk());
		pubsAuthorizedTestGets(requestPostProcessor, status().isForbidden());
		pubsAuthorizedTestDeletes(requestPostProcessor, status().isForbidden());
		pubsAuthorizedTestPosts(requestPostProcessor, status().isForbidden());
		pubsAuthorizedTestPuts(requestPostProcessor, status().isForbidden());
		administratorTest(requestPostProcessor, status().isForbidden());
	}

	@Test
	public void adminTest() throws Exception {
		requestPostProcessor = bearerToken(UserDetailTestService.ADMIN_USER);
		publicTest(requestPostProcessor, status().isOk());
		authenticatedTest(requestPostProcessor, status().isOk());
		//These are forbidden because admin is not part of authorized in this test environment
		pubsAuthorizedTestGets(requestPostProcessor, status().isForbidden());
		pubsAuthorizedTestDeletes(requestPostProcessor, status().isForbidden());
		pubsAuthorizedTestPosts(requestPostProcessor, status().isForbidden());
		pubsAuthorizedTestPuts(requestPostProcessor, status().isForbidden());
		administratorTest(requestPostProcessor, status().isOk());
	}
}
