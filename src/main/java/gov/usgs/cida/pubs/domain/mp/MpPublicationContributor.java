package gov.usgs.cida.pubs.domain.mp;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.PublicationContributor;

@JsonPropertyOrder({"id", "corporation", "usgs", "contributorType", "rank", "family", "given", "suffix",
	"email", "organization", "affiliation"})
public class MpPublicationContributor extends PublicationContributor<MpPublicationContributor> {

	private static final long serialVersionUID = 5207277965533996229L;

	private static IMpDao<MpPublicationContributor> mpPublicationContributorDao;

	public MpPublicationContributor() {}

	public MpPublicationContributor(final PublicationContributor<?> pubContributor) {
		//TODO this constructor is only here to create an MpPublicationContributor from the Jackson deserialized PublicationContributor...
		this.setId(pubContributor.getId());
		this.setPublicationId(pubContributor.getPublicationId());
		this.setContributorType(pubContributor.getContributorType());
		this.setContributor(pubContributor.getContributor());
		this.setRank(pubContributor.getRank());
	}

    /**
     * @return the mpPublicationContributorDao
     */
    public static IMpDao<MpPublicationContributor> getDao() {
        return mpPublicationContributorDao;
    }

    /**
     * The setter for mpPublicationContributorDao.
     * @param inMpPublicationContributorDao the mpPublicationContributorDao to set
     */
    public void setMpPublicationContributorDao(final IMpDao<MpPublicationContributor> inMpPublicationContributorDao) {
        mpPublicationContributorDao = inMpPublicationContributorDao;
    }

}
