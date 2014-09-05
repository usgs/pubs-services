package gov.usgs.cida.pubs.domain;

import java.io.Serializable;

import gov.usgs.cida.pubs.json.view.intfc.IMpView;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;

public class PublicationContributor<D> extends BaseDomain<D> implements Serializable {

	private static final long serialVersionUID = 5911778679824879199L;

//	@JsonBackReference
	private Integer publicationId;

    private ContributorType contributorType;

    @JsonProperty("rank")
    @JsonView(IMpView.class)
    private Integer rank;

    @JsonView(IMpView.class)
    @JsonUnwrapped
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
