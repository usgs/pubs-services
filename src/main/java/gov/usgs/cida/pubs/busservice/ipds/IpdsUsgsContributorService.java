package gov.usgs.cida.pubs.busservice.ipds;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;

@Service
public class IpdsUsgsContributorService {
	private static final Logger LOG = LoggerFactory.getLogger(IpdsUsgsContributorService.class);

	private final IpdsParserService parser;
	private final IpdsWsRequester requester;
	private final IBusService<PersonContributor<?>> personContributorBusService;
	private final IpdsCostCenterService ipdsCostCenterService;

	@Autowired
	public IpdsUsgsContributorService(IpdsParserService parser,
			IpdsWsRequester requester,
			IpdsCostCenterService ipdsCostCenterService,
			@Qualifier("personContributorBusService") IBusService<PersonContributor<?>> personContributorBusService) {
		this.parser = parser;
		this.requester = requester;
		this.ipdsCostCenterService = ipdsCostCenterService;
		this.personContributorBusService = personContributorBusService;
	}

	public UsgsContributor getContributor(final Element authorsItem, String context) throws SAXException, IOException {
		String orcid = parser.formatOrcid(parser.getFirstNodeText(authorsItem, Schema.ORCID));
		UsgsContributor contributor = null;
		if (null != orcid) {
			contributor = getByORCID(orcid);
		}
		if (null == contributor) {
			contributor = getByEmail(authorsItem, context);
		}
		if (null != contributor) {
			CostCenter costCenter = getCostCenter(authorsItem);
			if (null != costCenter && costCenter.isValid()) {
				contributor = updateAffiliations(contributor, costCenter);
			}
		}
		return contributor;
	}

	protected UsgsContributor getByORCID(final String orcid) {
		UsgsContributor filter = new UsgsContributor();
		filter.setOrcid(orcid);
		List<Contributor<?>> contributors = UsgsContributor.getDao().getByPreferred(filter);
		UsgsContributor contributor = null;
		if (contributors.isEmpty()) {
			LOG.debug("No UsgsContributors found for ORCID: " + filter.getOrcid());
		} else {
			if (contributors.size() > 1) {
				LOG.warn("Multiple UsgsContributors found for ORCID: " + filter.getOrcid());
			}
			contributor = (UsgsContributor) contributors.get(0);
		}
		return contributor;
	}

	protected UsgsContributor getByEmail(final Element authorsItem, final String context) throws SAXException, IOException {
		UsgsContributor contributor = null;
		UsgsContributor filter = bindContributor(authorsItem, context);
		if (null != filter.getEmail()) {
			//save off the orcid & clear it from the filer - otherwise we get get no results.
			String orcid = filter.getOrcid();
			filter.setOrcid(null);
			List<Contributor<?>> contributors = UsgsContributor.getDao().getByPreferred(filter);
			if (contributors.isEmpty()) {
				LOG.debug("No UsgsContributors found for email: " + filter.getEmail());
			} else {
				if (contributors.size() > 1) {
					LOG.warn("Multiple UsgsContributors found for email: " + filter.getEmail());
				}
				contributor = (UsgsContributor) contributors.get(0);
			}
			if (null != contributor && null == contributor.getOrcid() && null != orcid) {
				contributor.setOrcid(orcid);
			}
		}
		return contributor;
	}

	public UsgsContributor createContributor(final Element authorsItem, final String context) throws SAXException, IOException {
		UsgsContributor contributor = bindContributor(authorsItem, context);
		CostCenter costCenter = getCostCenter(authorsItem);
		if (null != costCenter && costCenter.isValid()) {
			contributor.getAffiliations().add(costCenter);
		}
		contributor = (UsgsContributor) personContributorBusService.createObject(contributor);
		return contributor;
	}

	protected UsgsContributor bindContributor(final Element authorsItem, final String context) throws SAXException, IOException {
		String orcid = parser.formatOrcid(parser.getFirstNodeText(authorsItem, Schema.ORCID));
		String ipdsContributorId = parser.getFirstNodeText(authorsItem, Schema.AUTHOR_NAME_ID);
		String contributorXml = requester.getContributor(ipdsContributorId, context);
		UsgsContributor contributor = new UsgsContributor();
		Document doc = parser.makeDocument(contributorXml);
		contributor.setFamily(parser.getFirstNodeText(doc.getDocumentElement(), Schema.LAST_NAME));
		contributor.setGiven(parser.getFirstNodeText(doc.getDocumentElement(), Schema.FIRST_NAME));
		contributor.setEmail(parser.getFirstNodeText(doc.getDocumentElement(), Schema.WORK_EMAIL));
		contributor.setOrcid(orcid);
		contributor.setPreferred(true);
		return contributor;
	}

	protected CostCenter getCostCenter(final Element authorsItem) throws SAXException, IOException {
		CostCenter costCenter = null;
		String costCenterId = parser.getFirstNodeText(authorsItem, Schema.COST_CENTER);
		if (null != costCenterId) {
			costCenter = ipdsCostCenterService.getCostCenter(costCenterId);
			if (null == costCenter) {
				costCenter = ipdsCostCenterService.createCostCenter(costCenterId);
			}
		}
		return costCenter;
	}

	protected UsgsContributor updateAffiliations(UsgsContributor contributor, CostCenter costCenter) {
		if (!contributor.getAffiliations().contains(costCenter)) {
			contributor.getAffiliations().add(costCenter);
			return (UsgsContributor) personContributorBusService.updateObject(contributor);
		} else {
			return contributor;
		}
	}

}