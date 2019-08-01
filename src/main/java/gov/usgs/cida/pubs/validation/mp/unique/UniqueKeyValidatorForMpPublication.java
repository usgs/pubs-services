package gov.usgs.cida.pubs.validation.mp.unique;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

/**
 * @author drsteini
 *
 */
public class UniqueKeyValidatorForMpPublication implements ConstraintValidator<UniqueKey, Publication<?>> {

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
	public boolean isValid(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != context) {

			if (null != value.getIndexId()) {
				Map<String, Object> filters = new HashMap<String,Object>();
				filters.put(PublicationDao.INDEX_ID, new String[] { ((Publication<?>) value).getIndexId() });
				List<Publication<?>> pubs = Publication.getPublicationDao().validateByMap(filters);
				for (Publication<?> pub : pubs) {
					if (null == value.getId() || 0 != pub.getId().compareTo(value.getId())) {
						rtn = false;
						Object[] messageArguments = Arrays.asList(new String[]{"indexId " + value.getIndexId(), pub.getId().toString()}).toArray();
						String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
						context.disableDefaultConstraintViolation();
						context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode(PublicationDao.INDEX_ID).addConstraintViolation();
					}
				}
			}

			if (null != value.getIpdsId()) {
				Map<String, Object> filters = new HashMap<String,Object>();
				filters.put(PublicationDao.IPDS_ID, new String[] { ((Publication<?>) value).getIpdsId() });
				List<Publication<?>> pubs = Publication.getPublicationDao().validateByMap(filters);
				for (Publication<?> pub : pubs) {
					if (null == value.getId() || 0 != pub.getId().compareTo(value.getId())) {
						rtn = false;
						Object[] messageArguments = Arrays.asList(new String[]{"ipdsId " + value.getIpdsId(), pub.getId().toString()}).toArray();
						String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
						context.disableDefaultConstraintViolation();
						context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode(PublicationDao.IPDS_ID).addConstraintViolation();
					}
				}
			}
		}

		return rtn;
	}

}
