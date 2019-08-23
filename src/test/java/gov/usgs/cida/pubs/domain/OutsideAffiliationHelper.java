package gov.usgs.cida.pubs.domain;

public final class OutsideAffiliationHelper {
	private OutsideAffiliationHelper() {
	}

	public static final OutsideAffiliation UNIVERSITY_HAWAII_HILO = new OutsideAffiliation();
	static {
		UNIVERSITY_HAWAII_HILO.setId(6977);
		UNIVERSITY_HAWAII_HILO.setText("University of Hawai`i at Hilo");
	}

	public static final OutsideAffiliation UNIVERSITY_WISCONSIN = new OutsideAffiliation();
	static {
		UNIVERSITY_WISCONSIN.setId(7122);
		UNIVERSITY_WISCONSIN.setText("University of Wisconsin");
	}

	public static final OutsideAffiliation OUTER_AFFILIATION_1 = new OutsideAffiliation();
	static {
		OUTER_AFFILIATION_1.setId(1);
		OUTER_AFFILIATION_1.setText("Outer Affiliation 1");
	}
}
