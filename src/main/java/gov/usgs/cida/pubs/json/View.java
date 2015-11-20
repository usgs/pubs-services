package gov.usgs.cida.pubs.json;

public class View {

	public interface Base {}

	public interface Lookup extends Base {}
	
	public interface PW extends Lookup {}
	
	public interface MP extends PW {}

}
