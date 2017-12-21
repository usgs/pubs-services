package gov.usgs.cida.pubs.busservice.ipds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import gov.usgs.cida.pubs.PubMap;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.ipds.IpdsPubTypeConv;
import gov.usgs.cida.pubs.domain.ipds.PublicationMap;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Service
public class IpdsBinding {

	private final IpdsParserService parser;
	private final IpdsCostCenterService ipdsCostCenterService;
	private final IpdsContributorService ipdsContributorService;

	@Autowired
	public IpdsBinding(IpdsParserService parser, IpdsCostCenterService ipdsCostCenterService, IpdsContributorService ipdsContributorService) throws ParserConfigurationException {
		this.parser = parser;
		this.ipdsCostCenterService = ipdsCostCenterService;
		this.ipdsContributorService = ipdsContributorService;
	}

	public PublicationMap bindNotes(String notesXml, Set<String> tagsOfInterest) throws SAXException, IOException {
		PublicationMap notes = new PublicationMap();
		if (null != tagsOfInterest) {
			Document doc = parser.makeDocument(notesXml);

			for (String tagName : tagsOfInterest) {
				StringBuilder valueText = new StringBuilder();
				NodeList nodes = doc.getElementsByTagName(tagName);
				if (nodes.getLength() < 1) {
					// TODO do we want to soft with no log or hard fail with pLog
					continue;
				}
				for (int i = 0; i < nodes.getLength(); i++) {
					valueText.append(nodes.item(i).getTextContent().trim()).append("|");
				}
				if (1 < valueText.length()) {
					//Went to 1 because we always tag the end with pipe...
					notes.put(tagName, valueText.toString());
				}
			}
		}
		return notes;
	}

