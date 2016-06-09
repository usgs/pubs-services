package gov.usgs.cida.pubs.validation.unique;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.cida.pubs.dao.typehandler.StringBooleanTypeHandler;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

public class UniqueKeyValidatorForPublicationSeries implements ConstraintValidator<UniqueKey, PublicationSeries> {

	public static final String ID = "ID";
	public static final String NAME_MATCH = "NAME_MATCH";
	public static final String CODE_MATCH = "CODE_MATCH";
	public static final String DOI_NAME_MATCH = "DOI_NAME_MATCH";
	public static final String PRINT_ISSN_MATCH = "PRINT_ISSN_MATCH";
	public static final String ONLINE_ISSN_MATCH = "ONLINE_ISSN_MATCH";

	@Override
	public void initialize(UniqueKey constraintAnnotation) {
		//Nothing to do here.
		
	}

	@Override
	public boolean isValid(PublicationSeries value, ConstraintValidatorContext context) {
		boolean rtn = true;

		if (null != value && null != context) {
			Map<BigDecimal, Map<String, Object>> dups = PublicationSeries.getDao().uniqueCheck(value);
				//The dao will always return an object, so dups will never be null, it could be empty, but never null.
				for (Map<String, Object> series : dups.values()) {
					rtn = false;
					context.disableDefaultConstraintViolation();
					if (series.containsKey(NAME_MATCH) && StringBooleanTypeHandler.TRUE.equalsIgnoreCase(series.get(NAME_MATCH).toString())) {
						Object[] messageArguments = Arrays.asList(new String[]{"Name " + value.getText(), series.get(ID).toString()}).toArray();
						String errorMsg = PubsUtilities.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
						context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("text").addConstraintViolation();
					}
					if (series.containsKey(CODE_MATCH) && StringBooleanTypeHandler.TRUE.equalsIgnoreCase(series.get(CODE_MATCH).toString())) {
						Object[] messageArguments = Arrays.asList(new String[]{"Code " + value.getCode(), series.get(ID).toString()}).toArray();
						String errorMsg = PubsUtilities.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
						context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("code").addConstraintViolation();
					}
					if (series.containsKey(DOI_NAME_MATCH) && StringBooleanTypeHandler.TRUE.equalsIgnoreCase(series.get(DOI_NAME_MATCH).toString())) {
						Object[] messageArguments = Arrays.asList(new String[]{"DOI Name " + value.getSeriesDoiName(), series.get(ID).toString()}).toArray();
						String errorMsg = PubsUtilities.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
						context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("seriesDoiName").addConstraintViolation();
					}
					if (series.containsKey(PRINT_ISSN_MATCH) && StringBooleanTypeHandler.TRUE.equalsIgnoreCase(series.get(PRINT_ISSN_MATCH).toString())) {
						Object[] messageArguments = Arrays.asList(new String[]{"Print ISSN " + value.getPrintIssn(), series.get(ID).toString()}).toArray();
						String errorMsg = PubsUtilities.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
						context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("printIssn").addConstraintViolation();
					}
					if (series.containsKey(ONLINE_ISSN_MATCH) && StringBooleanTypeHandler.TRUE.equalsIgnoreCase(series.get(ONLINE_ISSN_MATCH).toString())) {
						Object[] messageArguments = Arrays.asList(new String[]{"Online ISSN " + value.getOnlineIssn(), series.get(ID).toString()}).toArray();
						String errorMsg = PubsUtilities.buildErrorMsg(context.getDefaultConstraintMessageTemplate(), messageArguments); 
						context.buildConstraintViolationWithTemplate(errorMsg).addPropertyNode("onlineIssn").addConstraintViolation();
					}
				}
			}
	
		return rtn;
	}

}
