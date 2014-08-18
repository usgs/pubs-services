package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

public class Affiliation<D> extends BaseDomain<Affiliation<D>> implements ILookup {

    private static IDao<Affiliation<?>> affiliationDao;

    @JsonProperty("name")
    private String name;

    @JsonIgnore
    private boolean active;

    @JsonIgnore
    protected boolean usgs;

    public String getName() {
        return name;
    }

    public void setName(final String inName) {
        name = inName;
    }

    public boolean isActive() {
        return active;
    }

    public boolean getUsgs() {
        return usgs;
    }

    public static IDao<Affiliation<?>> getDao() {
        return affiliationDao;
    }

    public void setAffiliationDao(final IDao<Affiliation<?>> inAffiliationDao) {
        affiliationDao = inAffiliationDao;
    }

    @Override
    @JsonView({ILookupView.class, IMpView.class})
    public String getText() {
        return name;
    }

}
