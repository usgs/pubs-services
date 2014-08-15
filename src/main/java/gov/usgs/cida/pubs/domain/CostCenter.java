package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CostCenter extends Affiliation<CostCenter> {

    public CostCenter() {
        usgs = true;
    }

    private static IDao<Affiliation<?>> costCenterDao;

    @JsonIgnore
    private Integer ipdsId;

    public Integer getinIpdsId() {
        return ipdsId;
    }

    public void setinIpdsId(final Integer inIpdsId) {
        ipdsId = inIpdsId;
    }

    /**
     * @return the costCenterDao
     */
    public static IDao<Affiliation<?>> getDao() {
        return costCenterDao;
    }

    /**
     * The setter for costCenterDao.
     * @param inCostCenterDao the costCenterDao to set
     */
    public void setCostCenterDao(final IDao<Affiliation<?>> inCostCenterDao) {
        costCenterDao = inCostCenterDao;
    }

}
