package gov.usgs.cida.pubs.domain.query;

public class CrossRefFilterParams implements IFilterParams {
	private int[] subtypeId;

	public int[] getSubtypeId() {
		return subtypeId;
	}
	public void setSubtypeId(int[] subtypeId) {
		this.subtypeId = subtypeId;
	}
}
