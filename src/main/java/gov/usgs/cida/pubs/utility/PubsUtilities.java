package gov.usgs.cida.pubs.utility;

import java.text.MessageFormat;
import java.util.Properties;

import org.w3c.dom.Element;

/**
 * @author drsteini
 *
 */
public final class PubsUtilities {

    /** Utility method for determining if a string represents an integer.  
     * @param number .
     * @return Boolean .
     * */
    public static Boolean isInteger(final String number) {
        if (isNullOrEmpty(number)) return false;
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    /**
     * Utility method to check null or empty values.
     * @param value . 
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(final Object value) {
        if (null == value 
                || 0 == value.toString().trim().length()) {
                return true;
            }
        return false;
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


    public static Integer parseInteger(final String intString) {
        return isInteger(intString) ? Integer.parseInt(intString) : null;
    }

    public static String parseString(final String inString) {
        return null == inString ? null : 0 == inString.trim().length() ? null : inString.trim();
    }

    public static String buildErrorMsg(final String messageName, final Object[] messageArguments) {
        String messageProp = new String();
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
    }

    public static String getNodeText(final Element element, final String tagName) {
        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }
}
