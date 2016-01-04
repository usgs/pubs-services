package gov.usgs.cida.pubs.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;

@Component
@JsonPropertyOrder({"id", "text", "active", "usgs"})
public class Affiliation<D> extends BaseDomain<Affiliation<D>> implements ILookup {

    private static IDao<Affiliation<?>> affiliationDao;

    @JsonProperty("text")
	@Length(min=1, max=500)
    private String text;

    @JsonProperty("active")
    @NotNull
    protected Boolean active;

    @JsonProperty("usgs")
    @NotNull
    protected Boolean usgs;

    @Override
    public String getText() {
        return text;
    }

    public void setText(final String inText) {
        text = inText;
    }

    public Boolean isActive() {
        return active;
    }

    public Boolean isUsgs() {
        return usgs;
    }

    public static IDao<Affiliation<?>> getDao() {
        return affiliationDao;
    }

    @Autowired
    @Qualifier("affiliationDao")
    public void setAffiliationDao(final IDao<Affiliation<?>> inAffiliationDao) {
        affiliationDao = inAffiliationDao;
    }

}
