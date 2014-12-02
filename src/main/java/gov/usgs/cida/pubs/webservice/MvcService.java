package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * @author drsteini
 *
 */
@RequestMapping(produces={PubsConstants.MIME_TYPE_APPLICATION_JSON, PubsConstants.MIME_TYPE_TEXT_PLAIN, PubsConstants.MIME_TYPE_APPLICATION_RSS})
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
     * Configures the filters/orderby settings to support single search
     * @param filters
     * @return
     */
    protected Map<String, Object> configureSingleSearchFilters(Map<String, Object> filters, String searchTerms) {
        if (StringUtils.isNotEmpty(searchTerms)) {
	    	filters.put("searchTerms", searchTerms.split("[\\s+,+]"));
        }
    	return filters;
    }

    protected String buildOrderBy(String orderBy) {
    	StringBuilder rtn = new StringBuilder("publication_year desc nulls last, display_to_public_date desc"); 
    	if (StringUtils.isNotEmpty(orderBy)) {
    		if ("date".equalsIgnoreCase(orderBy)) {
    			//Nothing to see here, this is the default sort
    		} else if ("title".equalsIgnoreCase(orderBy)) {
    			rtn.insert(0, "title asc, ");
    		}
    	}

    	return rtn.toString();
    }

    protected Map<String, Object> buildPaging (String inPageRowStart, String inPageSize, String inPageNumber) {
    	Integer pageRowStart = PubsUtilities.parseInteger(inPageRowStart);
    	Integer pageSize = PubsUtilities.parseInteger(inPageSize);
    	Integer pageNumber = PubsUtilities.parseInteger(inPageNumber);
        Map<String, Object> paging = new HashMap<>();
        if (null != pageNumber) {
        	//pageNumber overrides the pageRowStart
        	if (null == pageSize) {
        		//default pageSize with pageNumber
        		pageSize = 25;
        	}
        	pageRowStart = ((pageNumber - 1) * pageSize);
        } else {
        	if (null == pageRowStart) {
        		pageRowStart = 0;
        	}
        	if (null == pageSize) {
        		//default pageSize with pageRowStart
        		pageSize = 15;
        	}
        }
    	paging.put("pageRowStart", pageRowStart);
    	paging.put("pageSize", pageSize);
    	paging.put("pageNumber", pageNumber);
    	return paging;
    }
    
    @ExceptionHandler(Exception.class)
    public @ResponseBody String handleUncaughtException(Exception ex, WebRequest request, HttpServletResponse response) throws IOException {
        if (ex instanceof AccessDeniedException) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return "You are not authorized to perform this action.";
        } else if (ex instanceof MissingServletRequestParameterException
        		|| ex instanceof HttpMediaTypeNotSupportedException) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ex.getLocalizedMessage();
        } else if (ex instanceof HttpMessageNotReadableException) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            //This exception's message contains implementation details after the new line, so only take up to that.
            return ex.getLocalizedMessage().substring(0, ex.getLocalizedMessage().indexOf("\n"));
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
