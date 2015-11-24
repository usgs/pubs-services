package gov.usgs.cida.pubs.domain;

import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.View;

public class UsgsContributor extends PersonContributor<UsgsContributor> implements ILookup {

    public UsgsContributor() {
        corporation = false;
        usgs = true;
    }

    @Override
    @JsonView({View.Lookup.class, View.PW.class})
    public String getText() {
    	return super.getText();
    }

}
