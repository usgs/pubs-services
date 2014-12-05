package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.json.PubsJsonLocalDateDeSerializer;
import gov.usgs.cida.pubs.json.PubsJsonLocalDateSerializer;
import gov.usgs.cida.pubs.json.PubsJsonLocalDateTimeDeSerializer;
import gov.usgs.cida.pubs.json.PubsJsonLocalDateTimeSerializer;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.json.view.intfc.IPwView;
import gov.usgs.cida.pubs.validation.constraint.CrossProperty;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;
import gov.usgs.cida.pubs.validation.constraint.PublishChecks;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

import java.io.Serializable;
import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author drsteini
 *
 */
@UniqueKey(message = "{publication.indexid.duplicate}")
@ParentExists
@CrossProperty
public class Publication<D> extends BaseDomain<D> implements Serializable {

    private static final long serialVersionUID = -9013357854464855631L;

    private static IDao<Publication<?>> publicationDao;

    @JsonProperty("indexId")
    @JsonView(IPwView.class)
    @Length(min=1, max=100)
    private String indexId;

    @JsonProperty("displayToPublicDate")
    @JsonView(IPwView.class)
    @JsonDeserialize(using=PubsJsonLocalDateTimeDeSerializer.class)
    @JsonSerialize(using=PubsJsonLocalDateTimeSerializer.class)
    @NotNull(groups = PublishChecks.class)
    private LocalDateTime displayToPublicDate;

    @JsonProperty("publicationYear")
    @JsonView(IPwView.class)
    @Pattern(regexp=PubsConstants.FOUR_DIGIT_REGEX, message="Publication Year must be a four digit year.")
    private String publicationYear;

    @JsonProperty("publicationType")
    @JsonView(IPwView.class)
    @NotNull
    private PublicationType publicationType;

    @JsonProperty("publicationSubtype")
    @JsonView(IPwView.class)
    private PublicationSubtype publicationSubtype;

    @JsonProperty("seriesTitle")
    @JsonView(IPwView.class)
    private PublicationSeries seriesTitle;

    @JsonProperty("seriesNumber")
    @JsonView(IPwView.class)
    @Length(min=0, max=100)
    private String seriesNumber;

    @JsonProperty("subseriesTitle")
    @JsonView(IPwView.class)
    @Length(min=0, max=255)
    private String subseriesTitle;

    @JsonProperty("chapter")
    @JsonView(IPwView.class)
    @Length(min=0, max=255)
    private String chapter;

    @JsonProperty("subchapterNumber")
    @JsonView(IPwView.class)
    @Length(min=0, max=255)
    private String subchapterNumber;

    @JsonProperty("title")
    @JsonView(IPwView.class)
    @Length(min=1, max=2000)
    private String title;

    @JsonProperty("docAbstract")
    @JsonView(IPwView.class)
    private String docAbstract;

    @JsonProperty("largerWorkType")
    @JsonView(IPwView.class)
    private PublicationType largerWorkType;

    @JsonProperty("largerWorkTitle")
    @JsonView(IPwView.class)
    @Length(min=0, max=2000)
    private String largerWorkTitle;
    
    @JsonProperty("largerWorkSubtype")
    @JsonView(IPwView.class)
    private PublicationSubtype largerWorkSubtype;

    @JsonProperty("conferenceTitle")
    @JsonView(IPwView.class)
    @Length(min=0, max=2000)
    private String conferenceTitle;

    @JsonProperty("conferenceDate")
    @JsonView(IPwView.class)
    @Length(min=0, max=255)
    private String conferenceDate;

    @JsonProperty("conferenceLocation")
    @JsonView(IPwView.class)
    @Length(min=0, max=255)
    private String conferenceLocation;

    @JsonProperty("language")
    @JsonView(IPwView.class)
    @Length(min=0, max=70)
    private String language;

    @JsonProperty("publisher")
    @JsonView(IPwView.class)
    @Length(min=0, max=255)
    private String publisher;

    @JsonProperty("publisherLocation")
    @JsonView(IPwView.class)
    @Length(min=0, max=255)
    private String publisherLocation;

    @JsonProperty("doi")
    @JsonView(IPwView.class)
    @Length(min=0, max=2000)
    private String doi;

