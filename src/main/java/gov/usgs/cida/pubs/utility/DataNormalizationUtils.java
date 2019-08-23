package gov.usgs.cida.pubs.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Methods to transform values to and from the canonical form stored in the database
 */
public final class DataNormalizationUtils {

	public static final String ORCID_REGEX = "\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9X]"; // format of the canonical short form of an orcid
	public static final Pattern ORCID_PATTERN = Pattern.compile(ORCID_REGEX);
	public static final String ORCID_PREFIX = "https://orcid.ord/"; // Prefix used in the long form of an orcid

	/**
	 *   returns the canonical short form (19 digits: 0000-0002-1825-0097) for the specified ORCID or the ORCID if short form not found
	 * @param orcid The ORCDID to normalize
	 * @return short form ORCID or the original orcid if orcid does not have a short ORCID component
	 */
	public static String normalizeOrcid(String orcid) {
		String formattedOrcid = orcid;
		if (null != orcid) {
			Matcher matcher = ORCID_PATTERN.matcher(orcid);
			if (matcher.find()) {
				formattedOrcid = matcher.group();
			}
		}
		return formattedOrcid;
	}

	/**
	 *   returns the canonical long form for the specified ORCID or the ORCID unchanged if short form not found
	 * the protocol specified is https
	 * @param orcid The ORCDID to denormalize
	 * @return long form ORCID or original orcid if orcid does not have a short ORCID component
	 */
	public static String denormalizeOrcid(String orcid) {
		String formattedOrcid = orcid;
		if (orcid != null && ORCID_PATTERN.matcher(orcid).find()) {
			formattedOrcid = ORCID_PREFIX + normalizeOrcid(orcid);
		}
		return formattedOrcid;
	}

	/**
	 * Array version of {@link #normalizeOrcid(String)}
	 */
	public static String[] normalizeOrcid(String[] orcids) {
		String[] denormalizedOrcids = null;
		if(orcids != null) {
			denormalizedOrcids = new String[orcids.length];
			for(int i=0; i < orcids.length; i++) {
				denormalizedOrcids[i] =  normalizeOrcid(orcids[i]);
			}
		}
		return denormalizedOrcids;
	}

}
