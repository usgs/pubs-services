package gov.usgs.cida.pubs.busservice.intfc;

/**
 * @author drsteini
 *
 */
public interface IIpdsService<D> {

    /** 
     * Let's place the message into the appropriate locations.
     * @throws Exception 
     */
    void processIpdsMessage(D ipdsMessage) throws Exception;

}
