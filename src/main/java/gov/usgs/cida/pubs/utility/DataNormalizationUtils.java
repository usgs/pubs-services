package gov.usgs.cida.pubs.utility;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.PersonContributor;

/**
 * Methods to transform values to and from the canonical form stored in the database
 */
public final class DataNormalizationUtils {

	public static final String ORCID_REGEX = "\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9X]"; // format of the canonical short form of an orcid
	public static final Pattern ORCID_PATTERN = Pattern.compile(ORCID_REGEX);
	public static final String ORCID_PREFIX = "https://orcid.ord/"; // Prefix used in the long form of an orcid

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

	/**
	 *   returns the canonical long form for the specified ORCID or null if short form not found
	 * the protocol specified is https
	 * @param orcid The ORCDID to denormalize
	 * @return long form ORCID or null if orcid does not have a short ORCID component
	 */
	public static String denormalizeOrcid(String orcid) {
		String formattedOrcid = null;
		String normalizedOrcid = DataNormalizationUtils.normalizeOrcid(orcid);
		if (normalizedOrcid != null) {
			formattedOrcid = ORCID_PREFIX + normalizedOrcid;
		}
		return formattedOrcid;
	}
	
	// update fields to form stored in the database
	public static void normalize(PersonContributor<?> object) {
		boolean orcidIsSet = object != null && object.getOrcid() != null;
		if(orcidIsSet) {
			String orcid = normalizeOrcid(object.getOrcid());
			if(orcid != null) {
				object.setOrcid(orcid);
			}
		}
	}
	
	/** Translate filter values to format expected when querying against the database.
	 **/
	public static void normalizeFilters(Map<String, Object> filters) {
		if(filters != null) {
			String[] orcids = (String[]) filters.get(PublicationDao.ORCID);
			if(orcids != null && orcids.length > 0) {
				for(int i=0; i < orcids.length; i++) {
					String orcid = normalizeOrcid(orcids[i]);
					if(orcid != null) {
						orcids[i] = orcid;
					}
				}
			}
		}
	}

}
