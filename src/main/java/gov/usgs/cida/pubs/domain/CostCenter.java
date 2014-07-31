package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

public class CostCenter extends BaseDomain<CostCenter> implements ILookup {

    private static IDao<CostCenter> costCenterDao;

    @JsonProperty("name")
    private String name;

    @JsonIgnore
    private Integer ipdsId;

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
     * @return the ipdsId
     */
    public Integer getinIpdsId() {
        return ipdsId;
    }

    /**
     * @param ininIpdsId the ipdsId to set
     */
    public void setinIpdsId(final Integer inIpdsId) {
        ipdsId = inIpdsId;
    }

    /**
     * @return the costCenterDao
     */
    public static IDao<CostCenter> getDao() {
        return costCenterDao;
    }

    /**
     * The setter for costCenterDao.
     * @param inCostCenterDao the costCenterDao to set
     */
    public void setCostCenterDao(final IDao<CostCenter> inCostCenterDao) {
        costCenterDao = inCostCenterDao;
    }

    @Override
    @JsonView(ILookupView.class)
    public String getText() {
        return name;
    }

}
