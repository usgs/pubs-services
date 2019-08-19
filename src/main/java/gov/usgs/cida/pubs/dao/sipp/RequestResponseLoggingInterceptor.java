package gov.usgs.cida.pubs.dao.sipp;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import gov.usgs.cida.pubs.domain.sipp.SippRequestLog;

@Component
public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		SippRequestLog log = logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(response, log);
		return response;
	}

	private SippRequestLog logRequest(HttpRequest request, byte[] body) throws IOException {
		SippRequestLog log = new SippRequestLog();
		log.setUri(request.getURI());
		log.setMethod(request.getMethod());
		log.setRequestHeaders(request.getHeaders());
		log.setRequestBody(new String(body, "UTF-8"));
		log.setId(SippRequestLog.getDao().add(log));
		return log;
	}

	private void logResponse(ClientHttpResponse response, SippRequestLog log) throws IOException {
		log.setStatusCode(response.getStatusCode());
		log.setStatusText(response.getStatusText());
		log.setResponseHeaders(response.getHeaders());
		log.setResponseBody(StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
		SippRequestLog.getDao().update(log);
	}
}
