package gov.usgs.cida.pubs.domain;

public final class CostCenterHelper {
	private CostCenterHelper() {
	}

	public static final CostCenter UPPER_MIDWEST_WSC = new CostCenter();
	static {
		UPPER_MIDWEST_WSC.setId(37947);
		UPPER_MIDWEST_WSC.setText("Upper Midwest Water Science Center");
		UPPER_MIDWEST_WSC.setIpdsId(207);
	}

	public static final CostCenter VOLCANO_HAZARDS_PROGRAM = new CostCenter();
	static {
		VOLCANO_HAZARDS_PROGRAM.setId(615);
		VOLCANO_HAZARDS_PROGRAM.setText("Volcano Hazards Program");
		VOLCANO_HAZARDS_PROGRAM.setIpdsId(160);
	}

	public static final CostCenter AFFILIATION_COST_CENTER_1 = new CostCenter();
	static {
		AFFILIATION_COST_CENTER_1.setId(1);
		AFFILIATION_COST_CENTER_1.setText("Affiliation Cost Center 1");
	}
}
