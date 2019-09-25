package gov.usgs.cida.pubs.dao.intfc;

import java.util.List;
import java.util.Map;

import gov.usgs.cida.pubs.domain.DeletedPublication;

public interface IDeletedPublicationDao {

	void add(DeletedPublication deletedPublication);

	List<DeletedPublication> getByMap(Map<String, Object> filters);

	Integer getObjectCount(Map<String, Object> filters);

}
