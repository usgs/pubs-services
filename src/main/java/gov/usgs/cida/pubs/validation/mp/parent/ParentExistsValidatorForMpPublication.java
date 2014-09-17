package gov.usgs.cida.pubs.validation.mp.parent;

import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentExistsValidatorForMpPublication implements ConstraintValidator<ParentExists, Publication<?>> {

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
    public boolean isValid(Publication<?> value, ConstraintValidatorContext context) {
        boolean rtn = true;

        if (null != value && null != context) {
	        if (null != value.getPublicationType() && null != value.getPublicationType().getId()
	        		&& null == PublicationType.getDao().getById(value.getPublicationType().getId())) {
	            rtn = false;
	            context.disableDefaultConstraintViolation();
	            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
	                .addPropertyNode("type").addConstraintViolation();
	        }
	        if (null != value.getPublicationSubtype() && null != value.getPublicationSubtype().getId()
	        		&& null == PublicationSubtype.getDao().getById(value.getPublicationSubtype().getId())) {
	            rtn = false;
	            context.disableDefaultConstraintViolation();
	            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
	                .addPropertyNode("genre").addConstraintViolation();
	        }
	        if (null != value.getSeriesTitle() && null != value.getSeriesTitle().getId()
	        		&& null == PublicationSeries.getDao().getById(value.getSeriesTitle().getId())) {
	            rtn = false;
	            context.disableDefaultConstraintViolation();
	            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
	                .addPropertyNode("seriesTitle").addConstraintViolation();
	        }
	        if (null != value.getLargerWorkType() && null != value.getLargerWorkType().getId()
	        		&& null == PublicationType.getDao().getById(value.getLargerWorkType().getId())) {
	            rtn = false;
	            context.disableDefaultConstraintViolation();
	            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
	                .addPropertyNode("largerWorkType").addConstraintViolation();
	        }
        //TODO implement Contact
//        if (null != value.getContact()) {
//            if (null == Contact.getDao().getById(value.getContact().getId())) {
//                rtn = false;
//                context.disableDefaultConstraintViolation();
//                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
//                    .addPropertyNode("contact").addConstraintViolation();
//            }
//        }
        }

        return rtn;
    }

}
