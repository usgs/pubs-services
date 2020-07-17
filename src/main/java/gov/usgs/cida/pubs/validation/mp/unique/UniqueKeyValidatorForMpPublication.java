package gov.usgs.cida.pubs.validation.mp.unique;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

public class UniqueKeyValidatorForMpPublication implements ConstraintValidator<UniqueKey, Publication<?>> {

	@Override
	public void initialize(UniqueKey constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	@Override
	public boolean isValid(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != context) {
			boolean indexIdOk = checkIndexId(value, context);
			boolean ipdsIdOk = checkIpdsId(value, context);
			rtn = indexIdOk && ipdsIdOk;
		}

		return rtn;
	}

	protected boolean checkIndexId(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value.getIndexId()) {
			Map<String, Object> filters = new HashMap<String,Object>();
			filters.put(PublicationDao.INDEX_ID, new String[] { ((Publication<?>) value).getIndexId() });
			List<Map<?,?>> pubs = Publication.getPublicationDao().validateByMap(filters);
			for (Map<?,?> pub : pubs) {
				Integer publicationId = (Integer) pub.get(PublicationDao.PUBLICATION_ID);
				if (null == value.getId() || 0 != publicationId.compareTo(value.getId())) {
					rtn = false;
					Object[] messageArguments = new String[] {"indexId " + value.getIndexId(), publicationId.toString()};
					String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode(PublicationDao.INDEX_ID).addConstraintViolation();
				}
			}
		}

		return rtn;
	}

	protected boolean checkIpdsId(Publication<?> value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value.getIpdsId()) {
			Map<String, Object> filters = new HashMap<String,Object>();
			filters.put(PublicationDao.IPDS_ID, new String[] { ((Publication<?>) value).getIpdsId() });
			List<Map<?,?>> pubs = Publication.getPublicationDao().validateByMap(filters);
			for (Map<?,?> pub : pubs) {
				Integer publicationId = (Integer) pub.get(PublicationDao.PUBLICATION_ID);
				if (null == value.getId() || 0 != publicationId.compareTo(value.getId())) {
					rtn = false;
					Object[] messageArguments = new String[] {"ipdsId " + value.getIpdsId(), publicationId.toString()};
					String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode(PublicationDao.IPDS_ID).addConstraintViolation();
				}
			}
		}

		return rtn;
	}

}
