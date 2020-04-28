package gov.usgs.cida.pubs.domain.pw;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import io.swagger.v3.oas.annotations.media.Schema;

public class PwPublicationContributor extends PublicationContributor<MpPublicationContributor> {

	private static final long serialVersionUID = -2142787008474259876L;

	private static IMpDao<PwPublicationContributor> pwPublicationContributorDao;

	public static IMpDao<PwPublicationContributor> getDao() {
		return pwPublicationContributorDao;
	}

	@Schema(hidden = true)
	public void setPwPublicationContributorDao(final IMpDao<PwPublicationContributor> inPwPublicationContributorDao) {
		pwPublicationContributorDao = inPwPublicationContributorDao;
	}

}
