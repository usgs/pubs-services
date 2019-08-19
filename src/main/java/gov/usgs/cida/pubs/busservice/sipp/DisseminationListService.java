package gov.usgs.cida.pubs.busservice.sipp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.busservice.intfc.ISippProcess;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.domain.sipp.IpdsBureauApproval;
import gov.usgs.cida.pubs.domain.sipp.ProcessSummary;
import gov.usgs.cida.pubs.domain.sipp.SippProcessLog;

@Service
public class DisseminationListService {
	private static final Logger LOG = LoggerFactory.getLogger(DisseminationListService.class);

	private final ISippProcess sippProcess;

	@Autowired
	DisseminationListService(final ISippProcess sippProcess) {
		this.sippProcess = sippProcess;
	}

	@Transactional
	public void processDisseminationList(final int daysLastDisseminated) {
		SippProcessLog sippProcessLog = logProcessStart();

		int additions = 0;
		int errors = 0;
		StringBuilder processingDetails = new StringBuilder();

		List<IpdsBureauApproval> ipdsBureauApprovals = IpdsBureauApproval.getDao().getIpdsBureauApprovals(daysLastDisseminated);

		try {
			for (IpdsBureauApproval ipdsBureauApproval : ipdsBureauApprovals) {
				ProcessSummary processSummary = sippProcess.processInformationProduct(ProcessType.DISSEMINATION, ipdsBureauApproval.getIpNumber());
				processingDetails.append(processSummary.getProcessingDetails());
				additions = additions + processSummary.getAdditions();
				errors = errors + processSummary.getErrors();
			}
		} catch (Exception e) {
			LOG.info(e.getMessage());
			processingDetails.append("\n\t").append(e.getMessage());
			errors = errors + 1;
		} finally {
			logProcessEnd(sippProcessLog, ipdsBureauApprovals.size(), additions, errors, processingDetails);
		}
	}

	protected SippProcessLog logProcessStart() {
		SippProcessLog sippProcessLog = new SippProcessLog();
		sippProcessLog.setProcessType(ProcessType.DISSEMINATION);
		sippProcessLog.setId(SippProcessLog.getDao().add(sippProcessLog));
		return sippProcessLog;
	}

	protected void logProcessEnd(SippProcessLog sippProcessLog, int totalEntries, int additions, int errors, StringBuilder processingDetails) {
		sippProcessLog.setTotalEntries(totalEntries);
		sippProcessLog.setPublicationsAdded(additions);
		sippProcessLog.setErrorsEncountered(errors);
		sippProcessLog.setProcessingDetails(processingDetails.toString());
		SippProcessLog.getDao().update(sippProcessLog);
	}
}
