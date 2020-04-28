package gov.usgs.cida.pubs.domain.pw;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import io.swagger.v3.oas.annotations.media.Schema;

public class PwPublicationCostCenter extends PublicationCostCenter<PwPublicationCostCenter>{

	private static final long serialVersionUID = 7311268641834130461L;
	private static IMpDao<PwPublicationCostCenter> pwPublicationCostCenterDao;

	public static IMpDao<PwPublicationCostCenter> getDao() {
		return pwPublicationCostCenterDao;
	}

	@Schema(hidden = true)
	public void setPwPublicationCostCenterDao(final IMpDao<PwPublicationCostCenter> inPwPublicationCostCenterDao) {
		pwPublicationCostCenterDao = inPwPublicationCostCenterDao;
	}

}
