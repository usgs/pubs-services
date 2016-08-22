package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;

@UniqueKey(message = "{publication.indexid.duplicate}")
@ParentExists
public class PublicationContributor<D> extends BaseDomain<D> implements Serializable {

	private static final long serialVersionUID = 5911778679824879199L;

	private Integer publicationId;

	@JsonProperty("contributorType")
	@JsonView(View.PW.class)
  //TODO Activate this once we can populate from front-end	@NotNull
	private ContributorType contributorType;

	@JsonProperty("rank")
	@JsonView(View.PW.class)
	private Integer rank;

	@JsonView(View.PW.class)
	@JsonUnwrapped
	@NotNull
	private Contributor<?> contributor;

	public Integer getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(final Integer inPublicationId) {
		publicationId = inPublicationId;
	}

	public ContributorType getContributorType() {
		return contributorType;
	}

	public void setContributorType(final ContributorType inContributorType) {
		contributorType = inContributorType;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(final Integer inRank) {
		rank = inRank;
	}

	public Contributor<?> getContributor() {
		return contributor;
	}

	public void setContributor(final Contributor<?> inContributor) {
		contributor = inContributor;
	}

}
