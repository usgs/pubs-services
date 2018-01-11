package gov.usgs.cida.pubs.jms;

import java.time.LocalDate;

public class MessagePayload {

	private String type;
	private LocalDate asOfDate;
	private String context;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public LocalDate getAsOfDate() {
		return null == asOfDate ? LocalDate.now() : asOfDate;
	}
	public String getAsOfString() {
		return getAsOfDate().toString();
	}
	public void setAsOfDate(String asOfDate) {
		this.asOfDate = LocalDate.parse(asOfDate);
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}

}
