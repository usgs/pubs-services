package gov.usgs.cida.pubs.webservice.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;
import gov.usgs.cida.auth.model.AuthToken;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class AuthTokenServiceTest {
	AuthTokenService testService;
	AuthenticationService mockAuthService;
	IMpPublicationBusService mockBusService;
    private MockMvc mockMvc;

	
	@Before
	public void setup() {
		mockAuthService = mock(AuthenticationService.class);
		mockBusService = mock(IMpPublicationBusService.class);
		testService = new AuthTokenService(mockAuthService, mockBusService);
	}
	
	@Test 
	public void logoutTestSuccess() {
		SecurityContextHolder.clearContext();
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer a-token-string");
		
		when(mockAuthService.invalidateToken("a-token-string")).thenReturn(true);

		ObjectNode response = testService.logout(mockRequest);
		assertEquals("Valid logout response", "success", response.get("status").textValue());
		verify(mockAuthService, times(1)).invalidateToken("a-token-string");
		verify(mockBusService, times(1)).releaseLocksUser(PubsConstants.ANONYMOUS_USER);
	}

	@Test 
	public void logoutTestFail() {
		SecurityContextHolder.clearContext();
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer a-token-string");
		
		when(mockAuthService.invalidateToken("a-token-string")).thenReturn(false);

		ObjectNode response = testService.logout(mockRequest);
		assertEquals("Invalid logout response", "failed", response.get("status").textValue());
		verify(mockAuthService, times(1)).invalidateToken("a-token-string");
		verify(mockBusService, times(1)).releaseLocksUser(PubsConstants.ANONYMOUS_USER);
	}
	
	@Test
	public void logoutTest() throws Exception {
		SecurityContextHolder.clearContext();
    	mockMvc = MockMvcBuilders.standaloneSetup(testService).build();
		when(mockAuthService.invalidateToken("a-token-string")).thenReturn(true);

        MvcResult rtn = mockMvc.perform(post("/auth/logout").header(TokenSecurityFilter.AUTHORIZATION_HEADER, "Bearer a-token-string")
        		.accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(status().isOk())
        	.andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        	.andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
                sameJSONObjectAs(new JSONObject("{\"status\":\"success\"}")));
		verify(mockAuthService, times(1)).invalidateToken("a-token-string");
		verify(mockBusService, times(1)).releaseLocksUser(PubsConstants.ANONYMOUS_USER);
	}

	@Test
	public void getTokenTest() throws Exception {
		Timestamp ts = new Timestamp(0);
    	mockMvc = MockMvcBuilders.standaloneSetup(testService).build();
		AuthToken testToken = new AuthToken();
		testToken.setTokenId("a-token-string");
		testToken.setExpires(ts);
		when(mockAuthService.authenticate("user", "pwd")).thenReturn(testToken);

        MvcResult rtn = mockMvc.perform(post("/auth/token").param("username", "user").param("password", "pwd")
        		.accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(status().isOk())
        	.andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        	.andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject("{\"token\":\"a-token-string\",\"expires\":\"" + ts.toString() + "\"}")));
        verify(mockAuthService, times(1)).authenticate("user", "pwd");
	}
	
	@Test
	public void getTokenFailTest() throws Exception {
    	mockMvc = MockMvcBuilders.standaloneSetup(testService).build();
		when(mockAuthService.authenticate("user", "pwd")).thenThrow(new UnauthorizedException("Invalid username/password"));

        MvcResult rtn = mockMvc.perform(post("/auth/token").param("username", "user").param("password", "pwd")
        		.accept(MediaType.parseMediaType(PubsConstants.MIME_TYPE_APPLICATION_JSON)))
        	.andExpect(status().isUnauthorized())
        	.andExpect(content().contentType(PubsConstants.MIME_TYPE_APPLICATION_JSON))
        	.andReturn();
        assertThat(new JSONObject(rtn.getResponse().getContentAsString()),
        		sameJSONObjectAs(new JSONObject("{\"reason\":\"Invalid username/password\"}")));
        verify(mockAuthService, times(1)).authenticate("user", "pwd");
	}
}
