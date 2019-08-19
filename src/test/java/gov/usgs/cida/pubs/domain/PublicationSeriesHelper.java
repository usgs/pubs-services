package gov.usgs.cida.pubs.domain;

public final class PublicationSeriesHelper {
	private PublicationSeriesHelper() {
	}

	public static final PublicationSeries THREE_ZERO_NINE = new PublicationSeries();
	static {
		THREE_ZERO_NINE.setId(309);
		THREE_ZERO_NINE.setPublicationSubtype(PublicationSubtypeHelper.FIVE);
		THREE_ZERO_NINE.setText("Coal Map");
		THREE_ZERO_NINE.setCode("COAL");
		THREE_ZERO_NINE.setActive(false);
	}

	public static final PublicationSeries THREE_THIRTY_FOUR = new PublicationSeries();
	static {
		THREE_THIRTY_FOUR.setId(334);
		THREE_THIRTY_FOUR.setPublicationSubtype(PublicationSubtypeHelper.FIVE);
		THREE_THIRTY_FOUR.setText("Scientific Investigations Report");
		THREE_THIRTY_FOUR.setCode("SIR");
		THREE_THIRTY_FOUR.setPrintIssn("2328-031X");
		THREE_THIRTY_FOUR.setOnlineIssn("2328-0328");
		THREE_THIRTY_FOUR.setActive(true);
	}

	public static final PublicationSeries FOUR_FIFTY_TWO = new PublicationSeries();
	static {
		FOUR_FIFTY_TWO.setId(452);
		FOUR_FIFTY_TWO.setPublicationSubtype(PublicationSubtypeHelper.THIRTEEN);
		FOUR_FIFTY_TWO.setText("Unclassified");
	}

	public static final PublicationSeries SEVEN_NINETEEN = new PublicationSeries();
	static {
		SEVEN_NINETEEN.setId(719);
		SEVEN_NINETEEN.setPublicationSubtype(PublicationSubtypeHelper.TEN);
		SEVEN_NINETEEN.setText("American Fishes and U.S. Trout News");
	}
}
