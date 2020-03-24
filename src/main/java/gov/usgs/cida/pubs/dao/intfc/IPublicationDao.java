package gov.usgs.cida.pubs.dao.intfc;

import java.util.List;
import java.util.Map;

import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.query.IFilterParams;

public interface IPublicationDao extends IDao<Publication<?>> {

	List<Publication<?>> filterByIndexId(String indexId);

	List<Publication<?>> validateByMap(Map<String, Object> filters);

	List<Publication<?>> getByFilter(IFilterParams filters);

	Integer getCountByFilter(IFilterParams filters);

	Integer getSeriesCount(Integer seriesId);

}
