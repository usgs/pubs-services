
package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.ContributorTypeDaoIT;

public final class PublicationContributorHelper {
	private PublicationContributorHelper() {
	}

	public static final PublicationContributor<?> USGS_AUTHOR = new PublicationContributor<>();
	static {
		USGS_AUTHOR.setContributorType(ContributorTypeHelper.AUTHORS);
		USGS_AUTHOR.setRank(12);
		USGS_AUTHOR.setContributor(UsgsContributorHelper.JANE_N_DOE);
	}

	public static final PublicationContributor<?> OUTSIDE_AUTHOR = new PublicationContributor<>();
	static {
		OUTSIDE_AUTHOR.setContributorType(ContributorTypeHelper.AUTHORS);
		OUTSIDE_AUTHOR.setRank(86);
		OUTSIDE_AUTHOR.setContributor(OutsideContributorHelper.JANE_M_DOE);
	}

	public static PublicationContributor<?> buildCorporatePublicationContributor(String typeText, int typeId) {
		CorporateContributor contributor = new CorporateContributor();
		contributor.setOrganization("Evil Corp");
		ContributorType contributorTypeAuthor = new ContributorType();
		contributorTypeAuthor.setText(typeText);
		contributorTypeAuthor.setId(typeId);
		PublicationContributor<?> pubContributor = new PublicationContributor<>();
		pubContributor.setContributor(contributor);
		pubContributor.setContributorType(contributorTypeAuthor);
		return pubContributor;
	}

	public static PublicationContributor<?> buildCorporatePublicationEditor() {
		return buildCorporatePublicationContributor(ContributorTypeDaoIT.EDITOR_KEY, ContributorType.EDITORS);
	}

	public static PublicationContributor<?> buildCorporatePublicationAuthor() {
		return buildCorporatePublicationContributor(ContributorTypeDaoIT.AUTHOR_KEY, ContributorType.AUTHORS);
	}

	public static PublicationContributor<?> buildPersonPublicationAuthor() {
		return buildPersonPublicationContributor(ContributorTypeDaoIT.AUTHOR_KEY, ContributorType.AUTHORS);
	}

	public static PublicationContributor<?> buildPersonPublicationEditor() {
		return buildPersonPublicationContributor(ContributorTypeDaoIT.EDITOR_KEY, ContributorType.EDITORS);
	}

	public static PublicationContributor<?> buildPersonPublicationContributor(String typeText, int typeId) {
		UsgsContributor contributor = new UsgsContributor();
		contributor.setGiven("John");
		contributor.setFamily("Powell");
		contributor.setSuffix("Jr.");
		ContributorType contributorTypeAuthor = new ContributorType();
		contributorTypeAuthor.setText(typeText);
		contributorTypeAuthor.setId(typeId);
		PublicationContributor<?> pubContributor = new PublicationContributor<>();
		pubContributor.setContributor(contributor);
		pubContributor.setContributorType(contributorTypeAuthor);
		return pubContributor;
	}

}
