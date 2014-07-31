package gov.usgs.cida.pubs.validation.publication;

import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
//TODO add in all foreign key properties
@SuppressWarnings("rawtypes")
public class ParentExistsValidatorForPublication implements ConstraintValidator<ParentExists, Publication> {

    /** {@inheritDoc}
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(ParentExists constraintAnnotation) {
        // TODO Auto-generated method stub
    }

    /** {@inheritDoc}
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(Publication value, ConstraintValidatorContext context) {
        boolean rtn = true;

        if (null != value.getPublicationType()) {
            if (null == PublicationType.getDao().getById(value.getPublicationType().getId())) {
                rtn = false;
            }
        }

        return rtn;
    }

}
