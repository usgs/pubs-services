package gov.usgs.cida.pubs.validation.constraint.norelated;

import java.util.List;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.constraint.NoRelated;

public class NoRelatedValidatorForPwPublication implements ConstraintValidator<NoRelated, PwPublication> {

	@Override
	public void initialize(NoRelated constraintAnnotation) {
		// Nothing for us to do here at this time.
	}

	@Override
	public boolean isValid(PwPublication value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != value.getId() && null != context) {
			List<Map<String, Object>> relatedList = PwPublication.getDao().getRelatedPublications(value.getId());
			if (0 != relatedList.size()) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				Object[] messageArguments = new String[]{value.getIndexId(), getRelatedText(relatedList)};
				String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
				context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("id").addConstraintViolation();
			}
		}

		return rtn;
	}

	protected String getRelatedText(List<Map<String, Object>> relatedList) {
		StringBuilder relatedText = new StringBuilder();
		for (Map<String, Object> related : relatedList) {
			relatedText.append("/nIndex ID: ").append(related.get("index_id"))
				.append(" relationship: ").append(related.get("relation"));
		}
		return relatedText.toString();
	}
}
