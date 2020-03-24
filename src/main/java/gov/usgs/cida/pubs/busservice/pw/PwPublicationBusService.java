package gov.usgs.cida.pubs.busservice.pw;

import java.util.List;

import org.apache.ibatis.session.ResultHandler;
import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.query.IFilterParams;

@Service
public class PwPublicationBusService implements IPwPublicationBusService {

	@Override
	public PwPublication getObject(Integer domainID) {
		return PwPublication.getDao().getById(domainID);
	}

	@Override
	public Integer getObjectCount(IFilterParams filters) {
		return PwPublication.getDao().getCountByFilter(filters);
	}

	@Override
	public PwPublication getByIndexId(String indexId) {
		return PwPublication.getDao().getByIndexId(indexId);
	}

	@Override
	public void stream(String statement, IFilterParams filters, ResultHandler<PwPublication> handler) {
		PwPublication.getDao().stream(statement, filters, handler);
	}

	@Override
	public List<PwPublication> getObjects(IFilterParams filters) {
		return PwPublication.getDao().getByFilter(filters);
	}

}
