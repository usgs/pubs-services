package gov.usgs.cida.pubs.domain.sipp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class USGSProgram {

	@JsonProperty("IPNumber")
	private String ipNumber;
	@JsonProperty("USGSProgram")
	private String usgsProgram;
	public String getIpNumber() {
		return ipNumber;
	}
	public void setIpNumber(String ipNumber) {
		this.ipNumber = ipNumber;
	}
	public String getUsgsProgram() {
		return usgsProgram;
	}
	public void setUsgsProgram(String usgsProgram) {
		this.usgsProgram = usgsProgram;
	}

}
