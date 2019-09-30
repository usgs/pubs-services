package gov.usgs.cida.pubs.dao.intfc;

import java.util.List;

import gov.usgs.cida.pubs.domain.DeletedPublication;
import gov.usgs.cida.pubs.domain.query.DeletedPublicationFilter;

public interface IDeletedPublicationDao {

	void add(DeletedPublication deletedPublication);

	List<DeletedPublication> getByFilter(DeletedPublicationFilter filter);

	Integer getObjectCount(DeletedPublicationFilter filter);

}
