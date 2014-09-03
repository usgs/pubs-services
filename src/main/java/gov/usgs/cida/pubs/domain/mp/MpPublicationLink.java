package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationLink;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id","rank","type","url","text","size","mime-type"})
public class MpPublicationLink extends PublicationLink<MpPublicationLink> {

	private static final long serialVersionUID = -3448411849107196775L;

	private static IMpDao<MpPublicationLink> mpPublicationLinkDao;

    public static IMpDao<MpPublicationLink> getDao() {
        return mpPublicationLinkDao;
    }

    public void setMpPublicationLinkDao(final IMpDao<MpPublicationLink> inMpPublicationLinkDao) {
        mpPublicationLinkDao = inMpPublicationLinkDao;
    }

}
