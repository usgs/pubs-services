package gov.usgs.cida.pubs.busservice.sipp;

public class ProcessSummary {

	private String processingDetails;
	private int additions;
	private int errors;
	public String getProcessingDetails() {
		return processingDetails;
	}
	public void setProcessingDetails(String processingDetails) {
		this.processingDetails = processingDetails;
	}
	public int getAdditions() {
		return additions;
	}
	public void setAdditions(int additions) {
		this.additions = additions;
	}
	public int getErrors() {
		return errors;
	}
	public void setErrors(int errors) {
		this.errors = errors;
	}

}
