package gov.usgs.cida.pubs.busservice.ipds;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Service
public class IpdsPubContributorService {
	private static final Logger LOG = LoggerFactory.getLogger(IpdsPubContributorService.class);

	private final IpdsParserService parser;
	private final IpdsUsgsContributorService ipdsUsgsContributorService;
	private final IpdsOutsideContributorService ipdsOutsideContributorService;

	@Autowired
	public IpdsPubContributorService(IpdsParserService parser,
			IpdsUsgsContributorService ipdsUsgsContributorService,
			IpdsOutsideContributorService ipdsOutsideContributorService) {
		this.parser = parser;
		this.ipdsUsgsContributorService = ipdsUsgsContributorService;
		this.ipdsOutsideContributorService = ipdsOutsideContributorService;
	}

	protected MpPublicationContributor buildPublicationContributor(final Node entry, String context) throws SAXException, IOException {
		MpPublicationContributor rtn = new MpPublicationContributor();
		LOG.debug("\nCurrent Element :" + entry.getNodeName());

		if (entry.getNodeType() == Node.ELEMENT_NODE) {
			Element authorsItem = (Element) entry;
			rtn.setContributor(bindContributor(authorsItem, context));
			rtn.setContributorType(bindContributorType(authorsItem));
			rtn.setRank(bindRank(authorsItem));
		}
		return rtn;
	}

	protected PersonContributor<?> bindContributor(Element authorsItem, String context) throws SAXException, IOException {
		PersonContributor<?> contributor;
		String ipdsContributorId = parser.getFirstNodeText(authorsItem, Schema.AUTHOR_NAME_ID);
		if (null == ipdsContributorId) {
			contributor = ipdsOutsideContributorService.getContributor(authorsItem);
			if (null == contributor) {
				contributor = ipdsOutsideContributorService.createContributor(authorsItem);
			}
		} else {
			contributor = ipdsUsgsContributorService.getContributor(authorsItem, context);
			if (null == contributor) {
				contributor = ipdsUsgsContributorService.createContributor(authorsItem, context);
			}
		}
		return contributor;
	}

	protected ContributorType bindContributorType(Element authorsItem) {
		if ("1".equalsIgnoreCase(parser.getFirstNodeText(authorsItem, Schema.CONTRIBUTOR_ROLE))) {
			return ContributorType.getDao().getById(ContributorType.AUTHORS);
		} else {
			return ContributorType.getDao().getById(ContributorType.EDITORS);
		}
	}

	protected Integer bindRank(Element authorsItem) {
		Integer rank = PubsUtilities.parseInteger(authorsItem.getElementsByTagName(Schema.RANK).item(0).getTextContent());
		if (null == rank) {
			return 1;
		} else {
			return rank;
		}
	}
}