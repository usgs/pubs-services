package gov.usgs.cida.pubs.busservice;

import javax.validation.Validator;

import gov.usgs.cida.pubs.domain.CorporateContributor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class CorporateContributorBusService extends BusService<CorporateContributor> {

	@Autowired
	CorporateContributorBusService(final Validator validator) {
		this.validator = validator;
	}

    @Override
    @Transactional
    public CorporateContributor createObject(final CorporateContributor object) {
        //TODO provide logic similar to the PersonContributorBusService
        return null;
    }

}
