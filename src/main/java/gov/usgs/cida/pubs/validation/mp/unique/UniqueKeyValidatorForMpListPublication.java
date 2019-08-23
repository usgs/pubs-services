package gov.usgs.cida.pubs.validation.mp.unique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

/**
 * @author drsteini
 *
 */
public class UniqueKeyValidatorForMpListPublication implements ConstraintValidator<UniqueKey, MpListPublication> {

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
	public boolean isValid(MpListPublication value, ConstraintValidatorContext context) {
		boolean rtn = true;
		
		if (null != value && null != context 
				&& null != value.getMpList() && null != value.getMpList().getId()
				&& null != value.getMpPublication() && null != value.getMpPublication().getId()) {
			Map<String, Object> filters = new HashMap<>();
			filters.put("mpListId", value.getMpList().getId());
			filters.put("publicationId", value.getMpPublication().getId());
			List<MpListPublication> listPubs = MpListPublication.getDao().getByMap(filters);
			for (MpListPublication listPub : listPubs) {
				if (null == value.getId() || 0 != listPub.getId().compareTo(value.getId())) {
					rtn = false;
					Object[] messageArguments = new String[] {value.getMpList().getId().toString(), listPub.getMpPublication().getId().toString()};
					String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation();
				}
			}
		}

		return rtn;
	}

}
