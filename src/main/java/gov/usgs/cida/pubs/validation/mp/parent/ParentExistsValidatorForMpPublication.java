package gov.usgs.cida.pubs.validation.mp.parent;

import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
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

			if (null != value.getIsPartOf() && null != value.getIsPartOf().getId()
					&& null == Publication.getPublicationDao().getById(value.getIsPartOf().getId())) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode("isPartOf").addConstraintViolation();
			}

			if (null != value.getSupersededBy() && null != value.getSupersededBy().getId()
					&& null == Publication.getPublicationDao().getById(value.getSupersededBy().getId())) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode("supersededBy").addConstraintViolation();
			}

			if (null != value.getPublishingServiceCenter() && null != value.getPublishingServiceCenter().getId()
					&& null == PublishingServiceCenter.getDao().getById(value.getPublishingServiceCenter().getId())) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
					.addPropertyNode("publishingServiceCenter").addConstraintViolation();
			}
		}

		return rtn;
	}

}
