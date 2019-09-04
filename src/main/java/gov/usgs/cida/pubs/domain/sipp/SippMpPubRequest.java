package gov.usgs.cida.pubs.domain.sipp;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.usgs.cida.pubs.domain.ProcessType;
import io.swagger.annotations.ApiModelProperty;

public class SippMpPubRequest {
	public static final String IPNUMBER_VALIDATION_REGEX = "^" + "IP-\\d{6}" + "$";
	public static final String IPNUMBER_VALIDATION_MESS = "Invalid IPNumber specified ('${validatedValue}') must be 'IP-' followed by 6 digits [0-9].";

	//TODO: generic routine to create the validation regex and message from the enum.
	public static final String PROCESS_TYPE_VALIDATION_REGEX = "(?i)" + "^" + "(DISSEMINATION)|(SPN_PRODUCTION)" + "$";
	public static final String PROCESS_TYPE_VALIDATION_MESS = "Unknown ProcessType specified ('${validatedValue}') must be one of: 'DISSEMINATION', 'SPN_PRODUCTION'";

	@JsonProperty("ProcessType")
	@Pattern(regexp=PROCESS_TYPE_VALIDATION_REGEX, message=PROCESS_TYPE_VALIDATION_MESS)
	private String processType;

	@JsonProperty("IPNumber")
	@Pattern(regexp=IPNUMBER_VALIDATION_REGEX, message=IPNUMBER_VALIDATION_MESS)
	private String ipNumber;

	public String getProcessType() {
		return processType;
	}
	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getIpNumber() {
		return ipNumber;
	}
	public void setIpNumber(String ipNumber) {
		this.ipNumber = ipNumber;
	}
	
	@ApiModelProperty(hidden=true)
	public ProcessType getProcessTypeEnum() {
	    return ProcessType.valueOf(processType.toUpperCase());	
	}

}