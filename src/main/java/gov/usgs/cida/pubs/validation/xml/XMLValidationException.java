package gov.usgs.cida.pubs.validation.xml;

public class XMLValidationException extends Exception {
	private static final long serialVersionUID = -2999761830115476654L;
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
