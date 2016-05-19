package gov.usgs.cida.pubs.domain;

public class Message extends BaseDomain<Message> {
	
	private String message;

	public Message(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}