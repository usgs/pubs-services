package gov.usgs.cida.pubs.domain;

import java.io.Serializable;

import gov.usgs.cida.pubs.json.view.intfc.IMpView;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

public class PublicationLink<D> extends BaseDomain<D> implements Serializable {

	private static final long serialVersionUID = 5845209857599494487L;

	@JsonIgnore
    private Integer publicationId;

    @JsonProperty("rank")
    @JsonView(IMpView.class)
    private Integer rank;

    @JsonProperty("type")
    @JsonView(IMpView.class)
    private LinkType linkType;

    @JsonProperty("url")
    @JsonView(IMpView.class)
    private String url;

    @JsonProperty("text")
    @JsonView(IMpView.class)
    private String text;

    @JsonProperty("size")
    @JsonView(IMpView.class)
    private String objectSize;

    @JsonProperty("linkFileType")
    @JsonView(IMpView.class)
    private LinkFileType linkFileType;

    @JsonProperty("description")
    @JsonView(IMpView.class)
    private String description;

    public Integer getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(final Integer inPublicationId) {
        publicationId = inPublicationId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(final Integer inRank) {
        rank = inRank;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(final LinkType inLinkType) {
        linkType = inLinkType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String inUrl) {
        url = inUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(final String inText) {
        text = inText;
    }

    public String getObjectSize() {
        return objectSize;
    }

    public void setObjectSize(final String inObjectSize) {
        objectSize = inObjectSize;
    }

    public LinkFileType getLinkFileType() {
        return linkFileType;
    }

    public void setLinkFileType(final LinkFileType inLinkFileType) {
        linkFileType = inLinkFileType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String inDescription) {
        description = inDescription;
    }

}
