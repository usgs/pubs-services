package gov.usgs.cida.pubs.domain;

public final class PublicationSubtypeHelper {
	private PublicationSubtypeHelper() {
	}

	public static final PublicationSubtype FOUR = new PublicationSubtype();
	static {
		FOUR.setId(4);
		FOUR.setPublicationType(PublicationTypeHelper.EIGHTEEN);
		FOUR.setText("Other Government Series");
	}

	public static final PublicationSubtype FIVE = new PublicationSubtype();
	static {
		FIVE.setId(5);
		FIVE.setPublicationType(PublicationTypeHelper.EIGHTEEN);
		FIVE.setText("USGS Numbered Series");
	}

	public static final PublicationSubtype TEN = new PublicationSubtype();
	static {
		TEN.setId(10);
		TEN.setPublicationType(PublicationTypeHelper.TWO);
		TEN.setText("Journal Article");
	}

	public static final PublicationSubtype THIRTEEN = new PublicationSubtype();
	static {
		THIRTEEN.setId(13);
		THIRTEEN.setPublicationType(PublicationTypeHelper.FOUR);
		THIRTEEN.setText("Handbook");
	}

}
