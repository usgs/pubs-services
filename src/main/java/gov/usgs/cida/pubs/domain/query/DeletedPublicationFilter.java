package gov.usgs.cida.pubs.domain.query;

import java.time.LocalDate;

import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.usgs.cida.pubs.dao.BaseDao;

//Note the paging parameters are not camel case/java like because of the API
public class DeletedPublicationFilter {

	@PastOrPresent
	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
	private LocalDate deletedSince;

	@Positive
	@JsonProperty(BaseDao.PAGE_NUMBER)
	protected Integer page_number;

	@JsonProperty(BaseDao.PAGE_ROW_START)
	protected Integer page_row_start;

	@Positive
	@JsonProperty(BaseDao.PAGE_SIZE)
	protected Integer page_size;

	public DeletedPublicationFilter() {
	}

	public DeletedPublicationFilter(Integer page_number,
			Integer page_size,
			LocalDate deletedSince) {
		this.page_number = page_number;
		this.page_size = page_size;
		this.deletedSince = deletedSince;
	}

	public LocalDate getDeletedSince() {
		return deletedSince;
	}
	public void setDeletedSince(LocalDate deletedSince) {
		this.deletedSince = deletedSince;
	}

	public Integer getPage_number() {
		//only respect given pageNumber if pageSize is provided
		return page_size != null && page_size > 0 ? page_number : null;
	}
	public void setPage_number(Integer page_number) {
		this.page_number = page_number;
	}

	public Integer getPage_row_start() {
		//Null unless both pageSize and pageNumber are given
		Integer workingOffset = null;
		if (page_size != null && page_size > 0
				&& getPage_number() != null && getPage_number() > 0) {
			workingOffset = (getPage_number() - 1) * page_size;
		}
		return workingOffset;
	}
	public void setPage_row_start(Integer page_row_start) {
		this.page_row_start = page_row_start;
	}

	public Integer getPage_size() {
		return page_size;
	}
	public void setPage_size(Integer page_size) {
		this.page_size = page_size;
	}
}
