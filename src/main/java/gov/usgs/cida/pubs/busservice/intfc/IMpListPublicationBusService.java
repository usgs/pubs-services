package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;

import java.util.Collection;

public interface IMpListPublicationBusService {

	/** 
	 * Add the publications to the list.
	 * @param listId - ID of the list to add publications to.
	 * @param publicationIds - IDs of the publications to add to the list.
	 * @return - a collection of the added MpListPublications.
	 */
	Collection<MpListPublication> addPubToList(Integer listId, String[] publicationIds);
	
	/** 
	 * Remove the publication from the list. 
	 * @param listId - ID of the list to remove the publication from.
	 * @param publicationId - ID of the publication to remove.
	 * @return - any validation errors encountered during the removal.
	 */
	ValidationResults removePubFromList(Integer listId, Integer publicationId);
}
