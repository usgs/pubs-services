package gov.usgs.cida.pubs.busservice.ext;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;

@Service
public class ExtAffiliationBusService {
	private static final Logger LOG = LoggerFactory.getLogger(ExtAffiliationBusService.class);

	private final IBusService<CostCenter> costCenterBusService;
	private final IBusService<OutsideAffiliation> outsideAffiliationBusService;

	@Autowired
	public ExtAffiliationBusService (
			@Qualifier("costCenterBusService") IBusService<CostCenter> costCenterBusService,
			@Qualifier("outsideAffiliationBusService") IBusService<OutsideAffiliation> outsideAffiliationBusService
			) {
		this.costCenterBusService = costCenterBusService;
		this.outsideAffiliationBusService = outsideAffiliationBusService;
	}

	public CostCenter processCostCenter(CostCenter costCenter) {
		CostCenter persistedCostCenter = null;
		if (null != costCenter) {
			persistedCostCenter = getCostCenter(costCenter);
			if (null == persistedCostCenter) {
				persistedCostCenter = createCostCenter(costCenter);
			}
		}
		return persistedCostCenter;
	}

	protected CostCenter createCostCenter(CostCenter costCenter) {
		CostCenter persistedCostCenter = null;
		if (StringUtils.isNotBlank(costCenter.getText())) {
			persistedCostCenter = costCenterBusService.createObject(costCenter);
		}
		return persistedCostCenter;
	}

	protected CostCenter getCostCenter(CostCenter costCenter) {
		CostCenter persistedCostCenter = null;
		if (StringUtils.isNotBlank(costCenter.getText())) {
			String name = costCenter.getText();
			Map<String, Object> filters = new HashMap<>();
			filters.put(AffiliationDao.EXACT_SEARCH, name);
			List<CostCenter> costCenters = costCenterBusService.getObjects(filters);
			if (costCenters.size() > 1) {
				LOG.warn("Multiple costCenters found for name: {}", name);
			}
			if (!costCenters.isEmpty()) {
				persistedCostCenter = costCenters.get(0);
			}
		}
		return persistedCostCenter;
	}

	public Set<Affiliation<? extends Affiliation<?>>> processAffiliations(Collection<Affiliation<? extends Affiliation<?>>> affiliations) {
		Set<Affiliation<? extends Affiliation<?>>> persistedAffiliations = new HashSet<>();
		if (!affiliations.isEmpty()) {
			for (Affiliation<?> affiliation : affiliations) {
				Affiliation<?> processed = null;
				if (affiliation instanceof CostCenter) {
					processed = processCostCenter((CostCenter) affiliation);
				} else if (affiliation instanceof OutsideAffiliation) {
					processed = processOutsideAffiliation((OutsideAffiliation) affiliation);
				}
				if (null != processed) {
					persistedAffiliations.add(processed);
				}
			}
			return persistedAffiliations;
		}
		return null;
	}

	protected OutsideAffiliation processOutsideAffiliation(OutsideAffiliation outsideAffiliation) {
		OutsideAffiliation persistedOutsideAffiliation = null;
		if (null != outsideAffiliation) {
			persistedOutsideAffiliation = getOutsideAffiliation(outsideAffiliation);
			if (null == persistedOutsideAffiliation) {
				persistedOutsideAffiliation = createOutsideAffiliation(outsideAffiliation);
			}
		}
		return persistedOutsideAffiliation;
	}

	protected OutsideAffiliation createOutsideAffiliation(OutsideAffiliation outsideAffiliation) {
		OutsideAffiliation persistedOutsideAffiliation = null;
		if (StringUtils.isNotBlank(outsideAffiliation.getText())) {
			persistedOutsideAffiliation = outsideAffiliationBusService.createObject(outsideAffiliation);
		}
		return persistedOutsideAffiliation;
	}

	protected OutsideAffiliation getOutsideAffiliation(OutsideAffiliation outsideAffiliation) {
		OutsideAffiliation persistedOutsideAffiliation = null;
		if (StringUtils.isNotBlank(outsideAffiliation.getText())) {
			String name = outsideAffiliation.getText();
			Map<String, Object> filters = new HashMap<>();
			filters.put(AffiliationDao.EXACT_SEARCH, name);
			filters.put(AffiliationDao.USGS_SEARCH, false);
			List<? extends Affiliation<?>> affiliations = outsideAffiliationBusService.getObjects(filters);
			if (!affiliations.isEmpty()) {
				if (affiliations.size() > 1) {
					LOG.warn("Multiple OutsideAffiliation found for: {}", name);
				}
				persistedOutsideAffiliation = (OutsideAffiliation) affiliations.get(0);
			}
		}
		return persistedOutsideAffiliation;
	}
}
