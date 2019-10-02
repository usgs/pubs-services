package gov.usgs.cida.pubs.domain.query;

import java.time.LocalDate;

import javax.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModelProperty;

public class DeletedPublicationFilter extends PagingFilter {

	@PastOrPresent
	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
	@ApiModelProperty(value="Filter to only Publications deleted since this date. For example 2019-01-15")
	private LocalDate deletedSince;

	public DeletedPublicationFilter() {
	}

	public DeletedPublicationFilter(Integer pageNumber,
			Integer pageSize,
			LocalDate deletedSince) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.deletedSince = deletedSince;
	}

	public LocalDate getDeletedSince() {
		return deletedSince;
	}
	public void setDeletedSince(LocalDate deletedSince) {
		this.deletedSince = deletedSince;
	}
	@Override
	public Integer getPageNumber() {
		//only respect given pageNumber if pageSize is provided
		return pageSize != null && pageSize > 0 ? pageNumber : null;
	}
	@Override
	public Integer getPageRowStart() {
		//Null unless both pageSize and pageNumber are given
		Integer workingOffset = null;
		if (pageSize != null && pageSize > 0
				&& getPageNumber() != null && getPageNumber() > 0) {
			workingOffset = (getPageNumber() - 1) * pageSize;
		}
		return workingOffset;
	}
}
