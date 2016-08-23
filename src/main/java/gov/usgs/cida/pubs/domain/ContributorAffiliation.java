package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.json.View;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;

//@UniqueKey(message = "{contributor.indexid.duplicate}")
//@ParentExists
public class ContributorAffiliation<D> extends BaseDomain<D> implements Serializable {

	private static final long serialVersionUID = 5911778679824879199L;

	private Integer contributorId;

	@JsonView(View.PW.class)
	@JsonUnwrapped
	@NotNull
	private Affiliation<?> affiliation;

	public Integer getContributorId() {
		return contributorId;
	}

	public void setContributorId(final Integer inContributorId) {
		contributorId = inContributorId;
	}

	public Affiliation<?> getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(final Affiliation<?> inAffiliation) {
		affiliation = inAffiliation;
	}
}