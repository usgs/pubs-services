package gov.usgs.cida.pubs.webservice;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;

public class GlobalDefaultExceptionHandlerTest {

	@Mock
	WebRequest request;

	private GlobalDefaultExceptionHandler controller = new GlobalDefaultExceptionHandler();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void handleUncaughtExceptionTest() throws IOException {
		HttpServletResponse response = new MockHttpServletResponse();
		assertEquals("Something bad happened. Contact us with Reference Number: ",
				controller.handleUncaughtException(new RuntimeException(), request, response).substring(0, 58));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());

		response = new MockHttpServletResponse();
		assertEquals("You are not authorized to perform this action.",
				controller.handleUncaughtException(new AccessDeniedException("haha"), request, response));
		assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());

		response = new MockHttpServletResponse();
		assertEquals("Required String parameter 'parm' is not present",
				controller.handleUncaughtException(new MissingServletRequestParameterException("parm", "String"), request, response));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		response = new MockHttpServletResponse();
		assertEquals("no way",
				controller.handleUncaughtException(new HttpMediaTypeNotSupportedException("no way"), request, response));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		response = new MockHttpServletResponse();
		assertEquals("ok to see",
				controller.handleUncaughtException(new HttpMessageNotReadableException("ok to see\nhide this\nand this"), request, response));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

		response = new MockHttpServletResponse();
		assertEquals("Some123$Mes\tsage!!.",
				controller.handleUncaughtException(new HttpMessageNotReadableException("Some123$Mes\tsage!!."), request, response));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}

}
