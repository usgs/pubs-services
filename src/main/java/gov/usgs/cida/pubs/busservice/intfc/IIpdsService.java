package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.jms.MessagePayload;

public interface IIpdsService {

	void processIpdsMessage(MessagePayload messagePayload);

}
