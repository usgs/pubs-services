package gov.usgs.cida.pubs.domain;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

public class PersonContributor<D> extends Contributor<PersonContributor<D>> implements ILookup {

    private static IDao<Contributor<?>> personContributorDao;

    @JsonProperty("family")
    @JsonView(IMpView.class)
    @Length(min=0, max=40)
    private String family;

    @JsonProperty("given")
    @JsonView(IMpView.class)
    @Length(min=0, max=40)
    private String given;

    @JsonProperty("suffix")
    @JsonView(IMpView.class)
    @Length(min=0, max=14)
    private String suffix;

    @JsonProperty("email")
    @JsonView(IMpView.class)
    @Length(min=0, max=400)
    private String email;

    @JsonProperty("affiliation")
    @JsonView(IMpView.class)
    //TODO parent exists validation
    private Affiliation<?> affiliation;

    @JsonIgnore
    private Integer ipdsContributorId;

    public String getFamily() {
        return family;
    }

    public void setFamily(final String inFamily) {
        family = inFamily;
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

    public Affiliation<?> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(final Affiliation<?> inAffiliation) {
        affiliation = inAffiliation;
    }

    public Integer getIpdsContributorId() {
        return ipdsContributorId;
    }

    public void setIpdsContributorId(final Integer inIpdsContributorId) {
        ipdsContributorId = inIpdsContributorId;
    }

    @Override
    @JsonView(ILookupView.class)
    public String getText() {
    	StringBuilder text = new StringBuilder();
    	if (StringUtils.isNotEmpty(family)) {
    		text.append(family);
    	}
    	if (StringUtils.isNotEmpty(given)) {
    		text.append(" ").append(given);
    	}
    	if (StringUtils.isNotEmpty(suffix)) {
    		text.append(" ").append(suffix);
    	}
    	if (StringUtils.isNotEmpty(email)) {
    		text.append(" ").append(email);
    	}
        return text.toString();
    }


    public static IDao<Contributor<?>> getDao() {
        return personContributorDao;
    }

    public void setPersonContributorDao(final IDao<Contributor<?>> inPersonContributorDao) {
        personContributorDao = inPersonContributorDao;
    }

}
