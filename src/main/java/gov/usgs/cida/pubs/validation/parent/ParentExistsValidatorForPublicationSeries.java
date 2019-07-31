package gov.usgs.cida.pubs.validation.parent;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

public class ParentExistsValidatorForPublicationSeries implements ConstraintValidator<ParentExists, PublicationSeries> {

	@Override
	public void initialize(ParentExists constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	@Override
	public boolean isValid(PublicationSeries value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != value.getPublicationSubtype() && null != context) {
			PublicationSubtype subtype = PublicationSubtype.getDao().getById(value.getPublicationSubtype().getId());
			if (null == subtype) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				Object[] messageArguments = Arrays.asList(new String[]{"Name " + value.getText()}).toArray();
				String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
				context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("id").addConstraintViolation();
			}
		}

		return rtn;
	}

}
