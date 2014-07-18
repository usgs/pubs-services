package gov.usgs.cida.pubs.domain;

import org.hibernate.validator.constraints.Length;

import gov.usgs.cida.pubs.dao.intfc.IDao;

/**
 * @author drsteini
 *
 */
public class PwPublication extends Publication<PwPublication> {

    private static final long serialVersionUID = 1176886529474726822L;

    private static IDao<PwPublication> pwPublicationDao;

    @Length(min=0, max=1)
    private String webDocFlag;

    @Length(min=0, max=4000)
    private String usgsCitationDisplay;

    @Length(min=0, max=255)
    private String contentsBreakdown;

    @Length(min=0, max=4000)
    private String searchResults;


    public String getWebDocFlag() {
        return webDocFlag;
    }

    public void setWebDocFlag(final String inWebDocFlag) {
        webDocFlag = inWebDocFlag;
    }

    public String getUsgsCitationDisplay() {
        return usgsCitationDisplay;
    }

    public void setUsgsCitationDisplay(final String inUsgsCitationDisplay) {
        usgsCitationDisplay = inUsgsCitationDisplay;
    }

    public String getContentsBreakdown() {
        return contentsBreakdown;
    }

    public void setContentsBreakdown(final String inContentsBreakdown) {
        contentsBreakdown = inContentsBreakdown;
    }

    public String getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(final String inSearchResults) {
        searchResults = inSearchResults;
    }

    /**
     * @return the pwPublicationDao
     */
    public static IDao<PwPublication> getDao() {
        return pwPublicationDao;
    }

    /**
     * The setter for pwPublicationDao.
     * @param inPwPublicationDao the pwPublicationDao to set
     */
    public void setPwPublicationDao(final IDao<PwPublication> inPwPublicationDao) {
        pwPublicationDao = inPwPublicationDao;
    }

}
