package gov.usgs.cida.pubs.domain;

//import gov.usgs.cida.pubs.dao.intfc.IDao;

public class PublicationContributor<D> extends BaseDomain<D> {

    //TODO Remove dao if it isn't needed.
//    private static IDao<PublicationContributor<?>> publicationContributorDao;

    private Integer publicationId;

    private ContributorType contributorType;

    private Integer rank;

    private Contributor contributor;

    public Integer getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(final Integer inPublicationId) {
        publicationId = inPublicationId;
    }

    public ContributorType getContributorType() {
        return contributorType;
    }

    public void setContributorType(final ContributorType inContributorType) {
        contributorType = inContributorType;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(final Integer inRank) {
        rank = inRank;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(final Contributor inContributor) {
        contributor = inContributor;
    }

//    /**
//     * @return the publicationContributorDao
//     */
//    public static IDao<PublicationContributor<?>> getPublicationContributorDao() {
//        return publicationContributorDao;
//    }
//
//    /**
//     * The setter for publicationContributorDao.
//     * @param inPublicationContributorDao the publicationContributorDao to set
//     */
//    public void setPublicationContributorDao(final IDao<PublicationContributor<?>> inPublicationContributorDao) {
//        publicationContributorDao = inPublicationContributorDao;
//    }

}
