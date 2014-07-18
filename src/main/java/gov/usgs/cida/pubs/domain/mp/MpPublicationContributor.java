package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationContributor;

public class MpPublicationContributor extends PublicationContributor<MpPublicationContributor> {

    private static IMpDao<MpPublicationContributor> mpPublicationContributorDao;

    /**
     * @return the mpPublicationContributorDao
     */
    public static IMpDao<MpPublicationContributor> getDao() {
        return mpPublicationContributorDao;
    }

    /**
     * The setter for mpPublicationContributorDao.
     * @param inmpPublicationContributorDao the mpPublicationContributorDao to set
     */
    public void setMpPublicationContributorDao(final IMpDao<MpPublicationContributor> inMpPublicationContributorDao) {
        mpPublicationContributorDao = inMpPublicationContributorDao;
    }

}
