package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.json.view.intfc.IMpView;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

public class PublicationLink<D> extends BaseDomain<D> {

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

    @JsonIgnore
    private String objectSize;

    @JsonIgnore
    private String sizeUnits;

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

    @JsonProperty("size")
    @JsonView(IMpView.class)
    public String getSize() {
        return objectSize + " " + sizeUnits;
    }

    public void setSize(final String inSize) {
        if (2 == inSize.split(" ").length) {
            objectSize = inSize.split(" ")[0];
            sizeUnits = inSize.split(" ")[0];
        } else {
            objectSize = inSize;
            sizeUnits = null;
        }
    }

    public String getObjectSize() {
        return objectSize;
    }

    public void setObjectSize(final String inObjectSize) {
        objectSize = inObjectSize;
    }

    public String getSizeUnits() {
        return sizeUnits;
    }

    public void setSizeUnits(final String inSizeUnits) {
        sizeUnits = inSizeUnits;
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