    @JsonProperty("issn")
    @JsonView(IPwView.class)
    @Length(min=0, max=20)
    private String issn;

    @JsonProperty("isbn")
    @JsonView(IPwView.class)
    @Length(min=0, max=30)
    private String isbn;

    @JsonProperty("collaboration")
    @JsonView(IPwView.class)
    @Length(min=0, max=4000)
    private String collaboration;

    @JsonProperty("usgsCitation")
    @JsonView(IPwView.class)
    @Length(min=0, max=4000)
    private String usgsCitation;

    @JsonProperty("productDescription")
    @JsonView(IPwView.class)
    @Length(min=0, max=4000)
    private String productDescription;

    @JsonProperty("startPage")
    @JsonView(IPwView.class)
    @Length(min=0, max=20)
    private String startPage;

    @JsonProperty("endPage")
    @JsonView(IPwView.class)
    @Length(min=0, max=20)
    private String endPage;

    @JsonProperty("numberOfPages")
    @JsonView(IPwView.class)
    @Pattern(regexp=PubsConstants.SPACES_OR_NUMBER_REGEX)
    private String numberOfPages;

    @JsonProperty("onlineOnly")
    @JsonView(IPwView.class)
    @Pattern(regexp="[YN]")
    private String onlineOnly;

    @JsonProperty("additionalOnlineFiles")
    @JsonView(IPwView.class)
    @Pattern(regexp="[YN]")
    private String additionalOnlineFiles;

    @JsonProperty("temporalStart")
    @JsonView(IPwView.class)
    @JsonSerialize(using=PubsJsonLocalDateSerializer.class)
    @JsonDeserialize(using=PubsJsonLocalDateDeSerializer.class)
    private LocalDate temporalStart;

    @JsonProperty("temporalEnd")
    @JsonView(IPwView.class)
    @JsonSerialize(using=PubsJsonLocalDateSerializer.class)
    @JsonDeserialize(using=PubsJsonLocalDateDeSerializer.class)
    private LocalDate temporalEnd;

    @JsonProperty("notes")
    @JsonView(IMpView.class)
    private String notes;

    @JsonProperty("ipdsId")
    @JsonView(IPwView.class)
    @Length(min=0, max=15)
    private String ipdsId;

	@JsonProperty("ipdsReviewProcessState")
	@Length(min = 0, max = 400)
	protected String ipdsReviewProcessState;

	@JsonProperty("ipdsInternalId")
    @Pattern(regexp=PubsConstants.SPACES_OR_NUMBER_REGEX)
	protected String ipdsInternalId;

    @JsonProperty("authors")
    @JsonView(IPwView.class)
    @Valid
	protected Collection<PublicationContributor<?>> authors;

    @JsonProperty("editors")
    @JsonView(IPwView.class)
    @Valid
    private Collection<PublicationContributor<?>> editors;

    @JsonProperty("costCenters")
    @JsonView(IPwView.class)
    @Valid
    private Collection<PublicationCostCenter<?>> costCenters;

    @JsonProperty("links")
    @JsonView(IPwView.class)
    @Valid
    private Collection<PublicationLink<?>> links;
    
    @JsonProperty("scale")
    @JsonView(IPwView.class)
    @Pattern(regexp=PubsConstants.SPACES_OR_NUMBER_REGEX)
    private String scale;
    
    @JsonProperty("projection")
    @JsonView(IPwView.class)
    @Length(min=0, max=500)
    private String projection;
    
    @JsonProperty("datum")
    @JsonView(IPwView.class)
    @Length(min=0, max=500)
    private String datum;
    
    @JsonProperty("country")
    @JsonView(IPwView.class)
    @Length(min=0, max=500)
    private String country;
    
    @JsonProperty("state")
    @JsonView(IPwView.class)
    @Length(min=0, max=500)
    private String state;
    
    @JsonProperty("county")
    @JsonView(IPwView.class)
    @Length(min=0, max=500)
    private String county;
    
    @JsonProperty("city")
    @JsonView(IPwView.class)
    @Length(min=0, max=500)
    private String city;
    
