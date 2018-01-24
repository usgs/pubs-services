package gov.usgs.cida.pubs.jms;

import java.time.LocalDate;

public class MessagePayload {

	private String type;
	private LocalDate asOfDate;
	private LocalDate priorToDate;
	private String context;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public LocalDate getAsOfDate() {
		//Default to today.
		return null == asOfDate ? LocalDate.now() : asOfDate;
	}
	public String getAsOfString() {
		return getAsOfDate().toString();
	}
	public void setAsOfDate(String asOfDate) {
		this.asOfDate = LocalDate.parse(asOfDate);
	}
	public LocalDate getPriorToDate() {
		//Default to tomorrow.
		return null == priorToDate ? LocalDate.now().plusDays(1) : priorToDate;
	}
	public String getPriorToString() {
		return getPriorToDate().toString();
	}
	public void setPriorToDate(String priorToDate) {
		this.priorToDate = LocalDate.parse(priorToDate);
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}

}
