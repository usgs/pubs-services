package gov.usgs.cida.pubs.domain.mp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import io.swagger.v3.oas.annotations.media.Schema;

@Component
public class MpPublicationCostCenter extends PublicationCostCenter<MpPublicationCostCenter>{
	private static final long serialVersionUID = -7830204904702714942L;
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
	@Autowired
	@Qualifier("mpPublicationCostCenterDao")
	@Schema(hidden = true)
	public void setMpPublicationCostCenterDao(final IMpDao<MpPublicationCostCenter> inMpPublicationCostCenterDao) {
		mpPublicationCostCenterDao = inMpPublicationCostCenterDao;
	}

}
