package gov.usgs.cida.pubs.busservice;

import gov.usgs.cida.pubs.domain.CorporateContributor;

import org.springframework.transaction.annotation.Transactional;

public class CorporateContributorBusService extends BusService<CorporateContributor> {

    @Override
    @Transactional
    public CorporateContributor createObject(final CorporateContributor object) {
        //TODO provide logic similar to the PersonContributorBusService
        return null;
    }

}
