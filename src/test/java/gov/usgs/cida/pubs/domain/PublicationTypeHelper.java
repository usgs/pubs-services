package gov.usgs.cida.pubs.domain;

public final class PublicationTypeHelper {
	private PublicationTypeHelper() {
	}

	public static final PublicationType TWO = new PublicationType();
	static {
		TWO.setId(2);
		TWO.setText("Article");
	}

	public static final PublicationType FOUR = new PublicationType();
	static {
		FOUR.setId(4);
		FOUR.setText("Book");
	}

	public static final PublicationType EIGHTEEN = new PublicationType();
	static {
		EIGHTEEN.setId(18);
		EIGHTEEN.setText("Report");
	}
}