    @JsonProperty("otherGeospatial")
    @JsonView(IPwView.class)
    @Length(min=0, max=500)
    private String otherGeospatial;
    
    @JsonProperty("geographicExtents")
    @JsonView(IPwView.class)
    private String geographicExtents;

    @JsonProperty("volume")
    @JsonView(IPwView.class)
    @Length(min=0, max=50)
    private String volume;

    @JsonProperty("issue")
    @JsonView(IPwView.class)
    @Length(min=0, max=20)
    private String issue;

    @JsonProperty("edition")
    @JsonView(IPwView.class)
    @Length(min=0, max=4000)
    private String edition;

    @JsonProperty("publicComments")
    @JsonView(IPwView.class)
    @Length(min=0, max=4000)
    private String comments;

    @JsonProperty("contact")
    @JsonView(IPwView.class)
    @Length(min=0, max=4000)
    private String contact;

    @JsonProperty("tableOfContents")
    @JsonView(IPwView.class)
    @Length(min=0, max=4000)
    private String tableOfContents;

    /**
     * @return the indexId
     */
    public String getIndexId() {
        return indexId;
    }

    /**
     * @param inIndexId the indexId to set
     */
    public void setIndexId(final String inIndexId) {
        indexId = inIndexId;
    }

    /**
     * @return the displayToPublicDate
     */
    public LocalDateTime getDisplayToPublicDate() {
        return displayToPublicDate;
    }

    /**
     * @param inDisplayToPublicDate the displayToPublicDate to set
     */
    public void setDisplayToPublicDate(final LocalDateTime inDisplayToPublicDate) {
        displayToPublicDate = inDisplayToPublicDate;
    }

    /**
     * @return the publicationType
     */
    public PublicationType getPublicationType() {
        return publicationType;
    }

    /**
     * @param inPublicationType the publicationType to set
     */
    public void setPublicationType(final PublicationType inPublicationType) {
        publicationType = inPublicationType;
    }

    /**
     * @return the publicationSubtype
     */
    public PublicationSubtype getPublicationSubtype() {
        return publicationSubtype;
    }

    /**
     * @param inPublicationSubtype the publicationSubtype to set
     */
    public void setPublicationSubtype(
            final PublicationSubtype inPublicationSubtype) {
        publicationSubtype = inPublicationSubtype;
    }

    public PublicationSeries getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(final PublicationSeries inSeriesTitle) {
        seriesTitle = inSeriesTitle;
    }

    /**
     * @return the seriesNumber
     */
    public String getSeriesNumber() {
        return seriesNumber;
    }

    /**
     * @param inSeriesNumber the seriesNumber to set
     */
    public void setSeriesNumber(final String inSeriesNumber) {
        seriesNumber = inSeriesNumber;
    }

    /**
     * @return the subSeriesTitle
     */
    public String getSubseriesTitle() {
        return subseriesTitle;
    }

    /**
     * @param inSubseriesTitle the subseriesTitle to set
     */
    public void setSubseriesTitle(final String inSubseriesTitle) {
        subseriesTitle = inSubseriesTitle;
    }

    /**
     * @return the chapter
     */
    public String getChapter() {
        return chapter;
    }

    /**
     * @param inChapter the chapter to set
     */
    public void setChapter(final String inChapter) {
        chapter = inChapter;
    }

    public String getSubchapterNumber() {
        return subchapterNumber;
    }

