package gov.usgs.cida.pubs.domain;

public final class PublicationIndexHelper {
	private PublicationIndexHelper() {
	}

	public static final String TABLE_NAME = "publication_index";
	public static final String QUERY_TEXT = "select publication_id, q from publication_index";
}
