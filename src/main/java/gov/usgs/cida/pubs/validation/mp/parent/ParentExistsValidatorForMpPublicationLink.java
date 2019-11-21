package gov.usgs.cida.pubs.validation.mp.parent;

import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentExistsValidatorForMpPublicationLink implements ConstraintValidator<ParentExists, PublicationLink<?>> {

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
	public boolean isValid(PublicationLink<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != context) {
			if (null != value.getPublicationId()
					&& null == MpPublication.getDao().getById(value.getPublicationId())) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode("publicationId").addConstraintViolation();
			}
	
			if (null == value.getLinkType()
					|| (null != value.getLinkType()
						&& (null == value.getLinkType().getId()
							|| (null != value.getLinkType().getId()
								&& null == LinkType.getDao().getById(value.getLinkType().getId()))))) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode("linkType").addConstraintViolation();
			}
	
			if (null != value.getLinkFileType() && null != value.getLinkFileType().getId()
					&& null == LinkFileType.getDao().getById(value.getLinkFileType().getId())) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode("linkFileType").addConstraintViolation();
			}
		}
		
		return rtn;
	}

}
