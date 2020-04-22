package gov.usgs.cida.pubs.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.oauth2.common.util.JacksonJsonParser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import gov.usgs.cida.pubs.BaseTest;

@EnableWebMvc
@AutoConfigureMockMvc
public abstract class BaseSecurityTest extends BaseTest {

	@Autowired
	protected MockMvc mockMvc;

	private String obtainAccessToken(String username, String password) {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("username", username);
		params.add("password", password);
		params.add("scope", "openid");

		String base64ClientCredentials = new String(Base64.getEncoder().encode("user:password".getBytes()));

		String resultString = "{}";
		try {
			ResultActions result = mockMvc.perform(post("/oauth/token")
				.params(params)
				.header("Authorization","Basic " + base64ClientCredentials)
				.accept("application/json;charset=UTF-8"))
			.andExpect(status().isOk());
			resultString = result.andReturn().getResponse().getContentAsString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}

	public RequestPostProcessor anonymous() {
		return mockRequest -> {
			return mockRequest;
		};
	}

	public RequestPostProcessor bearerToken(final String username) {
		return mockRequest -> {
			mockRequest.addHeader("Authorization", "Bearer " + obtainAccessToken(username, "password"));
			return mockRequest;
		};
	}
}
