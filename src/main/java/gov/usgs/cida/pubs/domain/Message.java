package gov.usgs.cida.pubs.domain;

import java.util.List;

import gov.usgs.cida.pubs.json.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

public class Message extends SearchResults {
	
	private static final long serialVersionUID = -867082634153177148L;
	
	private String message;

	public Message(String message) {
		this.message = message;
	}

	@JsonProperty("message")
    @JsonView(View.PW.class)
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@JsonIgnore
	@Override
	public String getPageNumber() {
		return super.getPageNumber();
	}
	
	@JsonIgnore
	@Override
	public String getPageSize() {
		return super.getPageSize();
	}
	
	@JsonIgnore
	@Override
	public String getPageRowStart() {
		return super.getPageNumber();
	}
	
	@JsonIgnore
	@Override
	public Integer getRecordCount() {
		return super.getRecordCount();
	}
	
	@JsonIgnore
	@Override
	public List<? extends BaseDomain<?>> getRecords() {
		return super.getRecords();
	}
}