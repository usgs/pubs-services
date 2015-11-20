package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.pw.PwPublication;

public interface IPwPublicationDao extends IDao<PwPublication>, IStreamingDao<PwPublication> {

    /** 
     * Get a publication by it's index id.
     * @param indexId - index id of the object to retrieve. 
     * @return the domain object.
     */
	PwPublication getByIndexId(String indexId);

    /** 
     * Get a publication by it's ipds id.
     * @param ipdsId - ipds id of the object to retrieve. 
     * @return the domain object.
     */
	PwPublication getByIpdsId(String ipdsId);

}
