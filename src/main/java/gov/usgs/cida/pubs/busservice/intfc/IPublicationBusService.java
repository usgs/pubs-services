package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.Publication;
import java.util.List;
import java.util.Map;


public interface IPublicationBusService {

	String getIndexPage(Publication<?> pub);

	Integer getObjectCount(Map<String, Object> filters);

	List<Publication<?>> getObjects(Map<String, Object> filters);
	
}
