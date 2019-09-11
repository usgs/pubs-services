package gov.usgs.cida.pubs.webservice;

public class PubsInvalidParameterException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4132352105985439757L;

	public PubsInvalidParameterException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public PubsInvalidParameterException(String message) {
		super(message);
	}

	public PubsInvalidParameterException() {
		super();
	}

}