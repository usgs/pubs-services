package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.json.view.intfc.IMpView;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

public class SearchResults implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("pageSize")
    @JsonView(IMpView.class)
	private String pageSize;
    
    @JsonProperty("pageRowStart")
    @JsonView(IMpView.class)
	private String pageRowStart;
    
    @JsonProperty("recordCount")
    @JsonView(IMpView.class)
	private Integer recordCount;

	@JsonProperty("records")
    @JsonView(IMpView.class)
	private List<? extends BaseDomain<?>> records;
	
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getPageRowStart() {
		return pageRowStart;
	}
	public void setPageRowStart(String pageRowStart) {
		this.pageRowStart = pageRowStart;
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