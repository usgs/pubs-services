package gov.usgs.cida.pubs.domain.sipp;

public class RollbackException extends RuntimeException {
	private static final long serialVersionUID = -7927963514298329478L;

	public RollbackException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
}
