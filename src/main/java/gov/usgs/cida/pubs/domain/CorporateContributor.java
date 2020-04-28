package gov.usgs.cida.pubs.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.View;
import io.swagger.v3.oas.annotations.media.Schema;

@Component
public class CorporateContributor extends Contributor<CorporateContributor> implements ILookup {

	private static IDao<Contributor<?>> corporateContributorDao;

	@JsonProperty("organization")
	@JsonView(View.PW.class)
	@Length(min=1, max=400)
	@NotNull
	private String organization;

	public CorporateContributor() {
		corporation = true;
		usgs = false;
	}

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

	@Autowired
	@Qualifier("corporateContributorDao")
	@Schema(hidden = true)
	public void setCorporateContributorDao(final IDao<Contributor<?>> inCorporateContributorDao) {
		corporateContributorDao = inCorporateContributorDao;
	}

}
