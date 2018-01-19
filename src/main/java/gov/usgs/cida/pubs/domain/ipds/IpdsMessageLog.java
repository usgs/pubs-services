package gov.usgs.cida.pubs.domain.ipds;

import gov.usgs.cida.pubs.dao.intfc.IIpdsMessageLogDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.domain.ProcessType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An object for logging the messages received from IPDS.
 * @author drsteini
 *
 */
@Component
public class IpdsMessageLog extends BaseDomain <IpdsMessageLog> {

	private static IIpdsMessageLogDao ipdsMessageLogDao;

	public static final String ABSTRACT = "ABSTRACT";
	public static final String CITATION = "CITATION";
	public static final String COOPERATORS = "COOPERATORS";
	public static final String COSTCENTER = "COSTCENTER";
	public static final String DIGITALOBJECTIDENTIFIER = "DIGITALOBJECTIDENTIFIER";
	public static final String EDITIONNUMBER = "EDITIONNUMBER";
	public static final String FINALTITLE = "FINALTITLE";
	public static final String IPDS_INTERNAL_ID = "IPDS_INTERNAL_ID";
	public static final String IPNUMBER = "IPNUMBER";
	public static final String ISSUE = "ISSUE";
	public static final String JOURNALTITLE = "JOURNALTITLE";
	public static final String NONUSGSPUBLISHER = "NONUSGSPUBLISHER";
	public static final String PAGERANGE = "PAGERANGE";
	public static final String PHYSICALDESCRIPTION = "PHYSICALDESCRIPTION";
	public static final String PRODUCTSUMMARY = "PRODUCTSUMMARY";
	public static final String PRODUCTTYPEVALUE = "PRODUCTTYPEVALUE";
	public static final String PUBLISHEDURL = "PUBLISHEDURL";
	public static final String PUBLISHINGSERVICECENTER = "PUBLISHINGSERVICECENTER";
	public static final String TASK = "TASK";
	public static final String USGSSERIESLETTER = "USGSSERIESLETTER";
	public static final String USGSSERIESNUMBER = "USGSSERIESNUMBER";
	public static final String USGSSERIESTYPEVALUE = "USGSSERIESTYPEVALUE";
	public static final String VOLUME = "VOLUME";
	public static final String WORKINGTITLE = "WORKINGTITLE";

	public static final Collection<String> IPDS_LOG_PROPERTIES = new ArrayList<>(
			Arrays.asList(ABSTRACT,
					CITATION,
					COSTCENTER,
					COOPERATORS,
					DIGITALOBJECTIDENTIFIER,
					EDITIONNUMBER,
					FINALTITLE,
					IPDS_INTERNAL_ID,
					IPNUMBER,
					ISSUE,
					JOURNALTITLE,
					NONUSGSPUBLISHER,
					PAGERANGE,
					PHYSICALDESCRIPTION,
					PRODUCTSUMMARY,
					PRODUCTTYPEVALUE,
					PUBLISHEDURL,
					PUBLISHINGSERVICECENTER,
					TASK,
					USGSSERIESLETTER,
					USGSSERIESNUMBER,
					USGSSERIESTYPEVALUE,
					VOLUME,
					WORKINGTITLE));

	private String messageText;

	private String processingDetails;

	private Integer prodId;

	private ProcessType processType;

	/**
	 * @return the ipdsMessageLogDao
	 */
	public static IIpdsMessageLogDao getDao() {
		return ipdsMessageLogDao;
	}

	/**
	 * The setter for ipdsMessageLogDao.
	 * @param inIpdsMessageLogDao the IpdsMessageLogDao to set
	 */
	@Autowired
	public void setIpdsMessageLogDao(final IIpdsMessageLogDao inIpdsMessageLogDao) {
		ipdsMessageLogDao = inIpdsMessageLogDao;
	}


	/**
	 * @return the messageText
	 */
	public String getMessageText() {
		return messageText;
	}

	/**
	 * @param inMessageText the messageText to set
	 */
	public void setMessageText(final String inMessageText) {
		messageText = inMessageText;
	}

	public String getProcessingDetails() {
		return processingDetails;
	}

	public void setProcessingDetails(final String inProcessingDetails) {
		processingDetails = inProcessingDetails;
	}

	public Integer getProdId() {
		return prodId;
	}

	public void setProdId(final Integer inProdId) {
		prodId = inProdId;
	}

	public ProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(final ProcessType inProcessType) {
		processType = inProcessType;
	}

}
