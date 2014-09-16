package gov.usgs.cida.pubs.utility;

import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;

import java.text.MessageFormat;
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

    /** The default username for anonymous access. */
    public static final String ANONYMOUS_USER = "anonymous";

	private PubsUtilities() {
		
	}

    /** Utility method for determining if a string represents an integer.  
     * @param number .
     * @return Boolean .
     * */
    public static Boolean isInteger(final String number) {
        if (StringUtils.isEmpty(number)) {
        	return false;
        }

        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    /** 
     * Utility method to scan a string array an turn all non-Integer values into -1
     * @param inStringArray .
     * @return .
     */
    public static Integer[] cleanStringArray(final String[] inStringArray) {
        Integer[] outIntegerArray = new Integer[inStringArray.length];
        for (int i=0; i<inStringArray.length; i++) {
            outIntegerArray[i] = isInteger(inStringArray[i]) ? Integer.parseInt(inStringArray[i]) : -1;
        }
        return outIntegerArray;
    }

    public static String getUsername() {
	    String username = ANONYMOUS_USER;
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
		if (null != auth) {
			if (auth.getPrincipal() instanceof User) {
				username = ((User) auth.getPrincipal()).getUsername();
//TODO What is our real Authentication???				
//			} else {
//				username = auth.toString();
			}
		}
		return username;
    }

    public static Integer parseInteger(final String intString) {
        return isInteger(intString) ? Integer.parseInt(intString) : null;
    }

    public static String parseString(final String inString) {
        return null == inString ? null : 0 == inString.trim().length() ? null : inString.trim();
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
}
