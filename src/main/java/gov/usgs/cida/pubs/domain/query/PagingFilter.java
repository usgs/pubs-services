package gov.usgs.cida.pubs.domain.query;

public class PagingFilter {

	protected String pageNumber;
	protected String pageRowStart;
	protected String pageSize;

	public String getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getPageRowStart() {
		return pageRowStart;
	}
	public void setPageRowStart(String pageRowStart) {
		this.pageRowStart = pageRowStart;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
}
