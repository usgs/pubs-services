package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
public class CostCenter extends Affiliation<CostCenter> implements Serializable {

	private static final long serialVersionUID = -7804226743028056085L;

	private static IDao<CostCenter> costCenterDao;

	public CostCenter() {
		usgs = true;
		active = true;
	}

	@JsonIgnore
	private Integer ipdsId;

	public Integer getIpdsId() {
		return ipdsId;
	}

	public void setIpdsId(final Integer inIpdsId) {
		ipdsId = inIpdsId;
	}

	public void setIpdsId(final String inIpdsId) {
		ipdsId = PubsUtilities.parseInteger(inIpdsId);
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
	@Autowired
	@Qualifier("costCenterDao")
	public void setCostCenterDao(final IDao<CostCenter> inCostCenterDao) {
		costCenterDao = inCostCenterDao;
	}
}