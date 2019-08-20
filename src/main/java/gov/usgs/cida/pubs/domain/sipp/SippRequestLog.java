package gov.usgs.cida.pubs.domain.sipp;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.BaseDomain;

@Component
public class SippRequestLog extends BaseDomain<SippRequestLog> {

	private static IDao<SippRequestLog> sippRequestLogDao;

	private String uri;
	private String method;
	private String requestHeaders;
	private String requestBody;
	private String statusCode;
	private String statusText;
	private String responseHeaders;
	private String responseBody;
	public String getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri.toString();
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(HttpMethod method) {
		this.method = method.toString();
	}
	public String getRequestHeaders() {
		return requestHeaders;
	}
	public void setRequestHeaders(HttpHeaders requestHeaders) {
		this.requestHeaders = requestHeaders.toString();
	}
	public String getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode.toString();
	}
	public String getStatusText() {
		return statusText;
	}
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	public String getResponseHeaders() {
		return responseHeaders;
	}
	public void setResponseHeaders(HttpHeaders responseHeaders) {
		this.responseHeaders = responseHeaders.toString();
	}
	public String getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
	public static IDao<SippRequestLog> getDao() {
		return sippRequestLogDao;
	}
	@Autowired
	@Qualifier("sippRequestLogDao")
	public void setSippRequestLogDao(IDao<SippRequestLog> inSippRequestLogDao) {
		sippRequestLogDao = inSippRequestLogDao;
	}

}
