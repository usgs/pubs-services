package gov.usgs.cida.pubs.webservice;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.TestOAuth;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.springinit.SecurityConfig;
import gov.usgs.cida.pubs.webservice.AuthController;

@RunWith(SpringRunner.class)
@WebMvcTest({SecurityConfig.class, TestOAuth.class, AuthController.class})
public class AuthControllerTest {

	RequestPostProcessor requestPostProcessor;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private TestOAuth testOAuth;

	@MockBean
	ConfigurationService configurationService;

	@MockBean(name="mpPublicationBusService")
	protected IMpPublicationBusService mpPublicationBusService;

	@Before
	public void setup() {
		requestPostProcessor = testOAuth.bearerToken(TestOAuth.AUTHENTICATED_USER);
	}

	@Test
	public void logoutTestSuccess() throws Exception {
		mvc.perform(post("/auth/logout").secure(true).with(csrf())
				.with(requestPostProcessor)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		verify(mpPublicationBusService, times(1)).releaseLocksUser(TestOAuth.AUTHENTICATED_USER);
	}

}
