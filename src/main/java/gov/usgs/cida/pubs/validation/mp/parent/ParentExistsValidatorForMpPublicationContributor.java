package gov.usgs.cida.pubs.validation.mp.parent;

import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ParentExistsValidatorForMpPublicationContributor implements ConstraintValidator<ParentExists, PublicationContributor<?>> {

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
	public boolean isValid(PublicationContributor<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != context) {
			if (null != value.getPublicationId()
					&& null == MpPublication.getDao().getById(value.getPublicationId())) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode("publicationId").addConstraintViolation();
			}

			if (null == value.getContributor()
					|| null == value.getContributor().getId() 
					|| null == Contributor.getDao().getById(value.getContributor().getId())) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode("contributor").addConstraintViolation();
			}

			if (null == value.getContributorType()
					|| null == value.getContributorType().getId()
					|| null == ContributorType.getDao().getById(value.getContributorType().getId())) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode("contributorType").addConstraintViolation();
			}
		}

		return rtn;
	}

}
