package gov.usgs.cida.pubs.busservice.ext;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;

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
		ValidationResults validationErrors = new ValidationResults();
		validationErrors.addValidationResults(extPublicationContributorBusService.processPublicationContributors(mpPublication.getContributors()));
		validationErrors.addValidationResults(processCostCenters(mpPublication.getCostCenters()));
		if (validationErrors.isValid()) {
			mpPublication = pubBusService.createObject(mpPublication);
		}
		mpPublication.addValidationResults(validationErrors);
		return mpPublication;
	}

	protected ValidationResults processCostCenters(Collection<PublicationCostCenter<?>> publicationCostCenters) {
		ValidationResults validationErrors = new ValidationResults();
		if (!publicationCostCenters.isEmpty()) {
			for (PublicationCostCenter<?> publicationCostCenter : publicationCostCenters) {
				validationErrors.addValidationResults(processPublicationCostCenter(publicationCostCenter));
			}
		}
		return validationErrors;
	}

	protected ValidationResults processPublicationCostCenter(PublicationCostCenter<?> publicationCostCenter) {
		CostCenter costCenter = extAffiliationBusService.processCostCenter(publicationCostCenter.getCostCenter());
		publicationCostCenter.setCostCenter(costCenter);
		return costCenter.getValidationErrors();
	}
}
