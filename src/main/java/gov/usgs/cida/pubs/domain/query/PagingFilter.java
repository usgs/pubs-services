package gov.usgs.cida.pubs.domain.query;

import javax.validation.constraints.Positive;

import io.swagger.annotations.ApiModelProperty;

public class PagingFilter {
	@Positive
	@ApiModelProperty(value="Used in conjunction with pageSize to page through the deleted Publications.")
	protected Integer pageNumber;
	@ApiModelProperty(hidden=true)
	protected Integer pageRowStart;
	@Positive
	@ApiModelProperty(value="Used in conjunction with pageNumber to page through the deleted Publications.")
	protected Integer pageSize;

	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Integer getPageRowStart() {
		return pageRowStart;
	}
	public void setPageRowStart(Integer pageRowStart) {
		this.pageRowStart = pageRowStart;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
