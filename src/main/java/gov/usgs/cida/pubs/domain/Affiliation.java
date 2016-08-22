package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.View;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

@Component
//@UniqueKey(message = "{affiliation.name.duplicate}")
//@NoChildren(groups = DeleteChecks.class)
@JsonPropertyOrder({"id", "text", "active", "usgs"})
public class Affiliation<D extends Affiliation<D>> extends BaseDomain<D> implements ILookup {

	private static IDao<? extends Affiliation<?>> affiliationDao;

	@JsonProperty("text")
	@Length(min=1, max=500)
	private String text;

	@JsonProperty("active")
	@JsonView(View.PW.class)
	@NotNull
	protected Boolean active;

	@JsonProperty("usgs")
	@JsonView(View.PW.class)
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

	public static IDao<? extends Affiliation<?>> getDao() {
		return affiliationDao;
	}

	@Autowired
	@Qualifier("affiliationDao")
	public void setAffiliationDao(final IDao<D> inAffiliationDao) {
		affiliationDao = inAffiliationDao;
	}
}