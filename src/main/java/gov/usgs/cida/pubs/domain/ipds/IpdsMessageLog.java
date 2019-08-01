package gov.usgs.cida.pubs.domain.ipds;

import gov.usgs.cida.pubs.dao.intfc.IIpdsMessageLogDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.domain.ProcessType;
import gov.usgs.cida.pubs.utility.PubsEscapeXML10Utils;

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

	public static final String ABSTRACT = "abstract";
	public static final String CITATION = "citation";
	public static final String COOPERATORS = "cooperators";
	public static final String COSTCENTER = "costcenter";
	public static final String DIGITALOBJECTIDENTIFIER = "digitalobjectidentifier";
	public static final String EDITIONNUMBER = "editionnumber";
	public static final String FINALTITLE = "finaltitle";
	public static final String IPDS_INTERNAL_ID = "ipds_internal_id";
	public static final String IPNUMBER = "ipnumber";
	public static final String ISSUE = "issue";
	public static final String JOURNALTITLE = "journaltitle";
	public static final String NONUSGSPUBLISHER = "nonusgspublisher";
	public static final String PAGERANGE = "pagerange";
	public static final String PHYSICALDESCRIPTION = "physicaldescription";
	public static final String PRODUCTSUMMARY = "productsummary";
	public static final String PRODUCTTYPEVALUE = "producttypevalue";
	public static final String PUBLISHEDURL = "publishedurl";
	public static final String PUBLISHINGSERVICECENTER = "publishingservicecenter";
	public static final String TASK = "task";
	public static final String USGSSERIESLETTER = "usgsseriesletter";
	public static final String USGSSERIESNUMBER = "usgsseriesnumber";
	public static final String USGSSERIESTYPEVALUE = "usgsseriestypevalue";
	public static final String VOLUME = "volume";
	public static final String WORKINGTITLE = "workingtitle";

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
		messageText = PubsEscapeXML10Utils.cleanseXml(inMessageText);
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
