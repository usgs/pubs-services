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

	@Override
	public void initialize(ParentExists constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	@Override
	public boolean isValid(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != context) {
			// This method gives us all possible errors, not just the first one encountered.
			boolean publicationTypeValid = checkPublicationType(value, context);
			boolean publicationSubTypeValid = checkPublicationSubtype(value, context);
			boolean seriesTitleValid = checkSeriesTitle(value, context);
			boolean largerWorkTypeValid = checkLargerWorkType(value, context);
			boolean isPartOfValid = checkIsPartOf(value, context);
			boolean supersededByValid = checkSupersededBy(value, context);
			boolean publishingServiceCenterValid = checkPublishingServiceCenter(value, context);
			rtn = publicationTypeValid && publicationSubTypeValid && seriesTitleValid && largerWorkTypeValid
					&& isPartOfValid && supersededByValid && publishingServiceCenterValid;
		}

		return rtn;
	}

	public boolean checkPublicationType(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;
		if (null != value.getPublicationType() && null != value.getPublicationType().getId()
				&& null == PublicationType.getDao().getById(value.getPublicationType().getId())) {
			rtn = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
				.addPropertyNode("type").addConstraintViolation();
		}
		return rtn;
	}

	public boolean checkPublicationSubtype(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;
		if (null != value.getPublicationSubtype() && null != value.getPublicationSubtype().getId()
				&& null == PublicationSubtype.getDao().getById(value.getPublicationSubtype().getId())) {
			rtn = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
				.addPropertyNode("genre").addConstraintViolation();
		}
		return rtn;
	}

	public boolean checkSeriesTitle(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;
		if (null != value.getSeriesTitle() && null != value.getSeriesTitle().getId()
				&& null == PublicationSeries.getDao().getById(value.getSeriesTitle().getId())) {
			rtn = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
				.addPropertyNode("seriesTitle").addConstraintViolation();
		}
		return rtn;
	}

	public boolean checkLargerWorkType(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;
		if (null != value.getLargerWorkType() && null != value.getLargerWorkType().getId()
				&& null == PublicationType.getDao().getById(value.getLargerWorkType().getId())) {
			rtn = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
				.addPropertyNode("largerWorkType").addConstraintViolation();
		}
		return rtn;
	}

	public boolean checkIsPartOf(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;
		if (null != value.getIsPartOf() && null != value.getIsPartOf().getId()
				&& null == Publication.getPublicationDao().getById(value.getIsPartOf().getId())) {
			rtn = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
				.addPropertyNode("isPartOf").addConstraintViolation();
		}
		return rtn;
	}

	public boolean checkSupersededBy(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;
		if (null != value.getSupersededBy() && null != value.getSupersededBy().getId()
				&& null == Publication.getPublicationDao().getById(value.getSupersededBy().getId())) {
			rtn = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
				.addPropertyNode("supersededBy").addConstraintViolation();
		}
		return rtn;
	}

	public boolean checkPublishingServiceCenter(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;
		if (null != value.getPublishingServiceCenter() && null != value.getPublishingServiceCenter().getId()
				&& null == PublishingServiceCenter.getDao().getById(value.getPublishingServiceCenter().getId())) {
			rtn = false;
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
				.addPropertyNode("publishingServiceCenter").addConstraintViolation();
		}
		return rtn;
	}

}
