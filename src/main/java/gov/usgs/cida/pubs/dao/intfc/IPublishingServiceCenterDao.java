package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.PublishingServiceCenter;

public interface IPublishingServiceCenterDao extends IDao<PublishingServiceCenter> {

	/** 
	 * Get a PublishingServiceCenter by it's ipds_internal_id.
	 * @param ipdsId - ipds_internal_id of the object to retrieve. 
	 * @return the domain object.
	 */
	PublishingServiceCenter getByIpdsId(Integer ipdsId);

}
