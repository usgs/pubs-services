package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.PublicationIndex;

public interface IPublicationIndexDao {

	/** 
	 * Get a domain object by it's unique id.
	 * @param domainID - id of the object to retrieve. 
	 * @return the domain object.
	 */
	PublicationIndex getById(Integer domainID);

	/** 
	 * Publish index to pw.
	 * @param publicationId ID of the publication we are building the index for.
	 */
	void publish(Integer publicationId);

}
