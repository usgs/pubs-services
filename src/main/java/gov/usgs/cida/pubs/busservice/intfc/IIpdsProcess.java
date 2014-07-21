package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.domain.ProcessType;

public interface IIpdsProcess {

    String processLog(final ProcessType inProcessType, final int logId) throws Exception;

}
