package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.busservice.sipp.ProcessSummary;
import gov.usgs.cida.pubs.domain.ProcessType;

public interface ISippProcess {

	ProcessSummary processPublication(final ProcessType inProcessType, final String ipNumber);

}
