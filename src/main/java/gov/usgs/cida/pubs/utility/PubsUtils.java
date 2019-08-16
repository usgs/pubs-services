package gov.usgs.cida.pubs.utility;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.validation.ValidatorResult;

public final class PubsUtils {

	private PubsUtils() {
	}

	public static final Pattern ORCID_PATTERN = Pattern.compile("\\d{4}-\\d{4}-\\d{4}-(\\d{3}X|\\d{4})"); //  format of the canonical short form of an orcid

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
		String username = PubsConstantsHelper.ANONYMOUS_USER;
		Authentication auth = getAuthentication();

		if (null != auth && auth.getPrincipal() instanceof User) {
			username = ((User) auth.getPrincipal()).getUsername();
		}
		return username;
	}

	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static boolean isSpnUser(ConfigurationService configurationService) {
		boolean rtn = false;
		Authentication auth =  getAuthentication();
		if (null != auth && null != auth.getAuthorities()) {
			Set<String> spnAuthorities = new HashSet<String>(Arrays.asList(configurationService.getSpnAuthorities()));
			if (0 < auth.getAuthorities().stream().filter(z -> spnAuthorities.contains(z.getAuthority())).collect(Collectors.toList()).size()) {
				rtn = true;
			}
		}
		return rtn;
	}

	public static boolean isOtherPubsUser(ConfigurationService configurationService) {
		boolean rtn = false;
		Authentication auth =  getAuthentication();
		if (null != auth && null != auth.getAuthorities()) {
			Set<String> pubsAuthorities = new HashSet<String>(Arrays.asList(configurationService.getAuthorizedAuthorities()));
			pubsAuthorities.removeAll(Arrays.asList(configurationService.getSpnAuthorities()));
			if (0 < auth.getAuthorities().stream().filter(z -> pubsAuthorities.contains(z.getAuthority())).collect(Collectors.toList()).size()) {
				rtn = true;
			}
		}
		return rtn;
	}

	public static boolean isSpnOnly(ConfigurationService configurationService) {
		return isSpnUser(configurationService) && !isOtherPubsUser(configurationService);
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
					props.load(PubsUtils.class.getClassLoader().getResourceAsStream("ValidationMessages.properties"));
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

	/**
	 *   returns the canonical short form (19 digits: 0000-0002-1825-0097) for the specified ORCID or null if short form not found
	 * @param orcid The ORCDID to normalize
	 * @return short form ORCID or null if orcid does not have a short ORCID component
	 */
	public static String normalizeOrcid(String orcid) {
		String formattedOrcid = null;
		if (null != orcid) {
			Matcher matcher = ORCID_PATTERN.matcher(orcid);
			if (matcher.find()) {
				formattedOrcid = matcher.group();
			} else {
				formattedOrcid = null;
			}
		}
		return formattedOrcid;
	}

}