package gov.usgs.cida.pubs.domain;

public final class PublicationCostCenterHelper {
	private PublicationCostCenterHelper() {
	}

	public final static PublicationCostCenter<?> PSC_VHP = new PublicationCostCenter<>();
	static {
		PSC_VHP.setCostCenter(CostCenterHelper.VOLCANO_HAZARDS_PROGRAM);
	}

	public final static PublicationCostCenter<?> PSC_UMW = new PublicationCostCenter<>();
	static {
		PSC_UMW.setCostCenter(CostCenterHelper.UPPER_MIDWEST_WSC);
	}
}
