package gov.usgs.cida.pubs.domain;

public final class ContributorTypeHelper {
	private ContributorTypeHelper() {
	}

	public static final ContributorType AUTHORS = new ContributorType();
	static {
		AUTHORS.setId(1);
		AUTHORS.setText("Authors");
	}

	public static final ContributorType EDITORS = new ContributorType();
	static {
		EDITORS.setId(2);
		EDITORS.setText("Editors");
	}

	public static final ContributorType COMPILERS = new ContributorType();
	static {
		EDITORS.setId(3);
		EDITORS.setText("Compilers");
	}
}
