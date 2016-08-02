package gov.usgs.cida.pubs.validation.mp.parent;

import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentExistsValidatorForPersonContributor implements ConstraintValidator<ParentExists, PersonContributor<? extends PersonContributor<?>>> {

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
    public boolean isValid(PersonContributor<? extends PersonContributor<?>> value, ConstraintValidatorContext context) {
        boolean rtn = true;

        if (null != value && null != context
        		&& null != value.getAffiliation() && null != value.getAffiliation().getId() 
	        		&& null == Affiliation.getDao().getById(value.getAffiliation().getId())) {
            rtn = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("affiliation").addConstraintViolation();
        }
        
        return rtn;
    }

}
