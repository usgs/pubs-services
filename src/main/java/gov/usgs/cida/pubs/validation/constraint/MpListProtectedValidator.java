package gov.usgs.cida.pubs.validation.constraint;

import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.validation.mp.MpListProtected;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MpListProtectedValidator implements ConstraintValidator<MpListProtected, MpList> {

	private List<String> protectedIds; 

	/** {@inheritDoc}
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(MpListProtected constraintAnnotation) {
		protectedIds = Arrays.asList(constraintAnnotation.protectedIds().split(","));
	}

	/** {@inheritDoc}
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(MpList value, ConstraintValidatorContext context) {
		boolean rtn = true;
		if (null != value 
				&& (null != value.getType() && value.getType().contains("MP_SHARED"))
				|| protectedIds.contains(value.getId().toString())) {
			rtn = false;
		}

		return rtn;
	}

}
