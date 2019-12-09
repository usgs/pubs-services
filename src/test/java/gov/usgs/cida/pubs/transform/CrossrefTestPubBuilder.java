package gov.usgs.cida.pubs.transform;

import java.util.ArrayList;
import java.util.Collection;

import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationContributorHelper;
import gov.usgs.cida.pubs.domain.PublicationIT;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationLinkTest;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;

/**
 * Methods in this class generate test Publications that are have sufficient 
 * attributes for converting to Crossref XML
 */
public class CrossrefTestPubBuilder {

	public static Publication<?> buildUnNumberedSeriesPub(Publication<?> inPub) {
		Publication<?> pub = PublicationIT.buildAPub(inPub, 42);
		PublicationSubtype unnumbered = new PublicationSubtype();
		unnumbered.setId(PublicationSubtype.USGS_UNNUMBERED_SERIES);
		pub.setPublicationSubtype(unnumbered);
		PublicationSeries series = new PublicationSeries();
		series.setCode("GIP");
		series.setText("General Information Product");
		pub.setIndexId("unnumbered");
		pub.setSeriesTitle(series);
		pub.setPublicationYear("2013");
		pub.setTitle("Postwildfire debris-flow hazard assessment of the area burned by the 2013 West Fork Fire Complex, southwestern Colorado");
		pub.setDoi("10.3133/ofr20131259");

		pub.setStartPage("52");
		pub.setEndPage("56");

		Collection<PublicationContributor<?>> contributors = new ArrayList<>();

		contributors.add(PublicationContributorHelper.buildPersonPublicationAuthor());
		contributors.add(PublicationContributorHelper.buildPersonPublicationEditor());
		contributors.add(PublicationContributorHelper.buildCorporatePublicationAuthor());
		contributors.add(PublicationContributorHelper.buildPersonPublicationCompiler());
		contributors.add(PublicationContributorHelper.buildCorporatePublicationEditor());

		pub.setContributors(contributors);

		Collection<PublicationLink<?>> links = new ArrayList<>();
		links.add(PublicationLinkTest.buildIndexLink());
		pub.setLinks(links);

		return pub;
	}

	public static Publication<?> buildNumberedSeriesPub(Publication<?> inPub) {
		Publication<?> pub = PublicationIT.buildAPub(inPub, 42);
		PublicationSubtype numbered = new PublicationSubtype();
		numbered.setId(PublicationSubtype.USGS_NUMBERED_SERIES);
		pub.setPublicationSubtype(numbered);
		PublicationSeries series = new PublicationSeries();
		series.setCode("OFR");
		series.setText("Open-File Report");
		series.setOnlineIssn("2331-1258");
		pub.setIndexId("numbered");
		pub.setSeriesTitle(series);
		pub.setPublicationYear("2013");
		pub.setTitle("Postwildfire debris-flow hazard assessment of the area burned by the 2013 West Fork Fire Complex, southwestern Colorado");
		pub.setSeriesNumber("2013-1259");
		pub.setDoi("10.3133/ofr20131259");

		Collection<PublicationContributor<?>> contributors = new ArrayList<>();
		contributors.add(PublicationContributorHelper.buildPersonPublicationAuthor());
		contributors.add(PublicationContributorHelper.buildPersonPublicationEditor());
		contributors.add(PublicationContributorHelper.buildPersonPublicationCompiler());
		contributors.add(PublicationContributorHelper.buildCorporatePublicationAuthor());
		contributors.add(PublicationContributorHelper.buildCorporatePublicationEditor());
		pub.setContributors(contributors);

		pub.setStartPage("52");
		pub.setEndPage("56");

		Collection<PublicationLink<?>> links = new ArrayList<>();
		links.add(PublicationLinkTest.buildIndexLink());
		pub.setLinks(links);

		return pub;
	}
}
