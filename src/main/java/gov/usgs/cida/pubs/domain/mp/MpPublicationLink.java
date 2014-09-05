package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationLink;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id","rank","type","url","text","size","mime-type"})
public class MpPublicationLink extends PublicationLink<MpPublicationLink> {

	private static final long serialVersionUID = -3448411849107196775L;

	private static IMpDao<MpPublicationLink> mpPublicationLinkDao;

	public MpPublicationLink() {}
	
	public MpPublicationLink(final PublicationLink<?> pubLink) {
		//TODO this constructor is only here to create an MpPublicationLink from the Jackson deserialized PublicationLink...
		this.setId(pubLink.getId());
		this.setRank(pubLink.getRank());
		this.setLinkType(pubLink.getLinkType());
		this.setUrl(pubLink.getUrl());
		this.setText(pubLink.getText());
		this.setObjectSize(pubLink.getObjectSize());
		this.setLinkFileType(pubLink.getLinkFileType());
		this.setDescription(pubLink.getDescription());
		this.setPublicationId(pubLink.getPublicationId());
	}

    public static IMpDao<MpPublicationLink> getDao() {
        return mpPublicationLinkDao;
    }

    public void setMpPublicationLinkDao(final IMpDao<MpPublicationLink> inMpPublicationLinkDao) {
        mpPublicationLinkDao = inMpPublicationLinkDao;
    }

}
