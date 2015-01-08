package gov.usgs.cida.pubs.busservice;

import gov.usgs.cida.pubs.domain.Publication;

import java.util.List;
import java.util.Map;

public class PublicationBusService extends BusService<Publication<?>> {

	@Override
    public List<Publication<?>> getObjects(Map<String, Object> filters) {
        return Publication.getPublicationDao().getByMap(filters);
    }

	@Override
	public Integer getObjectCount(Map<String, Object> filters) {
		return Publication.getPublicationDao().getObjectCount(filters);
	}

}
