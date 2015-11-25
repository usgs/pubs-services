package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.domain.intfc.ILookup;

public class UsgsContributor extends PersonContributor<UsgsContributor> implements ILookup {

    public UsgsContributor() {
        corporation = false;
        usgs = true;
    }

}
