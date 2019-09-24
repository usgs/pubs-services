package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.PublicationSeries;
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
	ValidationResults publish(Integer publicationId);

	/**
	 * Release all publication locks held by the username.
	 * @param lockUsername for which to remove locks.
	 */
	void releaseLocksUser(String lockUsername);

	/**
	 * Release the publication lock.
	 * @param publicationId of the pub to unlock.
	 */
	void releaseLocksPub(Integer publicationId);

	/** 
	 * Get an mpPublication by it's index id.
	 * @param indexId - index id of the object to retrieve. 
	 * @return the domain object.
	 */
	MpPublication getByIndexId(String indexId);

	String getUsgsNumberedSeriesIndexId(final MpPublication pub);

	String getUsgsNumberedSeriesIndexId(
			PublicationSeries series,
			String seriesNumber,
			String chapter,
			String subchapterNumber
			);

	/**
	 * Purge the publication identified by the publicationId from both the manager and warehouse tables.
	 * @param publicationId to purge.
	 * @return any validation errors that may have prevented the purging of this publication
	 */
	ValidationResults purgePublication(final Integer publicationId);
}
