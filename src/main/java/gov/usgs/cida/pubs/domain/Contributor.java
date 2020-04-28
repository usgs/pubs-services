package gov.usgs.cida.pubs.domain;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.utility.PubsUtils;
import io.swagger.v3.oas.annotations.media.Schema;

@Component
public class Contributor<D> extends BaseDomain<Contributor<D>> {

	private static IDao<Contributor<?>> contributorDao;

	@JsonProperty("corporation")
	@JsonView({View.PW.class, View.Lookup.class})
	@NotNull
	//TODO cross-field validations
	protected Boolean corporation;

	@JsonProperty("usgs")
	@JsonView({View.PW.class, View.Lookup.class})
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
		id = PubsUtils.parseInteger(inId);
	}

	@JsonProperty("id")
	@JsonView(View.Lookup.class)
	public Integer getLookupId() {
		return id;
	}

	public static IDao<Contributor<?>> getDao() {
		return contributorDao;
	}

	@Autowired
	@Qualifier("contributorDao")
	@Schema(hidden = true)
	public void setContributorDao(final IDao<Contributor<?>> inContributorDao) {
		contributorDao = inContributorDao;
	}

}
