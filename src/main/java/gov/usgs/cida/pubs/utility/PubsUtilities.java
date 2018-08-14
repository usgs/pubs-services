package gov.usgs.cida.pubs.utility;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.validation.ValidatorResult;
import gov.usgs.cida.pubs.webservice.security.PubsAuthentication;
import gov.usgs.cida.pubs.webservice.security.PubsRoles;

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

	public static boolean isSpnUser() {
		boolean rtn = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (null != auth && null != auth.getAuthorities() && auth instanceof PubsAuthentication) {
			Iterator<? extends GrantedAuthority> i = auth.getAuthorities().iterator();
			while (i.hasNext() && !rtn) {
				if (i.next().getAuthority().equalsIgnoreCase(PubsRoles.PUBS_SPN_USER.getSpringRole())) {
					rtn = true;
				}
			}
		}
		return rtn;
	}

	public static boolean isSpnOnly() {
		boolean hasSpn = false;
		boolean hasOther = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (null != auth && null != auth.getAuthorities() && auth instanceof PubsAuthentication) {
			Iterator<? extends GrantedAuthority> i = auth.getAuthorities().iterator();
			while (i.hasNext()) {
				GrantedAuthority gAuth = i.next();
				if (gAuth.getAuthority().equalsIgnoreCase(PubsRoles.PUBS_SPN_USER.getSpringRole())) {
					hasSpn = true;
				} else if (gAuth.getAuthority().equalsIgnoreCase(PubsRoles.PUBS_ADMIN.getSpringRole())
						|| gAuth.getAuthority().equalsIgnoreCase(PubsRoles.PUBS_CATALOGER_USER.getSpringRole())) {
					hasOther = true;
				}
			}
		}
		return hasSpn && !hasOther;
	}

	/**
	 * @param intString
	 * @return the Integer represented by intString, or null if it isn't an Integer
	 */
	public static Integer parseInteger(final String intString) {
		return isInteger(intString) ? Integer.parseInt(intString) : null;
	}

	/**
	 * @param intString
	 * @param domainObject
	 * @return null if intString matches the id in domainObject and they're not null, otherwise a ValidatorResult
	 */
	public static ValidatorResult validateIdsMatch(final String intString, final BaseDomain<?> domainObject) {
		ValidatorResult result = new ValidatorResult("id", "The id in the URL does not match the id in the request.", SeverityLevel.FATAL, intString);
		Integer id = parseInteger(intString);
		Integer objId = domainObject.getId();
		if (null != id && null != objId && id.intValue() == objId.intValue()) {
			result = null;
		}
		return result;
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

	public static boolean isPublicationTypeUSGSDataRelease(final PublicationSubtype pubSubtype) {
		boolean rtn = false;
		if (null != pubSubtype
				&& PublicationSubtype.USGS_DATA_RELEASE.equals(pubSubtype.getId())) {
			rtn = true;
		}
		return rtn;
	}

	public static boolean isPublicationTypeUSGSWebsite(final PublicationSubtype pubSubtype) {
		boolean rtn = false;
		if (null != pubSubtype
				&& PublicationSubtype.USGS_WEBSITE.equals(pubSubtype.getId())) {
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

}