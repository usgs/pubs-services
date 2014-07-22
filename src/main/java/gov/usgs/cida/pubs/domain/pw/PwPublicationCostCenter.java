package gov.usgs.cida.pubs.domain.pw;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;

public class PwPublicationCostCenter extends PublicationCostCenter<PwPublicationCostCenter>{

    private static IMpDao<PwPublicationCostCenter> pwPublicationCostCenterDao;

    /**
     * @return the pwPublicationCostCenterDao
     */
    public static IMpDao<PwPublicationCostCenter> getDao() {
        return pwPublicationCostCenterDao;
    }

    /**
     * The setter for pwPublicationCostCenterDao.
     * @param inPwPublicationCostCenterDao the pwPublicationCostCenterDao to set
     */
    public void setPwPublicationCostCenterDao(final IMpDao<PwPublicationCostCenter> inPwPublicationCostCenterDao) {
        pwPublicationCostCenterDao = inPwPublicationCostCenterDao;
    }

}
