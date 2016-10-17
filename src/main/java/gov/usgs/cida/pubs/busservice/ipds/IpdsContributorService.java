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

	protected MpPublicationContributor buildPublicationContributor(final Node entry) throws SAXException, IOException {
		MpPublicationContributor rtn = new MpPublicationContributor();
		LOG.debug("\nCurrent Element :" + entry.getNodeName());

		if (entry.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) entry;
			PersonContributor<?> contributor;
			String ipdsContributorId = parser.getFirstNodeText(element, "d:AuthorNameId");
			if (null == ipdsContributorId) {
				contributor = ipdsOutsideContributorService.getContributor(element);
				if (null == contributor) {
					contributor = ipdsOutsideContributorService.createContributor(element);
				}
			} else {
				contributor = ipdsUsgsContributorService.getContributor(element);
				if (null == contributor) {
					contributor = ipdsUsgsContributorService.createContributor(element);
				} else {
					String costCenterId = parser.getFirstNodeText(element, "d:CostCenterId");
					if (null != costCenterId) {
						CostCenter costCenter = ipdsCostCenterService.getCostCenter(costCenterId);
						if (null == costCenter) {
							costCenter = ipdsCostCenterService.createCostCenter(costCenterId);
						}
						addAffiliation(contributor, costCenter);
					}
				}
			}
			rtn.setContributor(contributor);
			if ("Author".equalsIgnoreCase(parser.getFirstNodeText(element, "d:ContentType"))) {
				rtn.setContributorType(ContributorType.getDao().getById(ContributorType.AUTHORS));
			} else {
				rtn.setContributorType(ContributorType.getDao().getById(ContributorType.EDITORS));
			}
			Integer rank = PubsUtilities.parseInteger(element.getElementsByTagName("d:Rank").item(0).getTextContent());
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
			contributor = personContributorBusService.updateObject(contributor);
		}
	}
}