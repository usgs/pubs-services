package gov.usgs.cida.pubs.domain;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.View;

public class CorporateContributor extends Contributor<CorporateContributor> implements ILookup {

    private static IDao<Contributor<?>> corporateContributorDao;

    public CorporateContributor() {
        corporation = true;
        usgs = false;
    }

    @JsonProperty("organization")
    @JsonView(View.PW.class)
    @Length(min=1, max=400)
    private String organization;

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(final String inOrganization) {
        organization = inOrganization;
    }

    @Override
    public String getText() {
        return organization;
    }

    public static IDao<Contributor<?>> getDao() {
        return corporateContributorDao;
    }

    public void setCorporateContributorDao(final IDao<Contributor<?>> inCorporateContributorDao) {
        corporateContributorDao = inCorporateContributorDao;
    }

}
