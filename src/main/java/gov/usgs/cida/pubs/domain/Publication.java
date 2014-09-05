package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.json.PubsJsonLocalDateDeSerializer;
import gov.usgs.cida.pubs.json.PubsJsonLocalDateSerializer;
import gov.usgs.cida.pubs.json.PubsJsonLocalDateTimeDeSerializer;
import gov.usgs.cida.pubs.json.PubsJsonLocalDateTimeSerializer;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

import java.io.Serializable;
import java.util.Collection;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Publication<D> extends BaseDomain<D> implements Serializable {

    private static final long serialVersionUID = -9013357854464855631L;

    private static IDao<Publication<?>> publicationDao;

    @JsonProperty("indexId")
    @JsonView(IMpView.class)
    @Length(min=1, max=100)
    private String indexId;

    @JsonProperty("displayToPublicDate")
    @JsonView(IMpView.class)
    @JsonDeserialize(using=PubsJsonLocalDateTimeDeSerializer.class)
    @JsonSerialize(using=PubsJsonLocalDateTimeSerializer.class)
//TODO    @NotNull
    private LocalDateTime displayToPublicDate;

    @JsonProperty("publicationYear")
    @JsonView(IMpView.class)
    @NotNull
    @Length(min=4, max=4)
    private String publicationYear;

    @JsonProperty("publicationType")
    @JsonView(IMpView.class)
    @NotNull
    private PublicationType publicationType;

    @JsonProperty("publicationSubtype")
    @JsonView(IMpView.class)
    @NotNull
    private PublicationSubtype publicationSubtype;

    @JsonProperty("seriesTitle")
    @JsonView(IMpView.class)
    private PublicationSeries seriesTitle;

    @JsonProperty("seriesNumber")
    @JsonView(IMpView.class)
    @Length(min=0, max=100)
    private String seriesNumber;

    @JsonProperty("subseriesTitle")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String subseriesTitle;

    @JsonProperty("chapter")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String chapter;

    @JsonProperty("subchapterNumber")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String subchapterNumber;

    @JsonProperty("title")
    @JsonView(IMpView.class)
    @Length(min=1, max=2000)
    private String title;

    @JsonProperty("abstract")
    @JsonView(IMpView.class)
    private String docAbstract;

    @JsonProperty("largerWorkType")
    @JsonView(IMpView.class)
//    @NotNull
    private PublicationType largerWorkType;

    @JsonProperty("largerWorkTitle")
    @JsonView(IMpView.class)
    @Length(min=1, max=2000)
    private String largerWorkTitle;

    @JsonProperty("conferenceTitle")
    @JsonView(IMpView.class)
    @Length(min=1, max=2000)
    private String conferenceTitle;

    @JsonProperty("conferenceDate")
    @JsonView(IMpView.class)
    @Length(min=1, max=255)
    private String conferenceDate;

    @JsonProperty("conferenceLocation")
    @JsonView(IMpView.class)
    @Length(min=1, max=255)
    private String conferenceLocation;

    @JsonProperty("language")
    @JsonView(IMpView.class)
    @Length(min=0, max=70)
    private String language;

    @JsonProperty("publisher")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String publisher;

    @JsonProperty("publisherLocation")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String publisherLocation;

    @JsonProperty("doi")
    @JsonView(IMpView.class)
    @Length(min=0, max=2000)
    private String doi;

    @JsonProperty("issn")
    @JsonView(IMpView.class)
    @Length(min=0, max=20)
    private String issn;

    @JsonProperty("isbn")
    @JsonView(IMpView.class)
    @Length(min=0, max=30)
    private String isbn;

    @JsonProperty("collaboration")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String collaboration;

    @JsonProperty("usgsCitation")
    @JsonView(IMpView.class)
    @Length(min=0, max=2000)
    private String usgsCitation;

    @JsonProperty("contact")
    @JsonView(IMpView.class)
    private Contact contact;

    @JsonProperty("productDescription")
    @JsonView(IMpView.class)
    @Length(min=0, max=4000)
    private String productDescription;

    @JsonProperty("startPage")
    @JsonView(IMpView.class)
    @Length(min=0, max=20)
    private String startPage;

    @JsonProperty("endPage")
    @JsonView(IMpView.class)
    @Length(min=0, max=20)
    private String endPage;

    @JsonProperty("numberOfPages")
    @JsonView(IMpView.class)
    @Digits(integer=4, fraction=0)
    private String numberOfPages;

    @JsonProperty("onlineOnly")
    @JsonView(IMpView.class)
    @Pattern(regexp="[YN]")
    private String onlineOnly;

    @JsonProperty("additionalOnlineFiles")
    @JsonView(IMpView.class)
    @Pattern(regexp="[YN]")
    private String additionalOnlineFiles;

    @JsonProperty("temporalStart")
    @JsonView(IMpView.class)
    @JsonSerialize(using=PubsJsonLocalDateSerializer.class)
    @JsonDeserialize(using=PubsJsonLocalDateDeSerializer.class)
    private LocalDate temporalStart;

    @JsonProperty("temporalEnd")
    @JsonView(IMpView.class)
    @JsonSerialize(using=PubsJsonLocalDateSerializer.class)
    @JsonDeserialize(using=PubsJsonLocalDateDeSerializer.class)
    private LocalDate temporalEnd;

    @JsonProperty("notes")
    @JsonView(IMpView.class)
    @Length(min=0, max=400000)
    private String notes;

    @JsonProperty("ipdsId")
    @JsonView(IMpView.class)
    @Length(min=0, max=15)
    private String ipdsId;

	@JsonProperty("ipdsReviewProcessState")
	@Length(min = 0, max = 400)
	protected String ipdsReviewProcessState;

	@JsonProperty("ipdsInternalId")
	@Digits(integer = 28, fraction = 0)
	protected String ipdsInternalId;

    @JsonProperty("authors")
    @JsonView(IMpView.class)
//    @JsonManagedReference
	protected Collection<PublicationContributor<?>> authors;

    @JsonProperty("editors")
    @JsonView(IMpView.class)
//    @JsonManagedReference
    private Collection<PublicationContributor<?>> editors;

    @JsonProperty("costCenters")
    @JsonView(IMpView.class)
    @Valid
    private Collection<PublicationCostCenter<?>> costCenters;

    @JsonProperty("links")
    @JsonView(IMpView.class)
//    @JsonManagedReference
    private Collection<PublicationLink<?>> links;

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
     * @return the contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * @param inContact the contact to set
     */
    public void setContact(final Contact inContact) {
        contact = inContact;
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
	
    @JsonProperty("lastModifiedDate")
    @JsonView(IMpView.class)
    @JsonSerialize(using=PubsJsonLocalDateTimeSerializer.class)
    @Override
	public LocalDateTime getUpdateDate() {
		return super.getUpdateDate();
	}

    @JsonIgnore
    @Override
	public void setUpdateDate(LocalDateTime inUpdateDate) {
		super.setUpdateDate(inUpdateDate);
	}
}
