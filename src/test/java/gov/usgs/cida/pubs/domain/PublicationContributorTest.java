
package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.utility.PubsUtilities;

public class PublicationContributorTest {
	
	protected static String getAuthorKey() {
		return PubsUtilities.getAuthorKey();
	}
	protected static String getEditorKey() {
		return PubsUtilities.getEditorKey();
	}
	
	public static PublicationContributor<?> buildCorporatePublicationContributor(String typeText, int typeId) {
		CorporateContributor contributor = new CorporateContributor();
		contributor.setOrganization("Evil Corp");
		ContributorType contributorTypeAuthor = new ContributorType();
		contributorTypeAuthor.setText(typeText);
		contributorTypeAuthor.setId(typeId);
		PublicationContributor<?> pubContributor = new PublicationContributor();
		pubContributor.setContributor(contributor);
		pubContributor.setContributorType(contributorTypeAuthor);
		return pubContributor;
		
	}
	
	public static PublicationContributor<?> buildCorporatePublicationEditor() {
		return buildCorporatePublicationContributor(getEditorKey(), ContributorType.EDITORS);
	}
	
	public static PublicationContributor<?> buildCorporatePublicationAuthor() {
		return buildCorporatePublicationContributor(getAuthorKey(), ContributorType.AUTHORS);
	}
	
	public static PublicationContributor<?> buildPersonPublicationAuthor() {
		return buildPersonPublicationContributor(getAuthorKey(), ContributorType.AUTHORS);
	}
	
	public static PublicationContributor<?> buildPersonPublicationEditor() {
		return buildPersonPublicationContributor(getEditorKey(), ContributorType.EDITORS);
	}
	
	public static PublicationContributor<?> buildPersonPublicationContributor(String typeText, int typeId) {
		UsgsContributor contributor = new UsgsContributor();
		contributor.setGiven("John");
		contributor.setFamily("Powell");
		contributor.setSuffix("Jr.");
		ContributorType contributorTypeAuthor = new ContributorType();
		contributorTypeAuthor.setText(typeText);
		contributorTypeAuthor.setId(typeId);
		PublicationContributor<?> pubContributor = new PublicationContributor();
		pubContributor.setContributor(contributor);
		pubContributor.setContributorType(contributorTypeAuthor);
		return pubContributor;
	}


}
