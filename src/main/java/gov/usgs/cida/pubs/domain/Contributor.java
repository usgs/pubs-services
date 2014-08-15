package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

public class Contributor<D> extends BaseDomain<Contributor<D>> {

    private static IDao<Contributor<?>> contributorDao;

    @JsonIgnore
    //TODO cross-field validations
    protected boolean corporation;

    @JsonIgnore
    //TODO cross-field validations
    protected boolean usgs;

    public boolean isCorporation() {
        return corporation;
    }

    public boolean isUsgs() {
        return usgs;
    }

    @Override
    @JsonProperty("contributorId")
    @JsonView(IMpView.class)
    public Integer getId() {
        return id;
    }

    @Override
    @JsonProperty("contributorId")
    @JsonView(IMpView.class)
    public void setId(final String inId) {
        id = PubsUtilities.parseInteger(inId);
    }

    @JsonProperty("id")
    @JsonView(ILookupView.class)
    public Integer getLookupId() {
        return id;
    }

    /**
     * @return the contributorDao
     */
    public static IDao<Contributor<?>> getDao() {
        return contributorDao;
    }

    /**
     * The setter for contributorDao.
     * @param inContributorDao the contributorDao to set
     */
    public void setContributorDao(final IDao<Contributor<?>> inContributorDao) {
        contributorDao = inContributorDao;
    }

}
