package gov.usgs.cida.pubs.validation.nochildren;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.constraint.NoChildren;

public class NoChildrenValidatorForPublicationSeries implements ConstraintValidator<NoChildren, PublicationSeries> {

	@Override
	public void initialize(NoChildren constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	@Override
	public boolean isValid(PublicationSeries value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != context) {
			Map<String, Object> filters = new HashMap<>();
			filters.put(PublicationDao.SERIES_ID_SEARCH, value.getId());
			Integer cnt = Publication.getPublicationDao().getObjectCount(filters);
			if (0 != cnt) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				Object[] messageArguments = Arrays.asList(new String[]{"Name " + value.getText(), cnt.toString()}).toArray();
				String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
				context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("id").addConstraintViolation();
			}
		}

		return rtn;
	}

}
