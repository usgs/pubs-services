package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author drsteini
 *
 */
public abstract class MvcService<D> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Map<String, Object> buildResponseMap(final HttpServletResponse response, final Object objects) {
        return buildResponseMap(response, objects, null, null);
    }

    protected Map<String, Object> buildResponseMap(final HttpServletResponse response, final Object objects, final Integer totalRecordCount) {
        return buildResponseMap(response, objects, totalRecordCount, null);
    }

    protected Map<String, Object> buildResponseMap(final HttpServletResponse response, final Object objects,
            final Integer totalRecordCount, final ValidationResults validationResults) {
        int statusCode = HttpServletResponse.SC_OK;
        Map<String,Object> responseMap = new HashMap<String,Object>();
        responseMap.put("rowCount", null != totalRecordCount ? totalRecordCount 
                : objects instanceof List ? ((List<?>) objects).size() : null == objects ? 0 : 1);
        responseMap.put("data", objects);
        if (null != validationResults && !validationResults.isEmpty()) {
            responseMap.put("success", false);
            responseMap.put("errors", validationResults);
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
        } else {
            responseMap.put("success", true);
        }
        if (null != response) {
            response.setStatus(statusCode);
        }
        return responseMap;
    }

    protected Map<String, Object> buildErrorResponseMap(final String msg) {
        ValidationResults msgs = new ValidationResults();
        msgs.addValidatorResult(new ValidatorResult(null, msg, null, null));
        Map<String,Object> responseMap = new HashMap<String,Object>();
        responseMap.put("errors", msgs);
        responseMap.put("success", false);
        return responseMap;
    }

    /**
     * Takes a request body data and maps the values onto the given domain instance
     * @param body json request data body
     * @param givenDomain sample instance because of type erasure
     * @return domain from json body or empty sample on exception.
     */
    @SuppressWarnings("unchecked") // need to cast getClass() to Class<D> which returns Class<?>
    protected D getDomainFromExtRequest(MultiValueMap<String, String> body, D givenDomain) {

        D newDomain = givenDomain; // an empty user to return in exception

        try {
            ObjectMapper mapper = new ObjectMapper();
            String       data   = body.getFirst("data").toString();

            newDomain = mapper.readValue(data, (Class<D>) givenDomain.getClass());

        } catch (Exception e) {
            String myError = "Failed to parse " + givenDomain.getClass().getSimpleName() + " from json body";
            log.error(myError, e);
            throw new RuntimeException(myError, e); 
        }

        return newDomain;
    }

    @ExceptionHandler(Exception.class)
    public @ResponseBody Map<String,? extends Object> handleUncaughtException(Exception ex, WebRequest request, HttpServletResponse response) throws IOException {
        int statusCode = HttpServletResponse.SC_OK;
        Map<String, Object> out = new HashMap<String,Object>();
        if (ex instanceof AccessDeniedException) {
            statusCode = HttpServletResponse.SC_FORBIDDEN;
            out = buildErrorResponseMap("You are not authorized to perform this action.");
        } else {
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            int hashValue = response.hashCode();
            //Note: we are giving the user a generic message.  
            //Server logs can be used to troubleshoot problems.
            String msgText = "Something bad happened. Contact us with Reference Number: " + hashValue;
            log.error(msgText, ex);
            out = buildErrorResponseMap(msgText);
        }
        response.setStatus(statusCode);
        return out;
    }

//    protected HttpSession getSession(){
//        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//        return attr.getRequest().getSession();
//    }
//
    protected boolean validateParametersSetHeaders(HttpServletRequest request, HttpServletResponse response) {
        boolean rtn = true;
        setHeaders(response);
        if (request.getParameterMap().isEmpty()) {
            rtn = false;
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return rtn;
    }

    protected void setHeaders(HttpServletResponse response) {
        response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
    }
}
