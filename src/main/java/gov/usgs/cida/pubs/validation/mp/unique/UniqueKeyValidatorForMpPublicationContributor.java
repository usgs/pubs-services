package gov.usgs.cida.pubs.validation.mp.unique;

import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author drsteini
 *
 */
public class UniqueKeyValidatorForMpPublicationContributor implements ConstraintValidator<UniqueKey, PublicationContributor<?>> {

	/** {@inheritDoc}
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(UniqueKey constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	/** {@inheritDoc}
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(PublicationContributor<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != context 
				&& null != value.getContributorType() && null != value.getContributorType().getId()
				&& null != value.getContributor() && null != value.getContributor().getId()
				&& null != value.getPublicationId()) {
			Map<String, Object> filters = new HashMap<>();
			filters.put("contributorTypeId", value.getContributorType().getId());
			filters.put("contributorId", value.getContributor().getId());
			filters.put("publicationId", value.getPublicationId());
			List<MpPublicationContributor> pubContribs = MpPublicationContributor.getDao().getByMap(filters);
			for (MpPublicationContributor pubContrib : pubContribs) {
				if (null == value.getId() || 0 != pubContrib.getId().compareTo(value.getId())) {
					rtn = false;
					Object[] messageArguments = Arrays.asList(new String[]{value.getPublicationId().toString(), value.getContributorType().getId().toString(), pubContrib.getContributor().getId().toString()}).toArray();
					String errorMsg = PubsUtilities.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
				}
			}
		}

		return rtn;
	}

}
