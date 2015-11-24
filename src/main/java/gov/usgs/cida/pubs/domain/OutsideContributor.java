package gov.usgs.cida.pubs.domain;

import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.View;

public class OutsideContributor extends PersonContributor<OutsideContributor> implements ILookup {

    public OutsideContributor() {
        corporation = false;
        usgs = false;
    }

    @Override
    @JsonView({View.Lookup.class, View.PW.class})
    public String getText() {
    	return super.getText();
    }

}
