package gov.usgs.cida.pubs.busservice.ipds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;

@Service
public class IpdsOutsideContributorService {
	
	private static final Logger LOG = LoggerFactory.getLogger(IpdsOutsideContributorService.class);

	private final IpdsParserService parser;
	private final IBusService<OutsideAffiliation> outsideAffiliationBusService;
	private final IBusService<PersonContributor<?>> personContributorBusService;

	@Autowired
	public IpdsOutsideContributorService(IpdsParserService parser,
			@Qualifier("outsideAffiliationBusService")
			IBusService<OutsideAffiliation> outsideAffiliationBusService,
			@Qualifier("personContributorBusService") IBusService<PersonContributor<?>> personContributorBusService) {
		this.parser = parser;
		this.outsideAffiliationBusService = outsideAffiliationBusService;
		this.personContributorBusService = personContributorBusService;
	}

	public OutsideContributor getContributor(final Element element) {
		OutsideContributor contributor = null;
		Map<String, Object> filters = getSearchFilters(element);
		List<Contributor<?>> contributors = OutsideContributor.getDao().getByMap(filters);
		if (!contributors.isEmpty()) {
			if (contributors.size() > 1) {
				LOG.warn("Multiple OutsideContributors found for: " + filters.get(PersonContributorDao.FAMILY) + ", " + filters.get(PersonContributorDao.GIVEN));
			}
			contributor = (OutsideContributor) contributors.get(0);
		}
		return contributor;
	}

	private Map<String, Object> getSearchFilters(final Element element) {
		Map<String, Object> filters = new HashMap<>();
		
		String[] familyGiven = getContributorName(element);
		
		filters.put(PersonContributorDao.FAMILY, familyGiven[0]);
		filters.put(PersonContributorDao.GIVEN, familyGiven[1]);
		filters.put(PersonContributorDao.USGS, false);
		return filters;
	}

	public OutsideContributor createContributor(final Element element) {
		OutsideContributor contributor = new OutsideContributor();
		String[] familyGiven = getContributorName(element);
		contributor.setFamily(familyGiven[0]);
		contributor.setGiven(familyGiven[1]);
		
		contributor = (OutsideContributor) personContributorBusService.createObject(contributor);
		
		String affiliationName = parser.getFirstNodeText(element, "d:NonUSGSAffiliation");
		OutsideAffiliation outsideAffiliation = getOutsideAffiliation(affiliationName);
		if (null == outsideAffiliation) {
			outsideAffiliation = createOutsideAffiliation(affiliationName);
		}
		contributor.getAffiliations().add(outsideAffiliation);
		return contributor;
	}

	private String[] getContributorName(final Element element) {
		String[] familyGiven = new String[] {null, null};
		String contributorName = parser.getFirstNodeText(element, "d:AuthorNameText");
		String[] nameParts = contributorName.split(",");
		
		if (0 < nameParts.length) {
			familyGiven[0] = nameParts[0].trim();
		}
		if (1 < nameParts.length) {
			familyGiven[1] = nameParts[1].trim();
		}
		return familyGiven;
	}

	protected OutsideAffiliation getOutsideAffiliation(final String affiliationName) {
		OutsideAffiliation affiliation = null;
		if (null != affiliationName) {
			Map<String, Object> filters = new HashMap<>();
			filters.put(BaseDao.TEXT_SEARCH, affiliationName);
			filters.put(AffiliationDao.USGS_SEARCH, false);
			List<? extends Affiliation<?>> affiliations = Affiliation.getDao().getByMap(filters);
			if (!affiliations.isEmpty()) {
				if (affiliations.size() > 1) {
					LOG.warn("Multiple OutsideAffiliation found for: " + affiliationName);
				}
				affiliation = (OutsideAffiliation) affiliations.get(0);
			}
		}
		return affiliation;
	}

	protected OutsideAffiliation createOutsideAffiliation(final String affiliationName) {
		OutsideAffiliation affiliation = new OutsideAffiliation();
		affiliation.setText(affiliationName);
		return outsideAffiliationBusService.createObject(affiliation);
	}
}