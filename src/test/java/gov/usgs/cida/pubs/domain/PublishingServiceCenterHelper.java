package gov.usgs.cida.pubs.domain;

public final class PublishingServiceCenterHelper {
	private PublishingServiceCenterHelper() {
	}

	public static final PublishingServiceCenter FIFTEEN = new PublishingServiceCenter();
	static {
		FIFTEEN.setId(15);
		FIFTEEN.setText("Madison PSC");
	}
}
