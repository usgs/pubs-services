package gov.usgs.cida.pubs.busservice.sipp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.domain.sipp.Author;
import gov.usgs.cida.pubs.domain.sipp.InformationProduct;
import gov.usgs.cida.pubs.domain.sipp.Note;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Service
public class SippConversionService {

	public MpPublication buildMpPublication(InformationProduct informationProduct, Integer prodId) {
		MpPublication mpPublication = buildObjectProperties(informationProduct, prodId);

		mpPublication.setContributors(buildPublicationContributors(informationProduct));

		mpPublication.setCostCenters(buildPublicationCostCenters(informationProduct.getCostCenter()));

		mpPublication.setNotes(buildNotes(informationProduct));

		mpPublication.setLinks(buildPublicationLinks(informationProduct.getPublishedURL()));

		return mpPublication;
	}

	protected Contributor<?> buildContributor(Author author) {
		Contributor<?> contributor;
		if (StringUtils.isEmpty(author.getNonUSGSContributor())) {
			contributor = buildUsgsContributor(author);
		} else {
			contributor = buildOutsideContributor(author);
		}
		return contributor;
	}

	protected ContributorType buildContributorType(Author author) {
		ContributorType contributorType = new ContributorType();
		if ("1".equalsIgnoreCase(author.getContributorRole())) {
			contributorType.setId(ContributorType.AUTHORS);
		} else {
			contributorType.setId(ContributorType.EDITORS);
		}
		return contributorType;
	}

	protected String buildNotes(InformationProduct informationProduct) {
		StringBuilder notes = new StringBuilder(StringUtils.isEmpty(informationProduct.getProductSummary()) ? "" : informationProduct.getProductSummary());
		if (0 < notes.length()) {
			notes.append("\n\t");
		}
		if (null != informationProduct.getNotes()) {
			for (Note note : informationProduct.getNotes()) {
				if (StringUtils.isNotBlank(note.getNoteComment())) {
					notes.append(note.getNoteComment().trim()).append("|");
				}
			}
		}
		return 0 < notes.length()? notes.toString() : null;
	}

	protected MpPublication buildObjectProperties(InformationProduct informationProduct, Integer prodId) {
		MpPublication mpPublication = new MpPublication();
		mpPublication.setId(prodId);
		mpPublication.setIndexId(informationProduct.getIndexId());

		mpPublication.setPublicationType(informationProduct.getPublicationType());
		mpPublication.setPublicationSubtype(informationProduct.getPublicationSubtype());

		if (null != mpPublication.getPublicationSubtype()) {
			mpPublication.setSeriesTitle(informationProduct.getUsgsSeriesTitle());
		}

		mpPublication.setSeriesNumber(cleanseUsgsSeriesNumber(informationProduct.getUsgsSeriesNumber()));
		mpPublication.setChapter(informationProduct.getUsgsSeriesLetter());
		mpPublication.setTitle(buildTitle(informationProduct.getFinalTitle(), informationProduct.getWorkingTitle()));

		mpPublication.setDocAbstract(informationProduct.getAbstractText());
		mpPublication.setLanguage("English");

		if (informationProduct.isUsgsPeriodical() || informationProduct.isUsgsNumberedSeries()) {
			mpPublication.setPublisher("U.S. Geological Survey");
			mpPublication.setPublisherLocation("Reston VA");
		} else {
			mpPublication.setPublisher(informationProduct.getNonUSGSPublisher());
		}

		mpPublication.setDoi(informationProduct.getDigitalObjectIdentifier());
		mpPublication.setCollaboration(informationProduct.getCooperators());
		mpPublication.setUsgsCitation(informationProduct.getCitation());
		mpPublication.setProductDescription(informationProduct.getPhysicalDescription());

		mpPublication.setStartPage(informationProduct.getPageRange());

		mpPublication.setIpdsId(informationProduct.getIpNumber());
		mpPublication.setIpdsReviewProcessState(informationProduct.getTask());

		String largerWorkTitle = informationProduct.getJournalTitle();
		if (StringUtils.isNotBlank(largerWorkTitle)) {
			if (PubsUtils.isPublicationTypeArticle(mpPublication.getPublicationType())
					&& null != mpPublication.getPublicationSubtype()) {
				mpPublication.setSeriesTitle(buildSeriesTitle(mpPublication.getPublicationSubtype(), largerWorkTitle));
			} else {
				mpPublication.setLargerWorkTitle(largerWorkTitle);
			}
		}
		mpPublication.setPublicationYear(String.valueOf(LocalDate.now().getYear()));

		mpPublication.setVolume(informationProduct.getVolume());
		mpPublication.setIssue(informationProduct.getIssue());
		mpPublication.setEdition(informationProduct.getEditionNumber());

		mpPublication.setPublishingServiceCenter(buildPublishingServiceCenter(informationProduct.getPublishingServiceCenter()));

		return mpPublication;
	}

	protected OutsideContributor buildOutsideContributor(Author author) {
		OutsideContributor contributor = new OutsideContributor();

		String[] familyGiven = author.splitFullName();
		contributor.setFamily(familyGiven[0]);
		contributor.setGiven(familyGiven[1]);

		contributor.setOrcid(PubsUtils.normalizeOrcid(author.getOrcid()));

		if (null != author.getNonUSGSAffiliation()) {
			OutsideAffiliation affiliation = new OutsideAffiliation();
			affiliation.setText(author.getNonUSGSAffiliation());
			contributor.getAffiliations().add(affiliation);
		}

		return contributor;
	}

