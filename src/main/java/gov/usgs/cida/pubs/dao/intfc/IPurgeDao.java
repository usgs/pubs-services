package gov.usgs.cida.pubs.dao.intfc;

public interface IPurgeDao<D> {

	/**
	 * Purge the publication identified by the publicationId from all related tables.
	 * @param publicationId to purge.
	 */
	void purgePublication(final Integer publicationId);

}
