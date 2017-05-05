package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.mp.MpPublication;

public interface ICrossRefBusService {

	void submitCrossRef(MpPublication mpPublication);
	
}
