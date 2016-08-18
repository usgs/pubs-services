package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OutsideAffiliation extends Affiliation<OutsideAffiliation> {

	private static IDao<OutsideAffiliation> outsideAffiliationDao;

	public OutsideAffiliation() {
		usgs = false;
		active = true;
	}

	public static IDao<OutsideAffiliation> getDao() {
		return outsideAffiliationDao;
	}

	/**
	 * The setter for costCenterDao.
	 * @param inCostCenterDao the costCenterDao to set
	 */
	@Autowired
	@Qualifier("outsideAffiliationDao")
	public void setOutsideAffiliationDao(final IDao<OutsideAffiliation> inOutsideAffiliationDao) {
		outsideAffiliationDao = inOutsideAffiliationDao;
	}
}