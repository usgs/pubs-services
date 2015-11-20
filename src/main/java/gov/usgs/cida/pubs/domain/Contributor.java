package gov.usgs.cida.pubs.domain;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.utility.PubsUtilities;

public class Contributor<D> extends BaseDomain<Contributor<D>> {

    private static IDao<Contributor<?>> contributorDao;

    @JsonProperty("corporation")
    @JsonView(View.PW.class)
    @NotNull
    //TODO cross-field validations
    protected Boolean corporation;

    @JsonProperty("usgs")
    @JsonView(View.PW.class)
    @NotNull
    //TODO cross-field validations
    protected Boolean usgs;

    public Boolean isCorporation() {
        return corporation;
    }

    public Boolean isUsgs() {
        return usgs;
    }

    @Override
    @JsonProperty("contributorId")
    @JsonView(View.PW.class)
    public Integer getId() {
        return id;
    }

    @Override
    @JsonProperty("contributorId")
    @JsonView(View.PW.class)
    public void setId(final String inId) {
        id = PubsUtilities.parseInteger(inId);
    }

    @JsonProperty("id")
	@JsonView(View.Lookup.class)
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