	protected MpPublicationContributor buildPublicationContributor(Author author) {
		MpPublicationContributor rtn = new MpPublicationContributor();
		rtn.setContributor(buildContributor(author));
		rtn.setContributorType(buildContributorType(author));
		rtn.setRank(buildRank(author.getRank()));
		return rtn;
	}

	protected Collection<PublicationContributor<?>> buildPublicationContributors(InformationProduct informationProduct) {
		List<MpPublicationContributor> authors = new ArrayList<>();
		List<MpPublicationContributor> editors = new ArrayList<>();

		for (Author author : informationProduct.getAuthors()) {
			MpPublicationContributor pubContributor = buildPublicationContributor(author);
			if (ContributorType.AUTHORS.equals(pubContributor.getContributorType().getId())) {
				authors.add(pubContributor);
			} else {
				editors.add(pubContributor);
			}
		}

		Collection<PublicationContributor<?>> pubContributors = new ArrayList<>();
		pubContributors.addAll(fixRanks(authors));
		pubContributors.addAll(fixRanks(editors));
		return pubContributors;
	}

	protected Collection<PublicationCostCenter<?>> buildPublicationCostCenters(String name) {
		if (null != name) {
			CostCenter costCenter =  new CostCenter();
			costCenter.setText(name);
			PublicationCostCenter<?> publicationCostCenter = new MpPublicationCostCenter();
			publicationCostCenter.setCostCenter((CostCenter) costCenter);
			Collection<PublicationCostCenter<?>> costCenters = List.of(publicationCostCenter);
			return costCenters;
		} else {
			return null;
		}
	}

	protected Collection<PublicationLink<?>> buildPublicationLinks(String publishedUrl) {
		if (null != publishedUrl) {
			PublicationLink<?> publicationLink = new MpPublicationLink();
			publicationLink.setUrl(publishedUrl);
			LinkType linkType = new LinkType();
			linkType.setId(LinkType.INDEX_PAGE);
			publicationLink.setLinkType(linkType);
			Collection<PublicationLink<?>> publicationLinks = List.of(publicationLink);
			return publicationLinks;
		} else {
			return null;
		}
	}

	protected PublishingServiceCenter buildPublishingServiceCenter(String text) {
		if (null != text) {
			PublishingServiceCenter publishingServiceCenter = new PublishingServiceCenter();
			publishingServiceCenter.setText(text);
			return publishingServiceCenter;
		} else {
			return null;
		}
	}

	protected Integer buildRank(String rawRank) {
		Integer rank = PubsUtils.parseInteger(rawRank);
		if (null == rank) {
			return 1;
		} else {
			return rank;
		}
	}

	protected PublicationSeries buildSeriesTitle(PublicationSubtype pubSubtype, String text) {
		if (null == pubSubtype || StringUtils.isEmpty(text)) {
			return null;
		} else {
			PublicationSeries publicationSeries = new PublicationSeries();
			publicationSeries.setPublicationSubtype(pubSubtype);
			publicationSeries.setText(text);
			return publicationSeries;
		}
	}

	protected String buildTitle(String finalTitle, String workingTitle) {
		if (StringUtils.isEmpty(finalTitle)) {
			return workingTitle;
		} else {
			return finalTitle;
		}
	}

	protected UsgsContributor buildUsgsContributor(Author author) {
		UsgsContributor contributor = new UsgsContributor();
//TODO - if added to SIPP...
//		contributor.setFamily(parser.getFirstNodeText(doc.getDocumentElement(), Schema.LAST_NAME));
//		contributor.setGiven(parser.getFirstNodeText(doc.getDocumentElement(), Schema.FIRST_NAME));

		String[] familyGiven = author.splitFullName();
		contributor.setFamily(familyGiven[0]);
		contributor.setGiven(familyGiven[1]);

		contributor.setOrcid(PubsUtils.normalizeOrcid(author.getOrcid()));
		contributor.setPreferred(true);

		if (null != author.getCostCenter()) {
			CostCenter costCenter = new CostCenter();
			costCenter.setText(author.getCostCenter());
			contributor.getAffiliations().add(costCenter);
		}

		return contributor;
	}

	protected String cleanseUsgsSeriesNumber(String usgsSeriesNumber) {
		if (StringUtils.isEmpty(usgsSeriesNumber)
				|| ".".contentEquals(usgsSeriesNumber)) {
			return null;
		} else {
			return usgsSeriesNumber;
		}
	}

	protected Collection<MpPublicationContributor> fixRanks(final List<MpPublicationContributor> contributors) {
		Set<Integer> ranks = new HashSet<>();
		for (Iterator<MpPublicationContributor> contributorsIter = contributors.iterator(); contributorsIter.hasNext();) {
			MpPublicationContributor contributor = contributorsIter.next();
			ranks.add(contributor.getRank());
		}

		if (contributors.size() != ranks.size()) {
			Integer i = 0;
			for (Iterator<MpPublicationContributor> fixIterator = contributors.iterator(); fixIterator.hasNext();) {
				MpPublicationContributor fixMe = fixIterator.next();
				i++;
				fixMe.setRank(i);
			}
		}
		return contributors;
	}
}
