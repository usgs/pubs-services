package gov.usgs.cida.pubs.validation.mp;

import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentExistsValidatorForMpPublicationCostCenter implements ConstraintValidator<ParentExists, PublicationCostCenter<?>> {

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
    public boolean isValid(PublicationCostCenter<?> value, ConstraintValidatorContext context) {
        boolean rtn = true;

        if (null != value.getPublicationId() && null == MpPublication.getDao().getById(value.getPublicationId())) {
            rtn = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("publicationId").addConstraintViolation();
        }
        if (null != value.getCostCenter() && null == CostCenter.getDao().getById(value.getCostCenter().getId())) {
            rtn = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("costCenter").addConstraintViolation();
        }

        return rtn;
    }

}
