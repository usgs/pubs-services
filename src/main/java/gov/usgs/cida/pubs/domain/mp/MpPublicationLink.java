package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.PublicationLink;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id","rank","type","url","text","size","mime-type"})
public class MpPublicationLink extends PublicationLink<MpPublicationLink> {

    private static IDao<MpPublicationLink> mpPublicationLinkDao;

    public static IDao<MpPublicationLink> getDao() {
        return mpPublicationLinkDao;
    }

    public void setMpPublicationLinkDao(final IDao<MpPublicationLink> inMpPublicationLinkDao) {
        mpPublicationLinkDao = inMpPublicationLinkDao;
    }

}
