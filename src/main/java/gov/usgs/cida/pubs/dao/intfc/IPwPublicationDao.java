package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.pw.PwPublication;
import java.util.List;
import java.util.Map;

public interface IPwPublicationDao extends IDao<PwPublication>, IStreamingDao<PwPublication>, IPurgeDao<PwPublication> {

	/** 
	 * Get a publication by it's index id.
	 * @param indexId - index id of the object to retrieve. 
	 * @return the domain object.
	 */
	PwPublication getByIndexId(String indexId);

	/** 
	 * Get a publication by it's ipds id.
	 * @param ipdsId - ipds id of the object to retrieve. 
	 * @return the domain object.
	 */
	PwPublication getByIpdsId(String ipdsId);

	/**
	 * Get publications eligible for conversion to Crossref
	 * @param filters
	 * @return eligible publications
	 */
	public List<PwPublication> getCrossrefPublications(Map<String, Object> filters);

	/**
	 * Get a list of publications related to the given id.
	 * @param publicationId
	 * @return List of related publications (metadata only)
	 */
	List<Map<String, Object>> getRelatedPublications(Integer publicationId);
}
