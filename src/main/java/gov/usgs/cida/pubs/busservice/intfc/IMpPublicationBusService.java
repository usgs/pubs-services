package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;

/**
 * @author drsteini
 *
 */
public interface IMpPublicationBusService extends IBusService<MpPublication> {

    /** 
     * Publish the publication identified by the prodId.
     * @param prodId to publish.
     * @return any validation errors that may have prevented the publishing of this citation
     */
    ValidationResults publish(Integer prodId);
}
