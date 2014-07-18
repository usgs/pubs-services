package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;

public class MpPublicationCostCenter extends PublicationCostCenter<MpPublicationCostCenter>{

    private static IDao<MpPublicationCostCenter> mpPublicationCostCenterDao;

    /**
     * @return the mpPublicationCostCenterDao
     */
    public static IDao<MpPublicationCostCenter> getDao() {
        return mpPublicationCostCenterDao;
    }

    /**
     * The setter for mpPublicationCostCenterDao.
     * @param inMpPublicationCostCenterDao the mpPublicationCostCenterDao to set
     */
    public void setMpPublicationCostCenterDao(final IDao<MpPublicationCostCenter> inMpPublicationCostCenterDao) {
        mpPublicationCostCenterDao = inMpPublicationCostCenterDao;
    }

}
