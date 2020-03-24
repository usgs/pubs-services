package gov.usgs.cida.pubs.domain.query;

public class MpPublicationFilterParams extends PublicationFilterParams {
	private String global;

	public String getGlobal() {
		return global == null ? "true" : global;
	}
	public void setGlobal(String global) {
		this.global = global;
	}

	@Override
	public String toString() {
		return super.toString()
				+ " MpPublicationFilterParams [global=" + getGlobal() + "]";
	}
}
