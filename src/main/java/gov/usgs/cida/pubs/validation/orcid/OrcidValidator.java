package gov.usgs.cida.pubs.validation.orcid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.usgs.cida.pubs.validation.constraint.Orcid;

public class OrcidValidator implements ConstraintValidator<Orcid, String> {
	private static final Logger LOG = LoggerFactory.getLogger(OrcidValidator.class);

	@Override
	public void initialize(Orcid parameters) {
		// Nothing for us to do here at this time.
	}

	@Override
	public boolean isValid(String orcid, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != orcid && null != context) {
			try {
				String baseDigits = orcid.replace("-", "").substring(0, 15);
				String lastDigit = orcid.substring(18);
				String checkDigit = generateCheckDigit(baseDigits);
				LOG.debug("orcid: " + orcid + " baseDigits: " + baseDigits + " lastDigit: " + lastDigit + " checkDigit:" + checkDigit);
				if (!lastDigit.contentEquals(checkDigit)) {
					rtn = false;
				}
			} catch (Exception e) {
				//any exception should just flag as invalid - the @Pattern validation will give the 
				//format error messages
				rtn = false;
			}
		}

		return rtn;
	}

	/**
	 * 
	 * From https://support.orcid.org/hc/en-us/articles/360006897674-Structure-of-the-ORCID-Identifier
	 */
	public static String generateCheckDigit(String baseDigits) {
		int total = 0;
		for (int i = 0; i < baseDigits.length(); i++) {
			Integer digit = Character.getNumericValue(baseDigits.charAt(i));
			total = (total + digit) * 2;
			LOG.debug("digit: " + digit + " total:" + total);
		}
		LOG.debug("total: " + total);
		int remainder = total % 11;
		LOG.debug("remainder: " + remainder);
		int result = (12 - remainder) % 11;
		LOG.debug("result" + result);
		return result == 10 ? "X" : String.valueOf(result);
	}
}
