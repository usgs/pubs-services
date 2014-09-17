package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

@JsonPropertyOrder({"id", "text", "active", "usgs"})
public class Affiliation<D> extends BaseDomain<Affiliation<D>> implements ILookup {

    private static IDao<Affiliation<?>> affiliationDao;

    @JsonProperty("text")
	@Length(min=1, max=500)
    private String text;

    @JsonProperty("active")
    @NotNull
    protected boolean active;

    @JsonProperty("usgs")
    @NotNull
    protected boolean usgs;

    @Override
    @JsonView({ILookupView.class, IMpView.class})
    public String getText() {
        return text;
    }

    public void setText(final String inText) {
        text = inText;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isUsgs() {
        return usgs;
    }

    public static IDao<Affiliation<?>> getDao() {
        return affiliationDao;
    }

    public void setAffiliationDao(final IDao<Affiliation<?>> inAffiliationDao) {
        affiliationDao = inAffiliationDao;
    }

}
