package gov.usgs.cida.pubs.busservice.intfc;

import java.util.List;

import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.query.IFilterParams;


public interface IPublicationBusService {

	Integer getObjectCount(IFilterParams filters);

	List<Publication<?>> getObjects(IFilterParams filters);

}
