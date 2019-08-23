package gov.usgs.cida.pubs.validation.mp.unique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

/**
 * @author drsteini
 *
 */
public class UniqueKeyValidatorForMpPublicationCostCenter implements ConstraintValidator<UniqueKey, PublicationCostCenter<?>> {

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
	public boolean isValid(PublicationCostCenter<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;
		
		if (null != value && null != context 
				&& null != value.getCostCenter() && null != value.getCostCenter().getId()
				&& null != value.getPublicationId()) {
			Map<String, Object> filters = new HashMap<>();
			filters.put("costCenterId", value.getCostCenter().getId());
			filters.put("publicationId", value.getPublicationId());
			List<MpPublicationCostCenter> pubCostCenters = MpPublicationCostCenter.getDao().getByMap(filters);
			for (MpPublicationCostCenter pubCostCenter : pubCostCenters) {
				if (null == value.getId() || 0 != pubCostCenter.getId().compareTo(value.getId())) {
					rtn = false;
					Object[] messageArguments = new String[]{value.getPublicationId().toString(), value.getCostCenter().getId().toString()};
					String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
				}
			}
		}

		return rtn;
	}

}
