package gov.usgs.cida.pubs.domain.pw;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationLink;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id","rank","type","url","text","size","mime-type"})
public class PwPublicationLink extends PublicationLink<PwPublicationLink> {

	private static final long serialVersionUID = -2247848604171730365L;

	private static IMpDao<PwPublicationLink> pwPublicationLinkDao;

	public static IMpDao<PwPublicationLink> getDao() {
		return pwPublicationLinkDao;
	}

	public void setPwPublicationLinkDao(final IMpDao<PwPublicationLink> inPwPublicationLinkDao) {
		pwPublicationLinkDao = inPwPublicationLinkDao;
	}

}
