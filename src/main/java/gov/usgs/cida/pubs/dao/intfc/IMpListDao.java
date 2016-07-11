package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.mp.MpList;

public interface IMpListDao extends IDao<MpList> {

	/** 
	 * Get a mpList by it's ipds_internal_id.
	 * @param ipdsId - ipds_internal_id of the object to retrieve. 
	 * @return the domain object.
	 */
	@Deprecated
	MpList getByIpdsId(Integer ipdsId);

}
