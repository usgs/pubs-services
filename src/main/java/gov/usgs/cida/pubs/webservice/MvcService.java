package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.PubsConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * @author drsteini
 *
 */
public abstract class MvcService<D> {

    private static final Logger LOG = LoggerFactory.getLogger(MvcService.class);

    /**
     * Helper to check if null and add to filters map
     */
    protected Map<String, Object> addToFiltersIfNotNull(Map<String, Object> filters, String key, String value) {
        if (StringUtils.isNotEmpty(value)) {
            filters.put(key, value);
        }
    	return filters;
    }

    /**
     * Helper creates an array of non-empty values and places it in the filters map.
     */
    protected Map<String, Object> addToFiltersIfNotNull(Map<String, Object> filters, String key, String[] values) {
    	ArrayList<String> filterValues = new ArrayList<>();
        if (null != values && 0 < values.length) {
	    	for(String value : values) {
		        if (StringUtils.isNotEmpty(value)) {
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
    	if (StringUtils.isNotEmpty(orderBy)) {
    		String newOrderBy = orderBy;
    		if ("reportnumber".equalsIgnoreCase(newOrderBy)) {
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

    @ExceptionHandler(Exception.class)
    public @ResponseBody String handleUncaughtException(Exception ex, WebRequest request, HttpServletResponse response) throws IOException {
        if (ex instanceof AccessDeniedException) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "You are not authorized to perform this action.";
        } else if (ex instanceof MissingServletRequestParameterException) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ex.getLocalizedMessage();
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            int hashValue = response.hashCode();
            //Note: we are giving the user a generic message.  
            //Server logs can be used to troubleshoot problems.
            String msgText = "Something bad happened. Contact us with Reference Number: " + hashValue;
            LOG.error(msgText, ex);
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
