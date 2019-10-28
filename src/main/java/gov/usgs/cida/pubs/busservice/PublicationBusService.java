package gov.usgs.cida.pubs.busservice;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.domain.Publication;

@Service
public class PublicationBusService extends BusService<Publication<?>> implements IPublicationBusService {
	protected final ConfigurationService configurationService;

	@Autowired
	PublicationBusService(ConfigurationService configurationService){
		this.configurationService = configurationService;
	}
	
	@Override
	public List<Publication<?>> getObjects(Map<String, Object> filters) {
		return Publication.getPublicationDao().getByMap(filters);
	}

	@Override
	public Integer getObjectCount(Map<String, Object> filters) {
		return Publication.getPublicationDao().getObjectCount(filters);
	}

	@Override
	public String getWarehousePage(Publication<?> pub) {
		String rtn = "";
		if(pub != null && pub.getIndexId() != null) {
			rtn = configurationService.getWarehouseEndpoint() + "/publication/" + pub.getIndexId();
		}
		return rtn;
	}

}
