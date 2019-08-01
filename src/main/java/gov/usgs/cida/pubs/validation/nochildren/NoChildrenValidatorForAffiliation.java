package gov.usgs.cida.pubs.validation.nochildren;

import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.constraint.NoChildren;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoChildrenValidatorForAffiliation implements ConstraintValidator<NoChildren, Affiliation<?>> {

	@Override
	public void initialize(NoChildren constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	@Override
	public boolean isValid(Affiliation<?> value, ConstraintValidatorContext context) {
		boolean valid = true;

		if (null != value && null != context) {
			Map<String, Object> filters = new HashMap<>();
			filters.put(PersonContributorDao.AFFILIATION_ID, value.getId());
			Integer cnt = PersonContributor.getDao().getObjectCount(filters);
			if (0 != cnt) {
				valid = false;
				context.disableDefaultConstraintViolation();
				Object[] messageArguments = Arrays.asList(new String[]{"Name " + value.getText(), cnt.toString()}).toArray();
				String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
				context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("id").addConstraintViolation();
			}
		}
		return valid;
	}
}