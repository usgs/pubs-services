package gov.usgs.cida.pubs.webservice;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
	private static final Logger LOG = LoggerFactory.getLogger(CustomAsyncExceptionHandler.class);

	@Override
	public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
		LOG.error("Exception message - " + throwable.getMessage());
		LOG.error("Method name - " + method.getName());
		for (Object param : obj) {
			LOG.error("Parameter value - " + param);
		}
	}
}
