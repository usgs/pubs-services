package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationLink;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Component
@JsonPropertyOrder({"id", "rank", "type", "url", "text", "description", "size", "linkFileType"})
public class MpPublicationLink extends PublicationLink<MpPublicationLink> {

	private static final long serialVersionUID = -3448411849107196775L;

	private static IMpDao<MpPublicationLink> mpPublicationLinkDao;

	public MpPublicationLink() {
	}
	
	public MpPublicationLink(final PublicationLink<?> pubLink) {
		//TODO this constructor is only here to create an MpPublicationLink from the Jackson deserialized PublicationLink...
		this.setId(pubLink.getId());
		this.setRank(pubLink.getRank());
		this.setLinkType(pubLink.getLinkType());
		this.setUrl(pubLink.getUrl());
		this.setText(pubLink.getText());
		this.setSize(pubLink.getSize());
		this.setLinkFileType(pubLink.getLinkFileType());
		this.setDescription(pubLink.getDescription());
		this.setPublicationId(pubLink.getPublicationId());
		this.setHelpText(pubLink.getHelpText());
	}

	public static IMpDao<MpPublicationLink> getDao() {
		return mpPublicationLinkDao;
	}

	@Autowired
	@Qualifier("mpPublicationLinkDao")
	@Schema(hidden = true)
	public void setMpPublicationLinkDao(final IMpDao<MpPublicationLink> inMpPublicationLinkDao) {
		mpPublicationLinkDao = inMpPublicationLinkDao;
	}

}
