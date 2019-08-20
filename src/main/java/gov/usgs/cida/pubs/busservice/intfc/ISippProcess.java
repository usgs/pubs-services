package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.sipp.ProcessSummary;

public interface ISippProcess {

	ProcessSummary processInformationProduct(final ProcessType inProcessType, final String ipNumber);

}
