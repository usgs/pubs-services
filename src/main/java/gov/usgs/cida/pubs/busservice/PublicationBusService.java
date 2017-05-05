package gov.usgs.cida.pubs.busservice;

import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationLink;
import java.util.Collection;
import java.util.Iterator;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

@Service
public class PublicationBusService extends BusService<Publication<?>> implements IPublicationBusService {
	protected final String warehouseEndpoint;
	
	@Autowired
	PublicationBusService(
		@Qualifier("warehouseEndpoint")
		String warehouseEndpoint
	){
		this.warehouseEndpoint = warehouseEndpoint;
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
	public String getIndexPage(Publication<?> pub) {
		String rtn = "";
		if (null != pub) {
			if (null != pub.getLinks()) {
				Collection<PublicationLink<?>> links = pub.getLinks();
				for (Iterator<PublicationLink<?>> linksIter = links.iterator(); linksIter.hasNext();) {
					PublicationLink<?> link = linksIter.next();
					if (null != link.getLinkType() && LinkType.INDEX_PAGE.equals(link.getLinkType().getId())) {
						rtn = link.getUrl();
					}
				}
			}
			if (rtn.isEmpty() && null != pub.getIndexId()) {
				rtn = warehouseEndpoint + "/publication/" + pub.getIndexId();
			}
		}
		return rtn;
	}
	
}
