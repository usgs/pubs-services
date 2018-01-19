package gov.usgs.cida.pubs.busservice.ipds;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Service
public class IpdsContributorService {
	private static final Logger LOG = LoggerFactory.getLogger(IpdsContributorService.class);

	private final IpdsParserService parser;
	private final IpdsCostCenterService ipdsCostCenterService;
	private final IpdsUsgsContributorService ipdsUsgsContributorService;
	private final IBusService<PersonContributor<?>> personContributorBusService;
	private final IpdsOutsideContributorService ipdsOutsideContributorService;

	@Autowired
	public IpdsContributorService(IpdsParserService parser,
			IpdsCostCenterService ipdsCostCenterService,
			IpdsUsgsContributorService ipdsUsgsContributorService,
			@Qualifier("personContributorBusService")
			IBusService<PersonContributor<?>> personContributorBusService,
			IpdsOutsideContributorService ipdsOutsideContributorService) {
		this.parser = parser;
		this.ipdsCostCenterService = ipdsCostCenterService;
		this.ipdsUsgsContributorService = ipdsUsgsContributorService;
		this.personContributorBusService = personContributorBusService;
		this.ipdsOutsideContributorService = ipdsOutsideContributorService;
	}

	protected MpPublicationContributor buildPublicationContributor(final Node entry, String context) throws SAXException, IOException {
		MpPublicationContributor rtn = new MpPublicationContributor();
		LOG.debug("\nCurrent Element :" + entry.getNodeName());

		if (entry.getNodeType() == Node.ELEMENT_NODE) {
			Element authorsItem = (Element) entry;
			PersonContributor<?> contributor;
			boolean updateContributor = true;
			String ipdsContributorId = parser.getFirstNodeText(authorsItem, Schema.AUTHOR_NAME_ID);
			if (null == ipdsContributorId) {
				contributor = ipdsOutsideContributorService.getContributor(authorsItem);
				if (null == contributor) {
					contributor = ipdsOutsideContributorService.createContributor(authorsItem);
					updateContributor = false;
				}
			} else {
				contributor = ipdsUsgsContributorService.getContributor(authorsItem, context);
				if (null == contributor) {
					contributor = ipdsUsgsContributorService.createContributor(authorsItem, context);
					updateContributor = false;
				} else {
					String costCenterId = parser.getFirstNodeText(authorsItem, Schema.COST_CENTER_ID);
					if (null != costCenterId) {
						CostCenter costCenter = ipdsCostCenterService.getCostCenter(costCenterId);
						if (null == costCenter) {
							costCenter = ipdsCostCenterService.createCostCenter(costCenterId);
						}
						if (costCenter.isValid()) {
							addAffiliation(contributor, costCenter);
						} else {
							LOG.info("Unable to get cost center: " + costCenter.getIpdsId());
						}
					}
				}
			}
			if (updateContributor) {
				contributor = personContributorBusService.updateObject(contributor);
			}
			rtn.setContributor(contributor);
			if ("Author".equalsIgnoreCase(parser.getFirstNodeText(authorsItem, Schema.CONTENT_TYPE))) {
				rtn.setContributorType(ContributorType.getDao().getById(ContributorType.AUTHORS));
			} else {
				rtn.setContributorType(ContributorType.getDao().getById(ContributorType.EDITORS));
			}
			Integer rank = PubsUtilities.parseInteger(authorsItem.getElementsByTagName(Schema.RANK).item(0).getTextContent());
			if (null == rank) {
				rtn.setRank(1);
			} else {
				rtn.setRank(rank);
			}
		}
		return rtn;
	}

	private void addAffiliation(PersonContributor<?> contributor, Affiliation<? extends Affiliation<?>> affiliation) {
		if (!contributor.getAffiliations().contains(affiliation)) {
			contributor.getAffiliations().add(affiliation);
		}
	}
}