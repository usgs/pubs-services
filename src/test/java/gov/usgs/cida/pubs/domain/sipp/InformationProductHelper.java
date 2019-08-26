package gov.usgs.cida.pubs.domain.sipp;

import java.util.List;

import gov.usgs.cida.pubs.domain.PublicationSeriesHelper;
import gov.usgs.cida.pubs.domain.PublicationSubtypeHelper;
import gov.usgs.cida.pubs.domain.PublicationTypeHelper;

public final class InformationProductHelper {
	private InformationProductHelper() {
	}

	public static InformationProduct TEST_ONE = new InformationProduct();
	static {
		TEST_ONE.setIndexId("indexID");
		TEST_ONE.setPublicationType(PublicationTypeHelper.FOUR);
		TEST_ONE.setPublicationSubtype(PublicationSubtypeHelper.THIRTEEN);
		TEST_ONE.setUsgsSeriesTitle(PublicationSeriesHelper.FOUR_FIFTY_TWO);
		TEST_ONE.setUsgsSeriesNumber("usgsSeriesNumber");
		TEST_ONE.setUsgsSeriesLetter("usgsSeriesLetter");
		TEST_ONE.setFinalTitle("finalTitle");
		TEST_ONE.setAbstractText("abstractText");
		TEST_ONE.setUsgsPeriodical(true);
		TEST_ONE.setUsgsNumberedSeries(true);
		TEST_ONE.setNonUSGSPublisher("nonUSGSPublisher");
		TEST_ONE.setDigitalObjectIdentifier("digitalObjectIdentifier");
		TEST_ONE.setCooperators("cooperators");
		TEST_ONE.setCitation("citation");
		TEST_ONE.setPhysicalDescription("physicalDescription");
		TEST_ONE.setPageRange("pageRange");
		TEST_ONE.setProductSummary("productSummary");
		TEST_ONE.setIpNumber("ipNumber");
		TEST_ONE.setTask("task");
		TEST_ONE.setJournalTitle("journalTitle");
		TEST_ONE.setVolume("volume");
		TEST_ONE.setIssue("issue");
		TEST_ONE.setEditionNumber("editionNumber");
		TEST_ONE.setPublishingServiceCenter("Madison PSC");

		TEST_ONE.setCostCenter("PSC");
		TEST_ONE.setPublishedURL("https://water.usgs.gov");

		TEST_ONE.setAuthors(List.of(
				SippContributorHelper.FOUR,
				SippContributorHelper.FIVE,
				SippContributorHelper.ONE,
				SippContributorHelper.ONE_ZERO_FOUR,
				SippContributorHelper.SIX,
				SippContributorHelper.ONE_ZERO_ONE
				));

		TEST_ONE.setNotes(List.of(NoteHelper.ONE, NoteHelper.TWO, NoteHelper.THREE));
	}

	public static InformationProduct TEST_TWO = new InformationProduct();
	static {
		TEST_TWO.setIndexId("indexID");
		TEST_TWO.setPublicationType(PublicationTypeHelper.FOUR);
		TEST_TWO.setPublicationSubtype(null);
		TEST_TWO.setUsgsSeriesTitle(PublicationSeriesHelper.FOUR_FIFTY_TWO);
		TEST_TWO.setUsgsSeriesNumber("usgsSeriesNumber");
		TEST_TWO.setUsgsSeriesLetter("usgsSeriesLetter");
		TEST_TWO.setFinalTitle("finalTitle");
		TEST_TWO.setAbstractText("abstractText");
		TEST_TWO.setUsgsPeriodical(false);
		TEST_TWO.setUsgsNumberedSeries(false);
		TEST_TWO.setNonUSGSPublisher("nonUSGSPublisher");
		TEST_TWO.setDigitalObjectIdentifier("digitalObjectIdentifier");
		TEST_TWO.setCooperators("cooperators");
		TEST_TWO.setCitation("citation");
		TEST_TWO.setPhysicalDescription("physicalDescription");
		TEST_TWO.setPageRange("pageRange");
		TEST_TWO.setProductSummary("productSummary");
		TEST_TWO.setIpNumber("ipNumber");
		TEST_TWO.setTask("task");
		TEST_TWO.setJournalTitle("journalTitle");
		TEST_TWO.setVolume("volume");
		TEST_TWO.setIssue("issue");
		TEST_TWO.setEditionNumber("editionNumber");
		TEST_TWO.setPublishingServiceCenter("Madison PSC");
	}

	public static InformationProduct TEST_THREE = new InformationProduct();
	static {
		TEST_THREE.setIndexId("indexID");
		TEST_THREE.setPublicationType(PublicationTypeHelper.TWO);
		TEST_THREE.setPublicationSubtype(PublicationSubtypeHelper.TEN);
		TEST_THREE.setUsgsSeriesTitle(PublicationSeriesHelper.SEVEN_NINETEEN);
		TEST_THREE.setUsgsSeriesNumber("usgsSeriesNumber");
		TEST_THREE.setUsgsSeriesLetter("usgsSeriesLetter");
		TEST_THREE.setFinalTitle("finalTitle");
		TEST_THREE.setAbstractText("abstractText");
		TEST_THREE.setUsgsPeriodical(false);
		TEST_THREE.setUsgsNumberedSeries(false);
		TEST_THREE.setNonUSGSPublisher("nonUSGSPublisher");
		TEST_THREE.setDigitalObjectIdentifier("digitalObjectIdentifier");
		TEST_THREE.setCooperators("cooperators");
		TEST_THREE.setCitation("citation");
		TEST_THREE.setPhysicalDescription("physicalDescription");
		TEST_THREE.setPageRange("pageRange");
		TEST_THREE.setProductSummary("productSummary");
		TEST_THREE.setIpNumber("ipNumber");
		TEST_THREE.setTask("task");
		TEST_THREE.setJournalTitle("journalTitle");
		TEST_THREE.setVolume("volume");
		TEST_THREE.setIssue("issue");
		TEST_THREE.setEditionNumber("editionNumber");
		TEST_THREE.setPublishingServiceCenter("Madison PSC");
	}
}
