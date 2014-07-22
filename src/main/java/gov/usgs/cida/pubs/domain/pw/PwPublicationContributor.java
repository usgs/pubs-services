package gov.usgs.cida.pubs.domain.pw;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;

public class PwPublicationContributor extends PublicationContributor<MpPublicationContributor> {

    private static IMpDao<PwPublicationContributor> pwPublicationContributorDao;

    /**
     * @return the pwPublicationContributorDao
     */
    public static IMpDao<PwPublicationContributor> getDao() {
        return pwPublicationContributorDao;
    }

    /**
     * The setter for pwPublicationContributorDao.
     * @param inPwPublicationContributorDao the pwPublicationContributorDao to set
     */
    public void setPwPublicationContributorDao(final IMpDao<PwPublicationContributor> inPwPublicationContributorDao) {
        pwPublicationContributorDao = inPwPublicationContributorDao;
    }

}
