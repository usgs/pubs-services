package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.pw.PwPublication;

public interface IPwPublicationBusService extends IBusService<PwPublication> {

    /** 
     * Get a publication by it's index id.
     * @param indexId - index id of the object to retrieve. 
     * @return the domain object.
     */
	PwPublication getByIndexId(String indexId);

}
