package gov.usgs.cida.pubs.webservice.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.usgs.cida.auth.client.IAuthClient;
import gov.usgs.cida.auth.model.AuthToken;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationServiceTest {
	AuthenticationService testService;
	IAuthClient mockAuthClient;
	IMpPublicationBusService mockBusService;
	
	@Before
	public void setup() {
		SecurityContextHolder.clearContext();
		mockAuthClient = mock(IAuthClient.class);
		testService = new AuthenticationService(mockAuthClient);
	}
	
	@Test
	public void happyAuthTests() throws UnauthorizedException {
		AuthToken testToken = new AuthToken();
		testToken.setTokenId("a-token-string");
		testToken.setUsername("testyUser");
		ArrayList<String> pubsRoles = new ArrayList<>();
		pubsRoles.add(PubsRoles.PUBS_SPN_USER.name());
		when(mockAuthClient.getNewToken("username", "password")).thenReturn(testToken);
		when(mockAuthClient.getToken("a-token-string")).thenReturn(testToken);
		when(mockAuthClient.getRolesByToken("a-token-string")).thenReturn(pubsRoles);
		when(mockAuthClient.isValidToken("a-token-string")).thenReturn(true);
		
		testService.authorizeToken(testToken.getTokenId());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		assertEquals("testyUser", auth.getName());
		assertFalse(auth.getAuthorities().isEmpty());
		assertEquals(3, auth.getAuthorities().size());
		assertEquals(PubsAuthentication.ROLE_PUBS_SPN_USER, auth.getAuthorities().toArray()[0].toString());
		assertEquals(PubsRoles.PUBS_AUTHORIZED.name(), auth.getAuthorities().toArray()[1].toString());
		assertEquals(PubsRoles.AD_AUTHENTICATED.name(), auth.getAuthorities().toArray()[2].toString());
		
		AuthToken response = testService.authenticate("username", "password");
		assertEquals(testToken, response);

		assertTrue(testService.checkToken("a-token-string"));
	}
	
	@Test
	public void noPubsRolesAuthTests() throws UnauthorizedException {
		AuthToken testToken = new AuthToken();
		testToken.setTokenId("a-token-string");
		testToken.setUsername("testyUser");
		when(mockAuthClient.getNewToken("username", "password")).thenReturn(testToken);
		when(mockAuthClient.getToken("a-token-string")).thenReturn(testToken);
		when(mockAuthClient.getRolesByToken("a-token-string")).thenReturn(new ArrayList<String>());
		when(mockAuthClient.isValidToken("a-token-string")).thenReturn(false);
		when(mockAuthClient.isValidToken("b-token-string")).thenReturn(true);
		when(mockAuthClient.getToken("b-token-string")).thenReturn(testToken);
		when(mockAuthClient.getRolesByToken("b-token-string")).thenReturn(new ArrayList<String>());
		
		testService.authorizeToken(testToken.getTokenId());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		assertEquals("testyUser", auth.getName());
		assertFalse(auth.getAuthorities().isEmpty());
		assertEquals(1, auth.getAuthorities().size());
		assertEquals(PubsRoles.AD_AUTHENTICATED.name(), auth.getAuthorities().toArray()[0].toString());
		
		testService.authenticate("username", "password");
		auth = SecurityContextHolder.getContext().getAuthentication();
		assertEquals("testyUser", auth.getName());
		assertFalse(auth.getAuthorities().isEmpty());
		assertEquals(1, auth.getAuthorities().size());
		assertEquals(PubsRoles.AD_AUTHENTICATED.name(), auth.getAuthorities().toArray()[0].toString());
		
		testService.checkToken("a-token-string");
		auth = SecurityContextHolder.getContext().getAuthentication();
		assertEquals("testyUser", auth.getName());
		assertFalse(auth.getAuthorities().isEmpty());
		assertEquals(1, auth.getAuthorities().size());
		assertEquals(PubsRoles.AD_AUTHENTICATED.name(), auth.getAuthorities().toArray()[0].toString());

		testService.checkToken("b-token-string");
		auth = SecurityContextHolder.getContext().getAuthentication();
		assertEquals("testyUser", auth.getName());
		assertFalse(auth.getAuthorities().isEmpty());
		assertEquals(1, auth.getAuthorities().size());
		assertEquals(PubsRoles.AD_AUTHENTICATED.name(), auth.getAuthorities().toArray()[0].toString());
	}
	
	@Test
	public void invalidUserPassTests() {
		//authClient returns no token if user/pass is bad
		AuthToken testToken = new AuthToken();
		testToken.setTokenId("");
		when(mockAuthClient.getNewToken("username", "password")).thenReturn(null);
		when(mockAuthClient.getNewToken("username2", "password2")).thenReturn(new AuthToken());
		when(mockAuthClient.getNewToken("username3", "password3")).thenReturn(testToken);
		
		try {
			testService.authenticate("username", "password");
			assertTrue("Should never get here", false);
		} catch (UnauthorizedException e) {
			assertEquals("Correct exception is thrown", "Invalid username/password", e.getMessage());
		}
		
		try {
			testService.authenticate("username2", "password2");
			assertTrue("Should never get here", false);
		} catch (UnauthorizedException e) {
			assertEquals("Correct exception is thrown", "Invalid username/password", e.getMessage());
		}

		try {
			testService.authenticate("username3", "password3");
			assertTrue("Should never get here", false);
		} catch (UnauthorizedException e) {
			assertEquals("Correct exception is thrown", "Invalid username/password", e.getMessage());
		}
	}

	@Test
	public void invalidateTokenTest() {
		when(mockAuthClient.invalidateToken("a-token-string")).thenReturn(true);
		assertTrue(testService.invalidateToken("a-token-string"));
	}
}
