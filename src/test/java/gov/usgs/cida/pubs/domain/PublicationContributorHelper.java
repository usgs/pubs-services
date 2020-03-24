package gov.usgs.cida.pubs.domain;

import java.util.Collection;
import java.util.Set;

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
		return buildCorporatePublicationContributor(ContributorType.EDITOR_KEY, ContributorType.EDITORS);
	}

	public static PublicationContributor<?> buildCorporatePublicationAuthor() {
		return buildCorporatePublicationContributor(ContributorType.AUTHOR_KEY, ContributorType.AUTHORS);
	}

	public static PublicationContributor<?> buildPersonPublicationAuthor() {
		return buildPersonPublicationContributor(ContributorType.AUTHOR_KEY, ContributorType.AUTHORS);
	}

	public static PublicationContributor<?> buildPersonPublicationEditor() {
		return buildPersonPublicationContributor(ContributorType.EDITOR_KEY, ContributorType.EDITORS);
	}

	public static PublicationContributor<?> buildPersonPublicationCompiler() {
		return buildPersonPublicationContributor(ContributorType.COMPILER_KEY, ContributorType.COMPILERS);
	}

	public static PublicationContributor<?> buildPersonPublicationContributor(String typeText, int typeId) {
		UsgsContributor contributor = getContribubtorByType(typeId);
		ContributorType contributorTypeAuthor = new ContributorType();
		contributorTypeAuthor.setText(typeText);
		contributorTypeAuthor.setId(typeId);
		PublicationContributor<?> pubContributor = new PublicationContributor<>();
		pubContributor.setContributor(contributor);
		pubContributor.setContributorType(contributorTypeAuthor);
		return pubContributor;
	}

	public static UsgsContributor getContribubtorByType(int typeId) {
		UsgsContributor contributor = new UsgsContributor();
		contributor.setGiven("John");
		String family = "";
		if (typeId == ContributorType.AUTHORS) {
			family = "Author";
		} else if (typeId == ContributorType.EDITORS) {
			family = "Editor";
		} else if (typeId == ContributorType.COMPILERS) {
			family = "Compiler";
		} else {
			family = "Unknown";
		}
		contributor.setFamily(family);
		contributor.setSuffix("Jr.");
		return contributor;
	}

	public static Collection<PublicationContributor<?>> getContributors() {
		Collection<PublicationContributor<?>> contributors = Set.of(USGS_AUTHOR);
		return contributors;
	}
}
