package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.domain.intfc.ILookup;

public class OutsideContributor extends PersonContributor<OutsideContributor> implements ILookup {

	public OutsideContributor() {
		corporation = false;
		usgs = false;
	}

}
