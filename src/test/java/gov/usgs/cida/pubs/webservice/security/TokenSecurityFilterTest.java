package gov.usgs.cida.pubs.webservice.security;

import gov.usgs.cida.auth.client.IAuthClient;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import gov.usgs.cida.auth.model.AuthToken;

public class TokenSecurityFilterTest {
	private class TestTokenSecurityFilter extends TokenSecurityFilter {
		public TestTokenSecurityFilter() {
			super();
			authClient = mock(IAuthClient.class);
		}
		
		public IAuthClient getAuthClient() {
			return authClient;
		}
	}
	
	@Before
	public void setup() {
		//clear security context each time
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	@Test
	public void testGetTokenFromHeader() {
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		
		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn(null);
		assertNull("returns null if no auth header found", TokenSecurityFilter.getTokenFromHeader(mockRequest));

		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn("token-not-in-bearer-format");
		assertNull("returns null if auth header not in \"Bearer xxxx\" format", TokenSecurityFilter.getTokenFromHeader(mockRequest));

		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer the-token-string");
		assertEquals("correctly extracts token from \"Bearer xxxx\" format", TokenSecurityFilter.getTokenFromHeader(mockRequest), "the-token-string");
	}
	
	@Test
	public void testDoFilter_OPTIONS() throws IOException, ServletException {
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		HttpServletResponse mockResponse = mock(HttpServletResponse.class);
		FilterChain mockFilterChain = mock(FilterChain.class);
		
		//Verify all OPTIONS requests just continue down the chain
		when(mockRequest.getMethod()).thenReturn("OPTIONS");
		new TestTokenSecurityFilter().doFilter(mockRequest, mockResponse, mockFilterChain);
		verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
		verify(mockResponse, times(0)).setStatus(401);
	}
	
	@Test
	public void testDoFilter_invalid_token() throws IOException, ServletException {
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		HttpServletResponse mockResponse = mock(HttpServletResponse.class);
		FilterChain mockFilterChain = mock(FilterChain.class);
		
		//Tokens marked invalid produce a 401 response
		TestTokenSecurityFilter testFilter = new TestTokenSecurityFilter();
		when(mockRequest.getMethod()).thenReturn("GET");
		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer the-token-string");
		when(testFilter.getAuthClient().isValidToken("the-token-string")).thenReturn(false);
		testFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
		verify(mockFilterChain, times(0)).doFilter(mockRequest, mockResponse);
		verify(mockResponse, times(1)).setStatus(401);

	}
	
	@Test
	public void testDoFilter_invalid_roles() throws IOException, ServletException {
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		HttpServletResponse mockResponse = mock(HttpServletResponse.class);
		FilterChain mockFilterChain = mock(FilterChain.class);

		TestTokenSecurityFilter testFilter = new TestTokenSecurityFilter();
		
		//Tokens marked valid but with no pubs roles produce a 401 response
		AuthToken testToken = mock(AuthToken.class);
		when(testToken.getUsername()).thenReturn("username");
		
		ArrayList<String> randomRoles = new ArrayList<>();
		randomRoles.add("RANDOM_ROLE");
		
		when(mockRequest.getMethod()).thenReturn("GET");
		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer the-token-string");
		when(testFilter.getAuthClient().isValidToken("the-token-string")).thenReturn(true);
		when(testFilter.getAuthClient().getToken("the-token-string")).thenReturn(testToken);
		when(testFilter.getAuthClient().getRolesByToken("the-token-string")).thenReturn(randomRoles);
		testFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
		verify(mockFilterChain, times(0)).doFilter(mockRequest, mockResponse);
		verify(mockResponse, times(1)).setStatus(401);
		assertFalse("Security context has invalid authentication", SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
	}
	
	@Test
	public void testDoFilter_valid() throws IOException, ServletException {
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		HttpServletResponse mockResponse = mock(HttpServletResponse.class);
		FilterChain mockFilterChain = mock(FilterChain.class);

		TestTokenSecurityFilter testFilter = new TestTokenSecurityFilter();
		
		//Tokens marked valid but with no pubs roles produce a 401 response
		AuthToken testToken = mock(AuthToken.class);
		when(testToken.getUsername()).thenReturn("username");
		
		ArrayList<String> pubsRoles = new ArrayList<>();
		pubsRoles.add(PubsRoles.PUBS_ADMIN.name());
		
		when(mockRequest.getMethod()).thenReturn("GET");
		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer the-token-string");
		when(testFilter.getAuthClient().isValidToken("the-token-string")).thenReturn(true);
		when(testFilter.getAuthClient().getToken("the-token-string")).thenReturn(testToken);
		when(testFilter.getAuthClient().getRolesByToken("the-token-string")).thenReturn(pubsRoles);
		testFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
		verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
		verify(mockResponse, times(0)).setStatus(401);
		assertTrue("Security context has valid authentication", SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
	}
}