    public void setSubchapterNumber(final String inSubchapterNumber) {
        subchapterNumber = inSubchapterNumber;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param inTitle the title to set
     */
    public void setTitle(final String inTitle) {
        title = inTitle;
    }

    /**
     * @return the docAbstract
     */
    public String getDocAbstract() {
        return docAbstract;
    }

    /**
     * @param inDocAbstract the docAbstract to set
     */
    public void setDocAbstract(final String inDocAbstract) {
        docAbstract = inDocAbstract;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param inLanguage the language to set
     */
    public void setLanguage(final String inLanguage) {
        language = inLanguage;
    }

    /**
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * @param inPublisher the publisher to set
     */
    public void setPublisher(final String inPublisher) {
        publisher = inPublisher;
    }

    /**
     * @return the publisherLocation
     */
    public String getPublisherLocation() {
        return publisherLocation;
    }

    /**
     * @param inPublisherLocation the publisherLocation to set
     */
    public void setPublisherLocation(final String inPublisherLocation) {
        publisherLocation = inPublisherLocation;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(final String inDoi) {
        doi = inDoi;
    }

    /**
     * @return the issn
     */
    public String getIssn() {
        return issn;
    }

    /**
     * @param inIssn the issn to set
     */
    public void setIssn(final String inIssn) {
        issn = inIssn;
    }

    /**
     * @return the isbn
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @param inIsbn the isbn to set
     */
    public void setIsbn(final String inIsbn) {
        isbn = inIsbn;
    }

    /**
     * @return the collaboration
     */
    public String getCollaboration() {
        return collaboration;
    }

    /**
     * @param inCollaboration the collaboration to set
     */
    public void setCollaboration(final String inCollaboration) {
        collaboration = inCollaboration;
    }

    /**
     * @return the usgsCitation
     */
    public String getUsgsCitation() {
        return usgsCitation;
    }

    /**
     * @param inUsgsCitation the usgsCitation to set
     */
    public void setUsgsCitation(final String inUsgsCitation) {
        usgsCitation = inUsgsCitation;
    }

    /**
     * @return the productDescription
     */
    public String getProductDescription() {
        return productDescription;
    }

    /**
     * @param inProductDescription the productDescription to set
     */
    public void setProductDescription(final String inProductDescription) {
        productDescription = inProductDescription;
    }

    /**
     * @return the startPage
     */
    public String getStartPage() {
        return startPage;
    }

    /**
     * @param inStartPage the startPage to set
     */
    public void setStartPage(final String inStartPage) {
        startPage = inStartPage;
    }

    /**
     * @return the endPage
     */
    public String getEndPage() {
        return endPage;
    }

    /**
     * @param inEndPage the endPage to set
     */
    public void setEndPage(final String inEndPage) {
        endPage = inEndPage;
    }

    /**
     * @return the numberOfPages
     */
    public String getNumberOfPages() {
        return numberOfPages;
    }

    /**
     * @param inNumberOfPages the numberOfPages to set
     */
    public void setNumberOfPages(final String inNumberOfPages) {
        numberOfPages = inNumberOfPages;
    }

    /**
     * @return the onlineOnly
     */
    public String getOnlineOnly() {
        return onlineOnly;
    }

    /**
     * @param inOnlineOnly the onlineOnly to set
     */
    public void setOnlineOnly(final String inOnlineOnly) {
        onlineOnly = inOnlineOnly;
    }

    /**
     * @return the additionalOnlineFiles
     */
    public String getAdditionalOnlineFiles() {
        return additionalOnlineFiles;
    }

    /**
     * @param inAdditionalOnlineFiles the additionalOnlineFiles to set
     */
    public void setAdditionalOnlineFiles(final String inAdditionalOnlineFiles) {
        additionalOnlineFiles = inAdditionalOnlineFiles;
    }

    /**
     * @return the temporalStart
     */
    public LocalDate getTemporalStart() {
        return temporalStart;
    }

    /**
     * @param inTemporalStart the temporalStart to set
     */
    public void setTemporalStart(final LocalDate inTemporalStart) {
        temporalStart = inTemporalStart;
    }

    /**
     * @return the temporalEnd
     */
    public LocalDate getTemporalEnd() {
        return temporalEnd;
    }

    /**
     * @param inTemporalEnd the temporalEnd to set
     */
    public void setTemporalEnd(final LocalDate inTemporalEnd) {
        temporalEnd = inTemporalEnd;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return the ipdsId
     */
    public String getIpdsId() {
        return ipdsId;
    }

    /**
     * @param inIpdsId the ipdsId to set
     */
    public void setIpdsId(final String inIpdsId) {
        ipdsId = inIpdsId;
    }

    public String getIpdsReviewProcessState() {
        return ipdsReviewProcessState;
    }

    public void setIpdsReviewProcessState(final String inIpdsReviewProcessState) {
        this.ipdsReviewProcessState = inIpdsReviewProcessState;
    }

    /**
     * @param inIpdsInternalId the ipdsInternalId to set
     */
    public void setIpdsInternalId(final String inIpdsInternalId) {
        ipdsInternalId = inIpdsInternalId;
    }

    /**
     * @return the ipdsInternalId
     */
    public String getIpdsInternalId() {
        return ipdsInternalId;
    }

    public Collection<PublicationCostCenter<?>> getCostCenters() {
        return costCenters;
    }

    public void setCostCenters(final Collection<PublicationCostCenter<?>> inCostCenters) {
        costCenters = inCostCenters;
    }

    public Collection<PublicationContributor<?>> getAuthors() {
        return authors;
    }

    public void setAuthors(final Collection<PublicationContributor<?>> inAuthors) {
        authors = inAuthors;
    }

    public Collection<PublicationContributor<?>> getEditors() {
        return editors;
    }

    public void setEditors(final Collection<PublicationContributor<?>> inEditors) {
        editors = inEditors;
    }

    public Collection<PublicationLink<?>> getLinks() {
        return links;
    }

    public void setLinks(final Collection<PublicationLink<?>> inLinks) {
        links = inLinks;
    }

    /**
     * @return the publicationDao
     */
    public static IDao<Publication<?>> getPublicationDao() {
        return publicationDao;
    }

    /**
     * The setter for publicationDao.
     * 
     * @param inPublicationDao the publicationDao to set
     */
    public void setPublicationDao(final IDao<Publication<?>> inPublicationDao) {
        publicationDao = inPublicationDao;
    }

	public String getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(String publicationYear) {
		this.publicationYear = publicationYear;
	}

	public PublicationType getLargerWorkType() {
		return largerWorkType;
	}

	public void setLargerWorkType(PublicationType largerWorkType) {
		this.largerWorkType = largerWorkType;
	}

	public String getLargerWorkTitle() {
		return largerWorkTitle;
	}
	
	public void setLargerWorkTitle(String largerWorkTitle) {
		this.largerWorkTitle = largerWorkTitle;
	}

	public PublicationSubtype getLargerWorkSubtype() {
		return largerWorkSubtype;
	}

	public void setLargerWorkSubtype(PublicationSubtype largerWorkSubtype) {
		this.largerWorkSubtype = largerWorkSubtype;
	}
	
	public String getConferenceTitle() {
		return conferenceTitle;
	}

	public void setConferenceTitle(String conferenceTitle) {
		this.conferenceTitle = conferenceTitle;
	}

	public String getConferenceDate() {
		return conferenceDate;
	}

	public void setConferenceDate(String conferenceDate) {
		this.conferenceDate = conferenceDate;
	}

	public String getConferenceLocation() {
		return conferenceLocation;
	}

	public void setConferenceLocation(String conferenceLocation) {
		this.conferenceLocation = conferenceLocation;
	}
	
	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}
	
	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}
	
	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public String getOtherGeospatial() {
		return otherGeospatial;
	}

	public void setOtherGeospatial(String otherGeospatial) {
		this.otherGeospatial = otherGeospatial;
	}
	
	public String getGeographicExtents() {
		return geographicExtents;
	}

	public void setGeographicExtents(String geographicExtents) {
		this.geographicExtents = geographicExtents;
	}
	
    public String getVolume() {
		return volume;
	}

	public void setVolume(final String inVolume) {
		volume = inVolume;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(final String inIssue) {
		issue = inIssue;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(final String inEdition) {
		edition = inEdition;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(final String inComments) {
		comments = inComments;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(final String inContact) {
		contact = inContact;
	}

	public String getTableOfContents() {
		return tableOfContents;
	}

	public void setTableOfContents(final String inTableOfContents) {
		tableOfContents = inTableOfContents;
	}

	@JsonProperty("lastModifiedDate")
    @JsonView(IPwView.class)
    @JsonSerialize(using=PubsJsonLocalDateTimeSerializer.class)
    @Override
	public LocalDateTime getUpdateDate() {
		return super.getUpdateDate();
	}

    @JsonIgnore
    @Override
    public void setUpdateDate(final LocalDateTime inUpdateDate) {
    	super.setUpdateDate(inUpdateDate);
    }

}
