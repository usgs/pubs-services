package gov.usgs.cida.pubs.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.utility.PubsUtilities;

public class Contributor extends BaseDomain<Contributor> implements ILookup {

    private static IDao<Contributor> contributorDao;

    @JsonProperty("family")
    @JsonView(IMpView.class)
    private String first;

    @JsonProperty("given")
    @JsonView(IMpView.class)
    private String given;

    @JsonProperty("suffix")
    @JsonView(IMpView.class)
    private String suffix;

    @JsonProperty("email")
    @JsonView(IMpView.class)
    private String email;

    @JsonProperty("affiliation")
    @JsonView(IMpView.class)
    private CostCenter affiliation;

    @JsonProperty("organization")
    @JsonView(IMpView.class)
    private String literal;

    public String getFirst() {
        return first;
    }

    public void setFirst(final String inFirst) {
        first = inFirst;
    }

    public String getGiven() {
        return given;
    }

    public void setGiven(final String inGiven) {
        given = inGiven;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String inSuffix) {
        suffix = inSuffix;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String inEmail) {
        email = inEmail;
    }

    public CostCenter getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(final CostCenter inAffiliation) {
        affiliation = inAffiliation;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(final String inLiteral) {
        literal = inLiteral;
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

    @Override
    @JsonView(ILookupView.class)
    public String getText() {
        if (null == literal) {
            return first + " " + given + " " + suffix + " " + email;
        } else {
            return literal;
        }
    }

    /**
     * @return the contributorDao
     */
    public static IDao<Contributor> getDao() {
        return contributorDao;
    }

    /**
     * The setter for contributorDao.
     * @param inContributorDao the contributorDao to set
     */
    public void setContributorDao(final IDao<Contributor> inContributorDao) {
        contributorDao = inContributorDao;
    }

}
