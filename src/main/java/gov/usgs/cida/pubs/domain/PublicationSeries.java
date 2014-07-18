package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author drsteini
 *
 */
public class PublicationSeries extends BaseDomain<PublicationSeries> implements ILookup {

    private static IDao<PublicationSeries> publicationSeriesDao;

    public static final String GENERAL_INFORMATION_PRODUCT = "GIP";

    private PublicationSubtype publicationSubtype;

    private String name;

    private String code;

    private String seriesDoiName;

    private String onlineIssn;

    private String printIssn;

    /**
     * @return the publicationSubtype
     */
    public PublicationSubtype getPublicationSubtype() {
        return publicationSubtype;
    }

    /**
     * @param inPublicationSubtype the publicationSubtype to set
     */
    public void setPublicationSubtype(final PublicationSubtype inPublicationSubtype) {
        publicationSubtype = inPublicationSubtype;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param inName the name to set
     */
    public void setName(final String inName) {
        name = inName;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param inCode the code to set
     */
    public void setCode(final String inCode) {
        code = inCode;
    }

    public String getSeriesDoiName() {
        return seriesDoiName;
    }

    public void setSeriesDoiName(final String inSeriesDoiName) {
        seriesDoiName = inSeriesDoiName;
    }

    public String getOnlineIssn() {
        return onlineIssn;
    }

    public void setOnlineIssn(final String inOnlineIssn) {
        onlineIssn = inOnlineIssn;
    }

    public String getPrintIssn() {
        return printIssn;
    }

    public void setPrintIssn(final String inPrintIssn) {
        printIssn = inPrintIssn;
    }

    /**
     * @return the publicationSeriesDao
     */
    public static IDao<PublicationSeries> getDao() {
        return publicationSeriesDao;
    }

    /**
     * The setter for publicationSeriesDao.
     * @param inPublicationSeriesDao the publicationSeriesDao to set
     */
    public void setPublicationSeriesDao(final IDao<PublicationSeries> inPublicationSeriesDao) {
        publicationSeriesDao = inPublicationSeriesDao;
    }

    @Override
    @JsonView(ILookupView.class)
    public String getText() {
        return name;
    }

    @Override
    @JsonView(ILookupView.class)
    public String getValue() {
        return String.valueOf(id);
    }

}
