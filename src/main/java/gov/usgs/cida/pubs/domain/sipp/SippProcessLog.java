package gov.usgs.cida.pubs.domain.sipp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.domain.ProcessType;

@Component
public class SippProcessLog extends BaseDomain <SippProcessLog> {

	private static IDao<SippProcessLog> sippProcessLogDao;

	private ProcessType processType;
	private Integer totalEntries;
	private Integer publicationsAdded;
	private Integer errorsEncountered;
	private String processingDetails;
	public ProcessType getProcessType() {
		return processType;
	}
	public void setProcessType(final ProcessType inProcessType) {
		processType = inProcessType;
	}
	public Integer getTotalEntries() {
		return totalEntries;
	}
	public void setTotalEntries(Integer totalEntries) {
		this.totalEntries = totalEntries;
	}
	public Integer getPublicationsAdded() {
		return publicationsAdded;
	}
	public void setPublicationsAdded(Integer publicationsAdded) {
		this.publicationsAdded = publicationsAdded;
	}
	public Integer getErrorsEncountered() {
		return errorsEncountered;
	}
	public void setErrorsEncountered(Integer errorsEncountered) {
		this.errorsEncountered = errorsEncountered;
	}
	public String getProcessingDetails() {
		return processingDetails;
	}
	public void setProcessingDetails(final String inProcessingDetails) {
		processingDetails = inProcessingDetails;
	}
	public static IDao<SippProcessLog> getDao() {
		return sippProcessLogDao;
	}
	@Autowired
	@Qualifier("sippProcessLogDao")
	public void setSippProcessLogDao(final IDao<SippProcessLog> inSippProcessLogDao) {
		sippProcessLogDao = inSippProcessLogDao;
	}

}
