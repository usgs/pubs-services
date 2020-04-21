package gov.usgs.cida.pubs.webservice;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.security.AuthorizationTestServer;
import gov.usgs.cida.pubs.security.BaseSecurityTest;
import gov.usgs.cida.pubs.security.UserDetailTestService;
import gov.usgs.cida.pubs.springinit.CustomUserAuthenticationConverter;
import gov.usgs.cida.pubs.springinit.SecurityConfig;

@SpringBootTest(webEnvironment=WebEnvironment.MOCK,
	classes={SecurityConfig.class, AuthorizationTestServer.class, AuthController.class,
		UserDetailTestService.class, ConfigurationService.class, CustomUserAuthenticationConverter.class})
public class AuthControllerTest extends BaseSecurityTest {

	private RequestPostProcessor requestPostProcessor;

	@MockBean(name="mpPublicationBusService")
	protected IMpPublicationBusService mpPublicationBusService;

	@BeforeEach
	public void setup() {
		requestPostProcessor = bearerToken(UserDetailTestService.AUTHENTICATED_USER);
	}

	@Test
	public void logoutTestSuccess() throws Exception {
		mockMvc.perform(post("/auth/logout").secure(true).with(csrf())
				.with(requestPostProcessor)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		verify(mpPublicationBusService, times(1)).releaseLocksUser(UserDetailTestService.AUTHENTICATED_USER);
	}

}
