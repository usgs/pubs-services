package gov.usgs.cida.pubs.busservice.sipp;

import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.busservice.intfc.ISippProcess;
import gov.usgs.cida.pubs.domain.ProcessType;

@Service
public class SippProcess implements ISippProcess {

	public ProcessSummary processPublication(final ProcessType inProcessType, final String ipNumber) {
		ProcessSummary rtn = new ProcessSummary();
		StringBuilder processingDetails = new StringBuilder(ipNumber + ":");

		//PUBSTWO-1647 logic goes here

		rtn.setProcessingDetails(processingDetails.append("\n\n").toString());
		return rtn;
	}
}
