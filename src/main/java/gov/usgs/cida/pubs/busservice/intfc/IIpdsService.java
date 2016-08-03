package gov.usgs.cida.pubs.busservice.intfc;

/**
 * @author drsteini
 *
 */
public interface IIpdsService {

	/** 
	 * Let's place the message into the appropriate locations.
	 * @throws Exception 
	 */
	void processIpdsMessage(String ipdsMessage) throws Exception;

}
