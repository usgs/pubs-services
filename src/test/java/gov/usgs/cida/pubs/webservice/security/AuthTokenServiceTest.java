package gov.usgs.cida.pubs.webservice.security;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import gov.usgs.cida.auth.client.IAuthClient;
import gov.usgs.cida.auth.model.AuthToken;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AuthTokenServiceTest {
	AuthTokenService testService;
	IAuthClient mockAuthClient;
	IMpPublicationBusService mockBusService;
	
	@Before
	public void setup() {
		mockAuthClient = mock(IAuthClient.class);
		mockBusService = mock(IMpPublicationBusService.class);
		testService = new AuthTokenService(mockAuthClient, mockBusService);
	}
	
	@Test
	public void testGetToken_invalid_user_pass() {
		//authClient returns no token if user/pass is bad
		when(mockAuthClient.getNewToken("username", "password")).thenReturn(null);
		
		try {
			testService.getToken("username", "password");
			assertTrue("Should never get here", false);
		} catch (UnauthorizedException e) {
			assertEquals("Correct exception is thrown", "Invalid username/password", e.getMessage());
		}
	}
	
	@Test
	public void testGetToken_no_pubs_roles() {
		//authClient returns no token if user/pass is bad
		AuthToken testToken = new AuthToken();
		testToken.setTokenId("a-token-string");
		when(mockAuthClient.getNewToken("username", "password")).thenReturn(testToken);
		when(mockAuthClient.getRolesByToken("a-token-string")).thenReturn(new ArrayList<String>());
		
		try {
			testService.getToken("username", "password");
			assertTrue("Should never get here", false);
		} catch (UnauthorizedException e) {
			assertEquals("Correct exception is thrown", "User is not authorized to use the Publications Warehouse", e.getMessage());
		}
	}
	
	@Test
	public void testGetToken_valid_pubs_user() {
		//authClient returns no token if user/pass is bad
		AuthToken testToken = new AuthToken();
		testToken.setTokenId("a-token-string");
		when(mockAuthClient.getNewToken("username", "password")).thenReturn(testToken);
		ArrayList<String> pubsRoles = new ArrayList<>();
		pubsRoles.add(PubsRoles.PUBS_ADMIN.name());
		when(mockAuthClient.getRolesByToken("a-token-string")).thenReturn(pubsRoles);
		
		try {
			ObjectNode response = testService.getToken("username", "password");
			assertEquals("Correct token json node returned", "a-token-string", response.get("token").textValue());
		} catch (UnauthorizedException e) {
			assertTrue("Should never get here", false);
		}
	}
	
	@Test 
	public void testLogout() {
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer a-token-string");
		
		AuthToken testToken = mock(AuthToken.class);
		when(testToken.getUsername()).thenReturn("username");
		
		when(mockAuthClient.invalidateToken("a-token-string")).thenReturn(true);
		when(mockAuthClient.getToken("a-token-string")).thenReturn(testToken);

		ObjectNode response = testService.logout(mockRequest);
		assertEquals("Valid logout response", "success", response.get("status").textValue());
		verify(mockAuthClient, times(1)).invalidateToken("a-token-string");
		verify(mockBusService, times(1)).releaseLocksUser("username");
	}
}