	public Collection<PublicationContributor<?>> bindContributors(String contributorsXml, String context) throws SAXException, IOException {
		List<MpPublicationContributor> authors = new ArrayList<>();
		List<MpPublicationContributor> editors = new ArrayList<>();
		Document doc = parser.makeDocument(contributorsXml);

		NodeList entries = doc.getElementsByTagName("m:properties");
		for (int n=0; n<entries.getLength(); n++) {
			MpPublicationContributor pubContributor = ipdsContributorService.buildPublicationContributor(entries.item(n), context);
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

	public MpPublication bindPublication(PubMap inPub) {
		if (null != inPub && !inPub.isEmpty()) {
			MpPublication pub = new MpPublication();
			//Not from IPDS - pub.setId()
			//Not from IPDS - pub.setIndexId()
			//Not from IPDS - pub.setDisplayToPublicDate()

			IpdsPubTypeConv conv = IpdsPubTypeConv.getDao().getByIpdsValue(getStringValue(inPub, IpdsMessageLog.PRODUCTTYPEVALUE));
			if (null != conv) {
				pub.setPublicationType(conv.getPublicationType());
				pub.setPublicationSubtype(conv.getPublicationSubtype());
			}

			if (null != pub.getPublicationSubtype()) {
				pub.setSeriesTitle(getSeriesTitle(pub.getPublicationSubtype(), inPub));
			}

			String tempSeriesNumber = getStringValue(inPub, IpdsMessageLog.USGSSERIESNUMBER);
			if (null != tempSeriesNumber) {
				if (".".contentEquals(tempSeriesNumber)) {
					tempSeriesNumber = null;
				}
				pub.setSeriesNumber(tempSeriesNumber);
			}

			//Not from IPDS - pub.setSubseriesTitle();
			pub.setChapter(getStringValue(inPub, IpdsMessageLog.USGSSERIESLETTER));
			//Not from IPDS - pub.setSubchapterNumber();

			String tempTitle = getStringValue(inPub, IpdsMessageLog.FINALTITLE);
			if (null == tempTitle) {
				tempTitle = getStringValue(inPub, IpdsMessageLog.WORKINGTITLE);
			}
			pub.setTitle(tempTitle);

			pub.setDocAbstract(getStringValue(inPub, IpdsMessageLog.ABSTRACT));
			pub.setLanguage("English");

			if ((null != conv && IpdsPubTypeConv.USGS_PERIODICAL == conv.getId())
					|| (PubsUtilities.isUsgsNumberedSeries(pub.getPublicationSubtype()))) {
				pub.setPublisher("U.S. Geological Survey");
				pub.setPublisherLocation("Reston VA");
			} else {
				pub.setPublisher(getStringValue(inPub, IpdsMessageLog.NONUSGSPUBLISHER));
			}

			pub.setDoi(getStringValue(inPub, IpdsMessageLog.DIGITALOBJECTIDENTIFIER));
			//Not from IPDS - pub.setIssn();
			pub.setIsbn(getStringValue(inPub, IpdsMessageLog.ISBN));
			pub.setCollaboration(getStringValue(inPub, IpdsMessageLog.COOPERATORS));
			pub.setUsgsCitation(getStringValue(inPub, IpdsMessageLog.CITATION));
			pub.setProductDescription(getStringValue(inPub, IpdsMessageLog.PHYSICALDESCRIPTION));

			pub.setStartPage(getStringValue(inPub, IpdsMessageLog.PAGERANGE));
			//Not from IPDS - pub.setEndPage();
			//Not from IPDS - pub.setNumberOfPages();
			//Not from IPDS - pub.setOnlineOnly();
			//Not from IPDS - pub.setAdditionalOnlineFiles();
			//Not from IPDS - pub.setTemporalStart();
			//Not from IPDS - pub.setTemporalEnd();

			//We put ProductSummary into notes and then add to notes later with what is in the real notes...
			pub.setNotes(getStringValue(inPub, IpdsMessageLog.PRODUCTSUMMARY));

			pub.setIpdsId(getStringValue(inPub, IpdsMessageLog.IPNUMBER));
			pub.setIpdsReviewProcessState(getStringValue(inPub, IpdsMessageLog.IPDSREVIEWPROCESSSTATEVALUE));
			pub.setIpdsInternalId(getStringValue(inPub, IpdsMessageLog.IPDS_INTERNAL_ID));

			//Not from IPDS - pub.setLargerWorkType()
			String largerWorkTitle = getStringValue(inPub, IpdsMessageLog.JOURNALTITLE);
			if (StringUtils.isNotBlank(largerWorkTitle)) {
				if (PubsUtilities.isPublicationTypeArticle(pub.getPublicationType())
						&& null != pub.getPublicationSubtype()) {
					pub.setSeriesTitle(getSeriesTitle(pub.getPublicationSubtype(), largerWorkTitle));
				} else {
					pub.setLargerWorkTitle(getStringValue(inPub, IpdsMessageLog.JOURNALTITLE));
				}
			}
			String publicationYear = getStringValue(inPub, IpdsMessageLog.DISEMINATIONDATE);
			if (null != publicationYear && 3 < publicationYear.length()) {
				pub.setPublicationYear(getStringValue(inPub, IpdsMessageLog.DISEMINATIONDATE).substring(0, 4));
			}
			//Not from IPDS - pub.setConferenceTitle();
			//Not from IPDS - pub.setConferenceDate();
			//Not from IPDS - pub.setConferenceLocation();
			//In other section pub.setAuthors();
			//In other section pub.setEditors();
			//In other section pub.setCostCenters();
			//In other section pub.setLinks();
			//Not from IPDS - pub.setProjection();
			//Not from IPDS - pub.setDatum();
			//Not from IPDS - pub.setCountry();
			//Not from IPDS - pub.setState();
			//Not from IPDS - pub.setCounty();
			//Not from IPDS - pub.setCity();
			//Not from IPDS - pub.setOtherGeospatial();
			//Not from IPDS - pub.setGeographicExtents();
			pub.setVolume(getStringValue(inPub, IpdsMessageLog.VOLUME));
			pub.setIssue(getStringValue(inPub, IpdsMessageLog.ISSUE));
			pub.setEdition(getStringValue(inPub, IpdsMessageLog.EDITIONNUMBER));
			//Not from IPDS - pub.setComments();
			//Not from IPDS - pub.setContact();
			//Not from IPDS - pub.setTableOfContents();
			PublishingServiceCenter psc = PublishingServiceCenter.getDao().getByIpdsId(
					PubsUtilities.parseInteger(getStringValue(inPub, IpdsMessageLog.PUBLISHINGSERVICECENTERID)));
			pub.setPublishingServiceCenter(psc);
			//Not from IPDS - pub.setPublishedDateStatement();
			return pub;
		}
		return null;
	}

	public Collection<PublicationLink<?>> bindPublishedURL(final PubMap inPub) {
		Collection<PublicationLink<?>> rtn = null;
		//We pull the URL from the structure "URL, DisplayText"
		if (null != inPub
				&& null != getStringValue(inPub, IpdsMessageLog.PUBLISHEDURL)) {
			String[] publishedUrls = getStringValue(inPub, IpdsMessageLog.PUBLISHEDURL).split(",");
			if (0 < publishedUrls.length
					&& 0 < publishedUrls[0].length()) {
				PublicationLink<?> link = new MpPublicationLink();
				link.setUrl(publishedUrls[0]);
				link.setLinkType(LinkType.getDao().getById(LinkType.INDEX_PAGE));
				rtn = new ArrayList<>();
				rtn.add(link);
			}
		}
		return rtn;
	}
	
	public CostCenter getOrCreateCostCenter(final PubMap inPub) throws SAXException, IOException {
		String ipdsId = getStringValue(inPub, IpdsMessageLog.COSTCENTERID);
		CostCenter costCenter = null;
		if (null != ipdsId) {
			costCenter = ipdsCostCenterService.getCostCenter(ipdsId);
			if (null == costCenter) {
				costCenter = ipdsCostCenterService.createCostCenter(ipdsId);
			}
		}
		return costCenter;
	}

	protected PublicationSeries getSeriesTitle(PublicationSubtype pubSubtype, PubMap inPub) {
		String usgsSeriesValue = getStringValue(inPub, IpdsMessageLog.USGSSERIESVALUE);
		return getSeriesTitle(pubSubtype, usgsSeriesValue);
	}

	protected PublicationSeries getSeriesTitle(PublicationSubtype pubSubtype, String text) {
		if (null != pubSubtype && null != pubSubtype.getId() && StringUtils.isNotBlank(text)) {
			//Only hit the DB if both fields have values - otherwise the db call will return incorrect results.
			Map<String, Object> filters = new HashMap<>();
			filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, pubSubtype.getId());
			filters.put(PublicationSeriesDao.TEXT_SEARCH, text);
			List<PublicationSeries> pubSeries = PublicationSeries.getDao().getByMap(filters);
			if (!pubSeries.isEmpty()) {
				//We should really only get one, so just take the first...
				return pubSeries.get(0);
			}
		}
		return null;
	}

	protected String getStringValue(PubMap inPub, String key) {
		if (null != inPub && null != inPub.get(key) && StringUtils.isNotBlank(inPub.get(key).toString())) {
			return inPub.get(key).toString().trim();
		} else {
			return null;
		}
	}
}