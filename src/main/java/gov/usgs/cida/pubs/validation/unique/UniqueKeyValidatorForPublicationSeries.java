package gov.usgs.cida.pubs.validation.unique;

import java.util.Arrays;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.StringUtils;

import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

public class UniqueKeyValidatorForPublicationSeries implements ConstraintValidator<UniqueKey, PublicationSeries> {

	public static final String ID = "id";
	public static final String NAME_MATCH = "name_match";
	public static final String CODE_MATCH = "code_match";
	public static final String DOI_NAME_MATCH = "doi_name_match";
	public static final String PRINT_ISSN_MATCH = "print_issn_match";
	public static final String ONLINE_ISSN_MATCH = "online_issn_match";

	@Override
	public void initialize(UniqueKey constraintAnnotation) {
		//Nothing to do here.
	}

	@Override
	public boolean isValid(PublicationSeries value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != context &&
				(StringUtils.hasText(value.getText()) ||
						StringUtils.hasText(value.getCode()) ||
						StringUtils.hasText(value.getSeriesDoiName()) ||
						StringUtils.hasText(value.getPrintIssn()) ||
						StringUtils.hasText(value.getOnlineIssn())
				)
			) {
			Map<Integer, Map<String, Object>> dups = PublicationSeries.getDao().uniqueCheck(value);
			//The dao will always return an object, so dups will never be null, it could be empty, but never null.
			for (Map<String, Object> series : dups.values()) {
				rtn = false;
				context.disableDefaultConstraintViolation();
				if (checkValue(series, NAME_MATCH)) {
					Object[] messageArguments = Arrays.asList(new String[]{"Name " + value.getText(), series.get(ID).toString()}).toArray();
					String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
					context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("text").addConstraintViolation();
				}
				if (checkValue(series, CODE_MATCH)) {
					Object[] messageArguments = Arrays.asList(new String[]{"Code " + value.getCode(), series.get(ID).toString()}).toArray();
					String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
					context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("code").addConstraintViolation();
				}
				if (checkValue(series, DOI_NAME_MATCH)) {
					Object[] messageArguments = Arrays.asList(new String[]{"DOI Name " + value.getSeriesDoiName(), series.get(ID).toString()}).toArray();
					String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
					context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("seriesDoiName").addConstraintViolation();
				}
				if (checkValue(series, PRINT_ISSN_MATCH)) {
					Object[] messageArguments = Arrays.asList(new String[]{"Print ISSN " + value.getPrintIssn(), series.get(ID).toString()}).toArray();
					String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
					context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("printIssn").addConstraintViolation();
				}
				if (checkValue(series, ONLINE_ISSN_MATCH)) {
					Object[] messageArguments = Arrays.asList(new String[]{"Online ISSN " + value.getOnlineIssn(), series.get(ID).toString()}).toArray();
					String errorMsg = PubsUtils.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
					context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("onlineIssn").addConstraintViolation();
				}
			}
		}
	
		return rtn;
	}

	protected boolean checkValue(Map<String, Object> series, String key) {
		return series.containsKey(key) && series.get(key) instanceof Boolean && (Boolean)series.get(key);
	}
}
