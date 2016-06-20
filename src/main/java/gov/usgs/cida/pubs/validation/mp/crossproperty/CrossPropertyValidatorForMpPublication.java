package gov.usgs.cida.pubs.validation.mp.crossproperty;

import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.constraint.CrossProperty;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

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

		if (null != value && null != context) {
			if (PubsUtilities.isUsgsNumberedSeries(value.getPublicationSubtype())
					&& (null == value.getSeriesTitle() || null == value.getSeriesTitle().getId())) {
				// USGS Numbered Series must have a Series Title - validity of the title is verified in ParentExistsValidatorForMpPublication
				rtn = false;
				Object[] messageArguments = Arrays.asList(new String[]{"USGS Numbered Series", "a Series Title"}).toArray();
				String errorMsg = PubsUtilities.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
			}

			if (StringUtils.isNotEmpty(value.getPublicationYear()) && value.isNoYear()) {
				rtn = false;
				Object[] messageArguments = Arrays.asList(new String[]{"Publication Year", "No Year=false"}).toArray();
				String errorMsg = PubsUtilities.buildErrorMsg("{pubs.eitheror.error}", messageArguments); 
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
			}

			if (StringUtils.isEmpty(value.getPublicationYear()) && !value.isNoYear()) {
				rtn = false;
				Object[] messageArguments = Arrays.asList(new String[]{"Publication Year", "No Year=true"}).toArray();
				String errorMsg = PubsUtilities.buildErrorMsg("{pubs.eitheror.error}", messageArguments); 
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
			}
		}

		return rtn;
	}

}
