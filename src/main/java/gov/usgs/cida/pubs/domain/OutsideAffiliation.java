package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import io.swagger.v3.oas.annotations.media.Schema;

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

	@Autowired
	@Qualifier("outsideAffiliationDao")
	@Schema(hidden = true)
	public void setOutsideAffiliationDao(final IDao<OutsideAffiliation> inOutsideAffiliationDao) {
		outsideAffiliationDao = inOutsideAffiliationDao;
	}
}