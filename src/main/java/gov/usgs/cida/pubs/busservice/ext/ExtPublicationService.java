package gov.usgs.cida.pubs.busservice.ext;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

@Service
public class ExtPublicationService {

	private final ExtAffiliationBusService extAffiliationBusService;
	private final ExtPublicationContributorService extPublicationContributorBusService;
	private final IMpPublicationBusService pubBusService;

	@Autowired
	public ExtPublicationService (
			ExtAffiliationBusService extAffiliationBusService,
			ExtPublicationContributorService extPublicationContributorBusService,
			IMpPublicationBusService pubBusService
			) {
		this.extAffiliationBusService = extAffiliationBusService;
		this.extPublicationContributorBusService = extPublicationContributorBusService;
		this.pubBusService = pubBusService;
	}

	public MpPublication create(MpPublication mpPublication) {
		extPublicationContributorBusService.processPublicationContributors(mpPublication.getContributors());
		processCostCenters(mpPublication.getCostCenters());

		return pubBusService.createObject(mpPublication);
	}

	protected void processCostCenters(Collection<PublicationCostCenter<?>> publicationCostCenters) {
		if (!publicationCostCenters.isEmpty()) {
			for (PublicationCostCenter<?> publicationCostCenter : publicationCostCenters) {
				processPublicationCostCenter(publicationCostCenter);
			}
		}
	}

	protected void processPublicationCostCenter(PublicationCostCenter<?> publicationCostCenter) {
		publicationCostCenter.setCostCenter(
				extAffiliationBusService.processCostCenter(publicationCostCenter.getCostCenter()));
	}
}
