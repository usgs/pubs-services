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

    public static final String CONTENTTYPEID = "CONTENTTYPEID";
    public static final String WORKINGTITLE = "WORKINGTITLE";
    public static final String IPNUMBER = "IPNUMBER";
    public static final String SENIORUSGSAUTHORID = "SENIORUSGSAUTHORID";
    public static final String AUTHORSUPERVISORID = "AUTHORSUPERVISORID";
    public static final String PRODUCTTYPEVALUE = "PRODUCTTYPEVALUE";
    public static final String BASISNUMBER = "BASISNUMBER";
    public static final String IPPANUMBER = "IPPANUMBER";
    public static final String PRODUCTSUMMARY = "PRODUCTSUMMARY";
    public static final String PLANNEDDISSEMINATIONDATE = "PLANNEDDISSEMINATIONDATE";
    public static final String TEAMPROJECTNAME = "TEAMPROJECTNAME";
    public static final String COOPERATORS = "COOPERATORS";
    public static final String OMBINFLUENTIALFLAGVALUE = "OMBINFLUENTIALFLAGVALUE";
    public static final String POLICYSENSITIVEFLAGVALUE = "POLICYSENSITIVEFLAGVALUE";
    public static final String HIGHLYVISIBLEFLAGVALUE = "HIGHLYVISIBLEFLAGVALUE";
    public static final String FINALTITLE = "FINALTITLE";
    public static final String ABSTRACT = "ABSTRACT";
    public static final String CITATION = "CITATION";
    public static final String NONUSGSPUBLISHER = "NONUSGSPUBLISHER";
    public static final String SUPERSEDESIPNUMBER = "SUPERSEDESIPNUMBER";
    public static final String NUMBEROFMAPSORPLATES = "NUMBEROFMAPSORPLATES";
    public static final String PAGERANGE = "PAGERANGE";
    public static final String PHYSICALDESCRIPTION = "PHYSICALDESCRIPTION";
    public static final String NUMBEROFUSGSCOPIES = "NUMBEROFUSGSCOPIES";
    public static final String USGSSERIESNUMBER = "USGSSERIESNUMBER";
    public static final String USGSSERIESLETTER = "USGSSERIESLETTER";
    public static final String DIGITALOBJECTIDENTIFIER = "DIGITALOBJECTIDENTIFIER";
    public static final String GPOJACKETNUMBER = "GPOJACKETNUMBER";
    public static final String GPOREQNUMBER = "GPOREQNUMBER";
    public static final String PRINTORDERNUMBER = "PRINTORDERNUMBER";
    public static final String NBROFPSPAGES = "NBROFPSPAGES";
    public static final String NBROFOSPAGES = "NBROFOSPAGES";
    public static final String NBROFCPSPAGES = "NBROFCPSPAGES";
    public static final String NBROFCOSPAGES = "NBROFCOSPAGES";
    public static final String EDITIONNUMBER = "EDITIONNUMBER";
    public static final String VOLUME = "VOLUME";
    public static final String LEGACYDATA = "LEGACYDATA";
    public static final String PRODUCTDISSEMINATED = "PRODUCTDISSEMINATED";
    public static final String DISEMINATIONDATE = "DISEMINATIONDATE";
    public static final String ISSUE = "ISSUE";
    public static final String RELATEDIPNUMBER = "RELATEDIPNUMBER";
    public static final String ISBN = "ISBN";
    public static final String IPDSREVIEWPROCESSSTATEVALUE = "IPDSREVIEWPROCESSSTATEVALUE";
    public static final String IPDSTASKSTATUSVALUE = "IPDSTASKSTATUSVALUE";
    public static final String IPDSWORKFLOWSTARTDATE = "IPDSWORKFLOWSTARTDATE";
    public static final String IPDSWORKFLOWSTOPDATE = "IPDSWORKFLOWSTOPDATE";
    public static final String BUREAUAPPROVINGOFFICIALID = "BUREAUAPPROVINGOFFICIALID";
    public static final String CENTERCHIEFID = "CENTERCHIEFID";
    public static final String PSCCHIEFID = "PSCCHIEFID";
    public static final String VISPECIALISTID = "VISPECIALISTID";
    public static final String PUBLISHEDURL = "PUBLISHEDURL";
    public static final String PRODUCTNUMBER = "PRODUCTNUMBER";
    public static final String COSTCENTERID = "COSTCENTERID";
    public static final String PUBLISHINGSERVICECENTERID = "PUBLISHINGSERVICECENTERID";
    public static final String TEMPWORKFLOW = "TEMPWORKFLOW";
    public static final String CREATEDBYID = "CREATEDBYID";
    public static final String MODIFIEDBYID = "MODIFIEDBYID";
    public static final String MODIFIED = "MODIFIED";
    public static final String CREATED = "CREATED";
    public static final String PRODUCTPRINTINGCOST = "PRODUCTPRINTINGCOST";
    public static final String NBROFINFODELCOPIES = "NBROFINFODELCOPIES";
    public static final String REVIEWTYPEVALUE = "REVIEWTYPEVALUE";
    public static final String USGSMISSIONAREAVALUE = "USGSMISSIONAREAVALUE";
    public static final String USGSREGIONVALUE = "USGSREGIONVALUE";
    public static final String USGSSERIESVALUE = "USGSSERIESVALUE";
    public static final String BINDINGTYPEVALUE = "BINDINGTYPEVALUE";
    public static final String SPNEDITORID = "SPNEDITORID";
    public static final String INFLUENTIAL_OMB_FLAGVALUE = "INFLUENTIAL_OMB_FLAGVALUE";
    public static final String HVISIBLE_FLAGVALUE = "HVISIBLE_FLAGVALUE";
    public static final String SENSITIVE_POLICY_FLAGVALUE = "SENSITIVE_POLICY_FLAGVALUE";
    public static final String JOURNALTITLE = "JOURNALTITLE";
    public static final String PUBS_RESPONSE = "PUBS_RESPONSE";
    public static final String PUBS_STATUS = "PUBS_STATUS";
    public static final String IPDS_INTERNAL_ID = "IPDS_INTERNAL_ID";
    public static final String CONTENTTYPE = "CONTENTTYPE";
    public static final String OWSHIDDENVERSION = "OWSHIDDENVERSION";
    public static final String VERSION = "VERSION";
    public static final String PATH = "PATH";

    public static final Collection<String> IPDS_LOG_PROPERTIES = new ArrayList<>(
            Arrays.asList(CONTENTTYPEID,WORKINGTITLE,IPNUMBER,SENIORUSGSAUTHORID,
                            AUTHORSUPERVISORID,PRODUCTTYPEVALUE,BASISNUMBER,IPPANUMBER,
                            PRODUCTSUMMARY,PLANNEDDISSEMINATIONDATE,TEAMPROJECTNAME,COOPERATORS,
                            OMBINFLUENTIALFLAGVALUE,POLICYSENSITIVEFLAGVALUE,HIGHLYVISIBLEFLAGVALUE,FINALTITLE,
                            ABSTRACT,CITATION,NONUSGSPUBLISHER,SUPERSEDESIPNUMBER,
                            NUMBEROFMAPSORPLATES,PAGERANGE,PHYSICALDESCRIPTION,NUMBEROFUSGSCOPIES,
                            USGSSERIESNUMBER,USGSSERIESLETTER,DIGITALOBJECTIDENTIFIER,GPOJACKETNUMBER,
                            GPOREQNUMBER,PRINTORDERNUMBER,NBROFPSPAGES,NBROFOSPAGES,
                            NBROFCPSPAGES,NBROFCOSPAGES,EDITIONNUMBER,VOLUME,
                            LEGACYDATA,PRODUCTDISSEMINATED,DISEMINATIONDATE,ISSUE,
                            RELATEDIPNUMBER,ISBN,IPDSREVIEWPROCESSSTATEVALUE,IPDSTASKSTATUSVALUE,
                            IPDSWORKFLOWSTARTDATE,IPDSWORKFLOWSTOPDATE,BUREAUAPPROVINGOFFICIALID,CENTERCHIEFID,
                            PSCCHIEFID,VISPECIALISTID,PUBLISHEDURL,PRODUCTNUMBER,
                            COSTCENTERID,PUBLISHINGSERVICECENTERID,TEMPWORKFLOW,CREATEDBYID,
                            MODIFIEDBYID,MODIFIED,CREATED,PRODUCTPRINTINGCOST,
                            NBROFINFODELCOPIES,REVIEWTYPEVALUE,USGSMISSIONAREAVALUE,USGSREGIONVALUE,
                            USGSSERIESVALUE,BINDINGTYPEVALUE,SPNEDITORID,INFLUENTIAL_OMB_FLAGVALUE,
                            HVISIBLE_FLAGVALUE,SENSITIVE_POLICY_FLAGVALUE,JOURNALTITLE,PUBS_RESPONSE,
                            PUBS_STATUS,IPDS_INTERNAL_ID,CONTENTTYPE,OWSHIDDENVERSION,
                            VERSION,PATH));

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
