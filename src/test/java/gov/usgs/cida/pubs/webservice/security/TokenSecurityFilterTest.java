package gov.usgs.cida.pubs.webservice.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenSecurityFilterTest {

	private AuthenticationService authServiceMock; 

	@Before
	public void setup() {
		//clear security context each time
		SecurityContextHolder.clearContext();
		authServiceMock = mock(AuthenticationService.class);
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
	public void doFilterNoTokenTest() throws IOException, ServletException, UnauthorizedException {
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		HttpServletResponse mockResponse = mock(HttpServletResponse.class);
		FilterChain mockFilterChain = mock(FilterChain.class);

		TokenSecurityFilter testFilter = new TokenSecurityFilter(authServiceMock);
		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn("");
		testFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
		verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
		verify(authServiceMock, times(0)).checkToken(anyString());
	}

	@Test
	public void doFilterTokenTest() throws IOException, ServletException, UnauthorizedException {
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		HttpServletResponse mockResponse = mock(HttpServletResponse.class);
		FilterChain mockFilterChain = mock(FilterChain.class);

		TokenSecurityFilter testFilter = new TokenSecurityFilter(authServiceMock);
		when(mockRequest.getHeader(TokenSecurityFilter.AUTHORIZATION_HEADER)).thenReturn("Bearer a-token-string");
		testFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
		verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
		verify(authServiceMock, times(1)).checkToken("a-token-string");
	}

}
