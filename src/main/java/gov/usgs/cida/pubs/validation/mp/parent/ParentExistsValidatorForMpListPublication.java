package gov.usgs.cida.pubs.validation.mp.parent;

import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentExistsValidatorForMpListPublication implements ConstraintValidator<ParentExists, MpListPublication> {

    /** {@inheritDoc}
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(ParentExists constraintAnnotation) {
        // Nothing for us to do here at this time.
    }

    /** {@inheritDoc}
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(MpListPublication value, ConstraintValidatorContext context) {
        boolean rtn = true;

        if (null != value && null != context) {
	        if (null != value.getMpPublication() && null != value.getMpPublication().getId() 
	        		&& null == MpPublication.getDao().getById(value.getMpPublication().getId())) {
	            rtn = false;
	            context.disableDefaultConstraintViolation();
	            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
	                .addPropertyNode("publicationId").addConstraintViolation();
	        }
	        
	        if (null != value.getMpList() && null != value.getMpList().getId() 
	        		&& null == MpList.getDao().getById(value.getMpList().getId())) {
	            rtn = false;
	            context.disableDefaultConstraintViolation();
	            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
	                .addPropertyNode("mpList").addConstraintViolation();
	        }
        }

        return rtn;
    }

}
