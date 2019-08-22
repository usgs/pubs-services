package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.utility.DataNormalizationUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.dao.intfc.IPersonContributorDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

@Component
@ParentExists
public class PersonContributor<D> extends Contributor<PersonContributor<D>> implements ILookup {
	private static final String ORCID_VALIDATION_REGEX = ".*" + DataNormalizationUtils.ORCID_REGEX;
	private static final String ORCID_VALIDATION_MESS = "The value of orcid=${validatedValue} must include " + 
	                             "16 digits [0-9] separated into groups of 4 by hyphens, final character optionally the letter X";

	private static IPersonContributorDao personContributorDao;

	@JsonProperty("family")
	@JsonView(View.PW.class)
	@NotNull
	@Length(min=1, max=40)
	private String family;

	@JsonProperty("given")
	@JsonView(View.PW.class)
	@Length(min=0, max=40)
	private String given;

	@JsonProperty("suffix")
	@JsonView(View.PW.class)
	@Length(min=0, max=40)
	private String suffix;

	@JsonProperty("email")
	@JsonView(View.PW.class)
	@Length(min=0, max=400)
	@Email
	private String email;

	@Pattern(regexp=ORCID_VALIDATION_REGEX, message=ORCID_VALIDATION_MESS)
	private String orcid;

	@JsonProperty("affiliations")
	@JsonView(View.PW.class)
	private Set<Affiliation<? extends Affiliation<?>>> affiliations;

	@JsonProperty("preferred")
	@JsonView({View.PW.class, View.Lookup.class})
	protected Boolean preferred;

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

	public String getOrcid() {
		return orcid;
	}

	@JsonProperty("orcid")
	@JsonView(View.PW.class)
	public String getDenormalizeOrcid() {
		String formattedOrcid = DataNormalizationUtils.denormalizeOrcid(orcid);
		return formattedOrcid == null ? orcid : formattedOrcid;
	}

	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}

	public Collection<Affiliation<? extends Affiliation<?>>> getAffiliations() {
		if (null == affiliations) {
			affiliations = new HashSet<>();
		}
		return affiliations;
	}

	public void setAffiliations(Set<Affiliation<? extends Affiliation<?>>> affiliations) {
		this.affiliations = affiliations;
	}

	public Boolean isPreferred() {
		return preferred;
	}

	public void setPreferred(final Boolean inPreferred) {
		preferred = inPreferred;
	}

	@Override
	public String getText() {
		StringBuilder text = new StringBuilder();
		if (StringUtils.isNotBlank(family)) {
			text.append(family);
			if (StringUtils.isNotBlank(given) || StringUtils.isNotBlank(suffix)) {
				text.append(",");
			}
		}
		if (StringUtils.isNotBlank(given)) {
			text.append(" ").append(given);
		}
		if (StringUtils.isNotBlank(suffix)) {
			text.append(" ").append(suffix);
		}
		if (StringUtils.isNotBlank(email)) {
			text.append(" ").append(email);
		}
		return text.toString();
	}

	public static IPersonContributorDao getDao() {
		return personContributorDao;
	}

	@Autowired
	@Qualifier("personContributorDao")
	public void setPersonContributorDao(final IPersonContributorDao inPersonContributorDao) {
		personContributorDao = inPersonContributorDao;
	}
}