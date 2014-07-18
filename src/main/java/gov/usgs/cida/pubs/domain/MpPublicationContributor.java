package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;

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
