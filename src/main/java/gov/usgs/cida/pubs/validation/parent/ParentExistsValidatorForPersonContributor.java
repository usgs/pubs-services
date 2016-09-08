package gov.usgs.cida.pubs.validation.parent;

import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentExistsValidatorForPersonContributor implements ConstraintValidator<ParentExists, PersonContributor<?>> {

	@Override
	public void initialize(ParentExists constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	@Override
	public boolean isValid(PersonContributor<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;
		
		if (null != value && null != context) {
			if (!value.getAffiliations().isEmpty()) {
				for (Affiliation<?> affiliation : value.getAffiliations()) {
					if (rtn && null != affiliation && null != affiliation.getId() && null == Affiliation.getDao().getById(affiliation.getId())) {
						rtn = false;
						context.disableDefaultConstraintViolation();
						context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
						.addPropertyNode("affiliations").addConstraintViolation();
					}
				}
			}
		}
		
		return rtn;
	}
}