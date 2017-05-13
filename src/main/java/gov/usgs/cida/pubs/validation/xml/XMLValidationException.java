package gov.usgs.cida.pubs.validation.xml;

public class XMLValidationException extends Exception {
	public XMLValidationException(String message, Throwable throwable) {
		super(message, throwable);
	}
	public XMLValidationException(String message) {
		super(message);
	}
	public XMLValidationException() {
		super();
	}
}
