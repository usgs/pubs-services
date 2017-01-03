package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.Publication;

import java.util.List;
import java.util.Map;

public interface IPublicationDao extends IDao<Publication<?>> {

	/** 
	 * Get a list of publications filtered by index id.
	 * @param indexId - filtering index id. 
	 * @return the list of matching publications.
	 */
	List<Publication<?>> filterByIndexId(String indexId);

	List<Publication<?>> validateByMap(Map<String, Object> filters);

}
