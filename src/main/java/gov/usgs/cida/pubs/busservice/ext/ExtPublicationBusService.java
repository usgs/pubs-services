package gov.usgs.cida.pubs.busservice.ext;

import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.domain.mp.MpPublication;

@Service
public class ExtPublicationBusService {

	public MpPublication create(MpPublication mpPublication, Class<?>... validationGroups) {
		//Stub for next part of ticket.
		return mpPublication;
	}
}
