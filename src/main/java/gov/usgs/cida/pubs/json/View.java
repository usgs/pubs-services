package gov.usgs.cida.pubs.json;

public class View {

	public interface Base {}

	public interface Lookup extends Base {}

	public interface ManagerGrid extends Base {}

	public interface PWFast extends Base {}

	public interface PW extends PWFast {}

	public interface MP extends PW {}

	public interface LookupMaint extends MP {}
}
