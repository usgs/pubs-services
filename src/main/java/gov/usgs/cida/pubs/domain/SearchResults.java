package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.json.View;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

public class SearchResults implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("pageSize")
	@JsonView({View.PW.class, View.ManagerGrid.class})
	private String pageSize;

	@JsonProperty("pageRowStart")
	@JsonView({View.PW.class, View.ManagerGrid.class})
	private String pageRowStart;

	@JsonProperty("pageNumber")
	@JsonView({View.PW.class, View.ManagerGrid.class})
	private String pageNumber;

	@JsonProperty("recordCount")
	@JsonView({View.PW.class, View.ManagerGrid.class})
	private Integer recordCount;

	@JsonProperty("records")
	@JsonView({View.PW.class, View.ManagerGrid.class})
	private List<? extends BaseDomain<?>> records;

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = String.valueOf(pageSize);
	}

	public String getPageRowStart() {
		return pageRowStart;
	}

	public void setPageRowStart(Integer pageRowStart) {
		this.pageRowStart = String.valueOf(pageRowStart);
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = String.valueOf(pageNumber);
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

	public List<? extends BaseDomain<?>> getRecords() {
		return records;
	}

	public void setRecords(List<? extends BaseDomain<?>> records) {
		this.records = records;
	}

}
