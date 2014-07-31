package gov.usgs.cida.pubs.domain.ipds;

import gov.usgs.cida.pubs.dao.intfc.IIpdsMessageLogDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.domain.ProcessType;

/**
 * An object for logging the messages received from IPDS.
 * @author drsteini
 *
 */
public class IpdsMessageLog extends BaseDomain <IpdsMessageLog> {

    private static IIpdsMessageLogDao ipdsMessageLogDao;

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
