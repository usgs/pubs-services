package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;

/**
 * @author drsteini
 *
 */
public interface IMpPublicationBusService extends IBusService<MpPublication> {

	/** 
	 * Check to see if this publication is available for editing (not locked).
	 * @param publicationId of the publication to check.
	 * @return a validator result containing the username of the person holding the lock. null if not locked.
	 */
	ValidatorResult checkAvailability(Integer publicationId);

    /**
     * Publish the publication identified by the prodId.
     * @param publicationId to publish.
     * @return any validation errors that may have prevented the publishing of this citation
     */
    ValidationResults publish(String publicationId);

    /**
     * Release all publication locks held by the username.
     * @param lockUsername for which to remove locks.
     */
    void releaseLocks(String lockUsername);

}
