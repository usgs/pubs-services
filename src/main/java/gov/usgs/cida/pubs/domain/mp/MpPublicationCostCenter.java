package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;

public class MpPublicationCostCenter extends PublicationCostCenter<MpPublicationCostCenter>{

    private static IMpDao<MpPublicationCostCenter> mpPublicationCostCenterDao;

    /**
     * @return the mpPublicationCostCenterDao
     */
    public static IMpDao<MpPublicationCostCenter> getDao() {
        return mpPublicationCostCenterDao;
    }

    /**
     * The setter for mpPublicationCostCenterDao.
     * @param inMpPublicationCostCenterDao the mpPublicationCostCenterDao to set
     */
    public void setMpPublicationCostCenterDao(final IMpDao<MpPublicationCostCenter> inMpPublicationCostCenterDao) {
        mpPublicationCostCenterDao = inMpPublicationCostCenterDao;
    }

}
