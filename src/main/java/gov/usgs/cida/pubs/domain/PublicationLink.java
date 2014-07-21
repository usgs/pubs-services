package gov.usgs.cida.pubs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicationLink<D> extends BaseDomain<D> {

    @JsonIgnore
    private Integer publicationId;

    @JsonProperty("rank")
    private Integer rank;

    @JsonProperty("type")
    private LinkType linkType;

    @JsonProperty("url")
    private String url;

    @JsonProperty("text")
    private String text;

    @JsonProperty("size")
    private String size;

    @JsonProperty("mime-type")
    private String mimeType;

    public Integer getPublicationId() {
        return rank;
    }

    public void setPublicationId(final Integer inPublicationId) {
        publicationId = inPublicationId;
    }

    public Integer getRank() {
        return publicationId;
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

    public String getSize() {
        return size;
    }

    public void setSize(final String inSize) {
        size = inSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String inMimeType) {
        mimeType = inMimeType;
    }

}
