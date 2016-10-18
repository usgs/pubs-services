package gov.usgs.cida.pubs.busservice.ipds;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Service
public class IpdsUsgsContributorService {

	private static final Logger LOG = LoggerFactory.getLogger(IpdsUsgsContributorService.class);

	private final IpdsParserService parser;
	private final IpdsWsRequester requester;
	private final IBusService<PersonContributor<?>> personContributorBusService;

	@Autowired
	public IpdsUsgsContributorService(IpdsParserService parser, IpdsWsRequester requester, @Qualifier("personContributorBusService") IBusService<PersonContributor<?>> personContributorBusService) {
		this.parser = parser;
		this.requester = requester;
		this.personContributorBusService = personContributorBusService;
	}

	public UsgsContributor getContributor(final Element element) throws SAXException, IOException {
		String ipdsContributorId = parser.getFirstNodeText(element, "d:AuthorNameId");
		UsgsContributor contributor = null;
		Map<String, Object> filters = new HashMap<>();
		filters.put(PersonContributorDao.IPDS_CONTRIBUTOR_ID, ipdsContributorId);
		filters.put(PersonContributorDao.USGS, true);
		List<Contributor<?>> contributors = UsgsContributor.getDao().getByMap(filters);
		if (!contributors.isEmpty()) {
			if (contributors.size() > 1) {
				LOG.warn("Multiple UsgsContributors found for ipdsId: " + filters.get(PersonContributorDao.IPDS_CONTRIBUTOR_ID));
			}
			contributor = (UsgsContributor) contributors.get(0);
		}
		return contributor;
	}

	public UsgsContributor createContributor(final Element element) throws SAXException, IOException {
		String ipdsContributorId = parser.getFirstNodeText(element, "d:AuthorNameId");
		String contributorXml = requester.getContributor(ipdsContributorId);
		UsgsContributor contributor = bindContributor(contributorXml);
		contributor = (UsgsContributor) personContributorBusService.createObject(contributor);
		return contributor;
	}

	protected UsgsContributor bindContributor(String contributorXml) throws SAXException, IOException {
		UsgsContributor contributor = new UsgsContributor();
		Document doc = parser.makeDocument(contributorXml);
		contributor.setIpdsContributorId(PubsUtilities.parseInteger(parser.getFirstNodeText(doc.getDocumentElement(), "d:Id")));
		contributor.setFamily(parser.getFirstNodeText(doc.getDocumentElement(), "d:LastName"));
		contributor.setGiven(parser.getFirstNodeText(doc.getDocumentElement(), "d:FirstName"));
		contributor.setEmail(parser.getFirstNodeText(doc.getDocumentElement(), "d:WorkEMail"));
		return contributor;
	}
}