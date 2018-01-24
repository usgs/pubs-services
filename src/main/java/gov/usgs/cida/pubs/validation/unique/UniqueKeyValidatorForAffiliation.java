package gov.usgs.cida.pubs.validation.unique;

import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueKeyValidatorForAffiliation implements ConstraintValidator<UniqueKey, Affiliation<?>> {

	/** {@inheritDoc}
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(UniqueKey constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	/** {@inheritDoc}
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Affiliation<?> value, ConstraintValidatorContext context) {
		boolean valid = true;

		if (null != value && null != context) {
			if (null != value.getText()) {
				Map<String, Object> filters = new HashMap<String,Object>();
				filters.put(AffiliationDao.EXACT_SEARCH, value.getText());
				List<? extends Affiliation<?>> affiliations = Affiliation.getDao().getByMap(filters);
				for (Affiliation<?> affiliation : affiliations) {
					if (null == value.getId() || 0 != affiliation.getId().compareTo(value.getId())) {
						valid = false;
						Object[] messageArguments = Arrays.asList(new String[]{value.getText(), affiliation.getId().toString()}).toArray();
						String errorMsg = PubsUtilities.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
						context.disableDefaultConstraintViolation();
						context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode(AffiliationDao.TEXT_SEARCH).addConstraintViolation();
					}
				}
			}
		}

		return valid;
	}
}