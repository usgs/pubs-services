package gov.usgs.cida.pubs.busservice.intfc;

import java.util.Map;

import org.apache.ibatis.session.ResultHandler;

import gov.usgs.cida.pubs.domain.pw.PwPublication;

public interface IPwPublicationBusService extends IBusService<PwPublication> {

	PwPublication getByIndexId(String indexId);

	void stream(String statement, Map<String, Object> filters, ResultHandler<PwPublication> handler);

}
