package gov.usgs.cida.pubs.domain;

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
import gov.usgs.cida.pubs.utility.DataNormalizationUtils;
import gov.usgs.cida.pubs.validation.PayloadSeverityLevel.FATAL;
import gov.usgs.cida.pubs.validation.PayloadSeverityLevel.INFORMATIONAL;
import gov.usgs.cida.pubs.validation.constraint.ManagerChecks;
import gov.usgs.cida.pubs.validation.constraint.Orcid;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;
import gov.usgs.cida.pubs.validation.constraint.SippChecks;

@Component
@ParentExists
public class PersonContributor<D> extends Contributor<PersonContributor<D>> implements ILookup {

	public static final String ORCID_VALIDATION_REGEX = "^" + DataNormalizationUtils.ORCID_REGEX + "$"; // only storing short form in db
	public static final String ORCID_VALIDATION_MESS = "The value of orcid=${validatedValue} must include " +
			"16 digits [0-9] separated into groups of 4 by hyphens, final character optionally the letter X";

	private static IPersonContributorDao personContributorDao;

	@JsonProperty("family")
	@JsonView({View.PW.class, View.Lookup.class})
	@NotNull
	@Length(min=1, max=40)
	private String family;

	@JsonProperty("given")
	@JsonView({View.PW.class, View.Lookup.class})
	@Length(min=0, max=40)
	private String given;

	@JsonProperty("suffix")
	@JsonView({View.PW.class, View.Lookup.class})
	@Length(min=0, max=40)
	private String suffix;

	@JsonProperty("email")
	@JsonView({View.PW.class, View.Lookup.class})
	@Length(min=0, max=400)
	@Email
	private String email;

	@Pattern(regexp=ORCID_VALIDATION_REGEX, message=ORCID_VALIDATION_MESS, payload=INFORMATIONAL.class, groups=SippChecks.class)
	@Pattern(regexp=ORCID_VALIDATION_REGEX, message=ORCID_VALIDATION_MESS, payload=FATAL.class, groups=ManagerChecks.class)
	@Orcid(payload=INFORMATIONAL.class, groups=SippChecks.class)
	@Orcid(payload=FATAL.class, groups=ManagerChecks.class)
	private String orcid;

	@JsonProperty("affiliations")
	@JsonView({View.PW.class, View.Lookup.class})
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
	@JsonView({View.PW.class, View.Lookup.class})
	public String getDenormalizeOrcid() {
		return DataNormalizationUtils.denormalizeOrcid(orcid);
	}

	public void setOrcid(String orcid) {
		this.orcid = DataNormalizationUtils.normalizeOrcid(orcid);
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

	@Override
	public String toString() {
		return "PersonContributor [family=" + family + ", given=" + given + ", suffix=" + suffix + ", email=" + email
				+ ", orcid=" + orcid + ", affiliations=" + affiliations + ", preferred=" + preferred + ", corporation="
				+ corporation + ", usgs=" + usgs + ", id=" + id + ", validationErrors=" + validationErrors + "]";
	}

}