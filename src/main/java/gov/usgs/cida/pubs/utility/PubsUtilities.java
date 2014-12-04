package gov.usgs.cida.pubs.utility;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.StopWords;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * @author drsteini
 *
 */
public final class PubsUtilities {

	private PubsUtilities() {
		
	}

    /** Utility method for determining if a string represents an integer.  
     * @param number .
     * @return Boolean .
     * */
    public static Boolean isInteger(final String number) {
        if (StringUtils.isBlank(number)) {
        	return false;
        }

        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static String getUsername() {
	    String username = PubsConstants.ANONYMOUS_USER;
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
		if (null != auth && auth.getPrincipal() instanceof User) {
			username = ((User) auth.getPrincipal()).getUsername();
		}
		return username;
    }

    public static Integer parseInteger(final String intString) {
        return isInteger(intString) ? Integer.parseInt(intString) : null;
    }

    public static String buildErrorMsg(final String messageName, final Object[] messageArguments) {
        String messageProp;
        if (null != messageName) {
        	if (messageName.startsWith("{") && messageName.endsWith("}")) {
	            Properties props = new Properties();
	            try {
	                props.load(PubsUtilities.class.getClassLoader().getResourceAsStream("ValidationMessages.properties"));
	            } catch (Exception e) {
	                throw new RuntimeException("Unable to load ValidationMessages.properties", e);
	            }
	            messageProp = props.getProperty(messageName.substring(1, messageName.length() - 1));
	            if (null == messageProp) {
	                throw new RuntimeException("Unable to load messageResource " + messageName);
	            }
	        } else {
	            messageProp = messageName;
	        }
	        MessageFormat messageFormat = new MessageFormat(messageProp);
	        return messageFormat.format(messageArguments);
        } else {
        	return messageName;
        }
    }

    public static boolean isUsgsNumberedSeries(final PublicationSubtype pubSubtype) {
        boolean rtn = false;
        if (null != pubSubtype
                && PublicationSubtype.USGS_NUMBERED_SERIES.equals(pubSubtype.getId())) {
            rtn = true;
        }
        return rtn;
    }

    public static boolean isUsgsUnnumberedSeries(final PublicationSubtype pubSubtype) {
        boolean rtn = false;
        if (null != pubSubtype
                && PublicationSubtype.USGS_UNNUMBERED_SERIES.equals(pubSubtype.getId())) {
            rtn = true;
        }
        return rtn;
    }

    public static boolean isPublicationTypeArticle(final PublicationType pubType) {
        boolean rtn = false;
        if (null != pubType
                && PublicationType.ARTICLE.equals(pubType.getId())) {
            rtn = true;
        }
        return rtn;
    }
    
    public static boolean isSpnProduction(final String ipdsReviewProcessState) {
        boolean rtn = false;
        if (StringUtils.isNotBlank(ipdsReviewProcessState)
        		&& ProcessType.SPN_PRODUCTION.getIpdsValue().contentEquals(ipdsReviewProcessState)) {
        	rtn = true;
        }
        return rtn;
    }
    
    public static List<String> removeStopWords(final String q) {
    	if (StringUtils.isBlank(q)) {
    		return null;
    	} else {
    		//Arrays.asList returns a fixed size java.util.Arrays$ArrayList, so we actually need to create a real list to 
    		//be able to remove entries from it.
	    	List<String> splitQ = new LinkedList<>(Arrays.asList(q.trim().toLowerCase().split(PubsConstants.SEARCH_TERMS_SPLIT_REGEX)));
	    	splitQ.removeAll(StopWords.STOP_WORD_LIST);
	    	return splitQ;
    	}
    }
    
}
