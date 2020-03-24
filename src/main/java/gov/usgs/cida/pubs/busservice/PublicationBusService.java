package gov.usgs.cida.pubs.busservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.query.IFilterParams;

@Service
public class PublicationBusService implements IPublicationBusService {
	protected final ConfigurationService configurationService;

	@Autowired
	PublicationBusService(ConfigurationService configurationService){
		this.configurationService = configurationService;
	}
	
	@Override
	public List<Publication<?>> getObjects(IFilterParams filters) {
		return Publication.getPublicationDao().getByFilter(filters);
	}

	@Override
	public Integer getObjectCount(IFilterParams filters) {
		return Publication.getPublicationDao().getCountByFilter(filters);
	}

}
