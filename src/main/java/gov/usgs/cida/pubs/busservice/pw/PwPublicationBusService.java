package gov.usgs.cida.pubs.busservice.pw;

import java.util.List;
import java.util.Map;

import gov.usgs.cida.pubs.busservice.BusService;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

public class PwPublicationBusService extends BusService<PwPublication> implements IPwPublicationBusService {

	@Override
	public PwPublication getObject(Integer domainID) {
		return PwPublication.getDao().getById(domainID);
	}
	
	@Override
	public List<PwPublication> getObjects(Map<String, Object> filters) {
		return PwPublication.getDao().getByMap(filters);
	}

	@Override
	public Integer getObjectCount(Map<String, Object> filters) {
		return PwPublication.getDao().getObjectCount(filters);
	}

	@Override
	public PwPublication getByIndexId(String indexId) {
		return PwPublication.getDao().getByIndexId(indexId);
	}

}
