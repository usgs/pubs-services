package gov.usgs.cida.pubs.webservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
	private static final Logger LOG = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);
	private static final String NESTED_EXCEPTION_REGEX = ".*nested exception is.*:";

	public static final String ERROR_MESSAGE_KEY = "Error Message";

	@ExceptionHandler(Exception.class)
	public @ResponseBody Map<String, String> handleUncaughtException(Exception ex, WebRequest request, HttpServletResponse response) throws IOException {
		Map<String, String> responseMap = new HashMap<>();
		if (ex instanceof AccessDeniedException) {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			responseMap.put(ERROR_MESSAGE_KEY, "You are not authorized to perform this action.");
		} else if (ex instanceof BindException && hasOnlyFieldErrors((BindException) ex)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			responseMap.put(ERROR_MESSAGE_KEY, formatErrorMessage((BindException) ex));
		} else if (ex instanceof MethodArgumentNotValidException) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			responseMap.put(ERROR_MESSAGE_KEY, ((MethodArgumentNotValidException) ex).getBindingResult().getFieldError().getDefaultMessage());
		} else if (ex instanceof MissingServletRequestParameterException
				|| ex instanceof HttpMediaTypeNotSupportedException
				|| ex instanceof HttpMediaTypeNotAcceptableException) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			responseMap.put(ERROR_MESSAGE_KEY, ex.getLocalizedMessage());
		} else if (ex instanceof HttpMessageNotReadableException) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			if (ex.getLocalizedMessage().contains("\n")) {
				//This exception's message contains implementation details after the new line, so only take up to that.
				responseMap.put(ERROR_MESSAGE_KEY, ex.getLocalizedMessage().substring(0, ex.getLocalizedMessage().indexOf("\n")));
			} else {
				responseMap.put(ERROR_MESSAGE_KEY, ex.getLocalizedMessage().replaceAll("([a-zA-Z]+\\.)+",""));
			}
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			int hashValue = response.hashCode();
			//Note: we are giving the user a generic message.  
			//Server logs can be used to troubleshoot problems.
			String msgText = "Something bad happened. Contact us with Reference Number: " + hashValue;
			LOG.error(msgText, ex);
			responseMap.put(ERROR_MESSAGE_KEY, msgText);
		}
		return responseMap;
	}

	// return true if the bind exception only has field (data) errors, typically caused by bad input
	private boolean hasOnlyFieldErrors(BindException ex) {
		return ex.hasFieldErrors() && !ex.hasGlobalErrors();
	}

	// parse out a user friendly field message from the field error in the binding exception
	private String formatErrorMessage(BindException ex) {
		String errorMess = "";
		String sep = "";

		for(FieldError err: ex.getFieldErrors()) {
			String mess = err.getDefaultMessage() == null ? " Error processing field" : err.getDefaultMessage();
			mess = mess.replaceAll(NESTED_EXCEPTION_REGEX, "");
			String prefix = "Error in parameter " + err.getField() + ":";
			errorMess = errorMess + sep + prefix + mess;
			sep = " -- ";
		}

		return errorMess;
	}

}