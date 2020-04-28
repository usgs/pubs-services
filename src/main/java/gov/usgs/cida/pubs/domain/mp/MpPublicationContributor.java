package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Component
@JsonPropertyOrder({"id", "corporation", "usgs", "contributorType", "rank", "family", "given", "suffix",
	"email", "organization", "affiliation"})
public class MpPublicationContributor extends PublicationContributor<MpPublicationContributor> {

	private static final long serialVersionUID = 5207277965533996229L;

	private static IMpDao<MpPublicationContributor> mpPublicationContributorDao;

	public MpPublicationContributor() {
	}

	public MpPublicationContributor(final PublicationContributor<?> pubContributor) {
		//TODO this constructor is only here to create an MpPublicationContributor from the Jackson deserialized PublicationContributor...
		this.setId(pubContributor.getId());
		this.setPublicationId(pubContributor.getPublicationId());
		this.setContributorType(pubContributor.getContributorType());
		this.setContributor(pubContributor.getContributor());
		this.setRank(pubContributor.getRank());
	}

	public static IMpDao<MpPublicationContributor> getDao() {
		return mpPublicationContributorDao;
	}

	@Autowired
	@Qualifier("mpPublicationContributorDao")
	@Schema(hidden = true)
	public void setMpPublicationContributorDao(final IMpDao<MpPublicationContributor> inMpPublicationContributorDao) {
		mpPublicationContributorDao = inMpPublicationContributorDao;
	}

}
