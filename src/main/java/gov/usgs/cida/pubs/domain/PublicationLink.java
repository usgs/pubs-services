package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.json.view.intfc.IPwView;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

@ParentExists
public class PublicationLink<D> extends BaseDomain<D> implements Serializable {

	private static final long serialVersionUID = 5845209857599494487L;

	public static final String USGS_THUMBNAIL = "http://pubs.er.usgs.gov/thumbnails/usgs_thumb.jpg";
	public static final String EXTERNAL_THUMBNAIL = "http://pubs.er.usgs.gov/thumbnails/outside_thumb.jpg";

	@JsonIgnore
    private Integer publicationId;

    @JsonProperty("rank")
    @JsonView(IPwView.class)
    private Integer rank;

    @JsonProperty("type")
    @JsonView(IPwView.class)
    @NotNull
    private LinkType linkType;

    @JsonProperty("url")
    @JsonView(IPwView.class)
    @URL
    private String url;

    @JsonProperty("text")
    @JsonView(IPwView.class)
	@Length(min = 0, max = 4000)
    private String text;

    @JsonProperty("size")
    @JsonView(IPwView.class)
	@Length(min = 0, max = 100)
    private String size;

    @JsonProperty("linkFileType")
    @JsonView(IPwView.class)
    private LinkFileType linkFileType;

    @JsonProperty("description")
    @JsonView(IPwView.class)
	@Length(min = 0, max = 4000)
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

    public String getSize() {
        return size;
    }

    public void setSize(final String inSize) {
        size = inSize;
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
