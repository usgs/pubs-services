package gov.usgs.cida.pubs.validation.mp.crossproperty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.constraint.CrossProperty;

public class CrossPropertyValidatorForMpPublication implements ConstraintValidator<CrossProperty, Publication<?>> {

	/** {@inheritDoc}
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(CrossProperty constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	/** {@inheritDoc}
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != context
				&& PubsUtils.isUsgsNumberedSeries(value.getPublicationSubtype())
				&& (null == value.getSeriesTitle() || null == value.getSeriesTitle().getId())) {
			// USGS Numbered Series must have a Series Title - validity of the title is verified in ParentExistsValidatorForMpPublication
			rtn = false;
			Object[] messageArguments = new String[] {"USGS Numbered Series", "a Series Title"};
			String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
		}

		return rtn;
	}
}