package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.io.IOException;
import java.util.ArrayList;
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

    /**
     * Helper to check if null and add to filters map
     */
    protected Map<String, Object> addToFiltersIfNotNull(Map<String, Object> filters, String key, String value) {
        if (!PubsUtilities.isNullOrEmpty(value)) {
            filters.put(key, value);
        }
    	return filters;
    }
    
    /**
     * Helper creates an array of non-empty values and places it in the filters map.
     */
    protected Map<String, Object> addToFiltersIfNotNull(Map<String, Object> filters, String key, String[] values) {
    	ArrayList<String> filterValues = new ArrayList<>();
        if (!PubsUtilities.isNullOrEmpty(values)) {
	    	for(String value : values) {
		        if (!PubsUtilities.isNullOrEmpty(value)) {
		        	filterValues.add(value);
		        }
	    	}
	    	filters.put(key, filterValues.toArray());
        }
    	return filters;
    }
    
    /**
     * Extends current order by clause with more options if necessary
     */
    protected Map<String, Object> updateOrderBy(Map<String, Object> filters, String orderBy, String orderByDir) {
    	if ( ! PubsUtilities.isNullOrEmpty(orderBy)) {
    		String newOrderBy = orderBy;
    		if (newOrderBy.equalsIgnoreCase("reportnumber")) {
    			newOrderBy = "series_number";
            }
    		
		    String exitingOrderBy = (String) filters.get("orderby");
	    	String fullOrderBy = newOrderBy + " " + (orderByDir == null ? "" : orderByDir);
		    
		    if(exitingOrderBy != null) {
		    	filters.put("orderby", exitingOrderBy + ", " + fullOrderBy);
		    } else {
		    	filters.put("orderby", fullOrderBy);
		    	
		    }
    	}

    	return filters;
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
    public @ResponseBody String handleUncaughtException(Exception ex, WebRequest request, HttpServletResponse response) throws IOException {
        if (ex instanceof AccessDeniedException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "You are not authorized to perform this action.";
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            int hashValue = response.hashCode();
            //Note: we are giving the user a generic message.  
            //Server logs can be used to troubleshoot problems.
            String msgText = "Something bad happened. Contact us with Reference Number: " + hashValue;
            log.error(msgText, ex);
            return msgText;
        }
    }

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
