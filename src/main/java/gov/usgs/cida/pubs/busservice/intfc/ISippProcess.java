package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

public interface ISippProcess {

	MpPublication processInformationProduct(final ProcessType inProcessType, final String ipNumber);

}
