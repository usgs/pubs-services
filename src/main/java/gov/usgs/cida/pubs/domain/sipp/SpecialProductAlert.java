package gov.usgs.cida.pubs.domain.sipp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpecialProductAlert {

	@JsonProperty("IPNumber")
	private String ipNumber;
	@JsonProperty("SpecialProductAlert")
	private String specialProductAlert;
	public String getIpNumber() {
		return ipNumber;
	}
	public void setIpNumber(String ipNumber) {
		this.ipNumber = ipNumber;
	}
	public String getSpecialProductAlert() {
		return specialProductAlert;
	}
	public void setSpecialProductAlert(String specialProductAlert) {
		this.specialProductAlert = specialProductAlert;
	}

}
