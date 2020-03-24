package gov.usgs.cida.pubs.busservice.intfc;

import java.util.List;

import org.apache.ibatis.session.ResultHandler;

import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.query.IFilterParams;

public interface IPwPublicationBusService {

	PwPublication getObject(Integer objectId);

	PwPublication getByIndexId(String indexId);

	void stream(String statement, IFilterParams filters, ResultHandler<PwPublication> handler);

	List<PwPublication> getObjects(IFilterParams filters);

	Integer getObjectCount(IFilterParams filters);

}
