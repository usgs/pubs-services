package gov.usgs.cida.pubs.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.domain.intfc.ILookup;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.validation.constraint.CrossProperty;
import gov.usgs.cida.pubs.validation.constraint.ParentExists;
import gov.usgs.cida.pubs.validation.constraint.PublishChecks;
import gov.usgs.cida.pubs.validation.constraint.UniqueKey;

/**
 * @author drsteini
 *
 */
@Component
@UniqueKey(message = "{publication.indexid.duplicate}")
@ParentExists
@CrossProperty
public class Publication<D> extends BaseDomain<D> implements ILookup, Serializable {

    private static final long serialVersionUID = -9013357854464855631L;

    private static IPublicationDao publicationDao;

    @JsonProperty("indexId")
    @JsonView(View.PW.class)
    @Length(min=1, max=100)
    private String indexId;

    @JsonProperty("displayToPublicDate")
    @JsonView(View.PW.class)
    @NotNull(groups = PublishChecks.class)
    private LocalDateTime displayToPublicDate;

    @JsonProperty("publicationYear")
    @JsonView(View.PW.class)
    private String publicationYear;

    @JsonProperty("publicationType")
    @JsonView(View.PW.class)
    @NotNull
    private PublicationType publicationType;

    @JsonProperty("publicationSubtype")
    @JsonView(View.PW.class)
    private PublicationSubtype publicationSubtype;

    @JsonProperty("seriesTitle")
    @JsonView(View.PW.class)
    private PublicationSeries seriesTitle;

    @JsonProperty("seriesNumber")
    @JsonView(View.PW.class)
    @Length(min=0, max=100)
    private String seriesNumber;

    @JsonProperty("subseriesTitle")
    @JsonView(View.PW.class)
    @Length(min=0, max=255)
    private String subseriesTitle;

    @JsonProperty("chapter")
    @JsonView(View.PW.class)
    @Length(min=0, max=255)
    private String chapter;

    @JsonProperty("subchapterNumber")
    @JsonView(View.PW.class)
    @Length(min=0, max=255)
    private String subchapterNumber;

    @JsonProperty("title")
    @JsonView(View.PW.class)
    @Length(min=1, max=2000)
    private String title;

    @JsonProperty("docAbstract")
    @JsonView(View.PW.class)
    private String docAbstract;

    @JsonProperty("largerWorkType")
    @JsonView(View.PW.class)
    private PublicationType largerWorkType;

    @JsonProperty("largerWorkTitle")
    @JsonView(View.PW.class)
    @Length(min=0, max=2000)
    private String largerWorkTitle;
    
    @JsonProperty("largerWorkSubtype")
    @JsonView(View.PW.class)
    private PublicationSubtype largerWorkSubtype;

    @JsonProperty("conferenceTitle")
    @JsonView(View.PW.class)
    @Length(min=0, max=2000)
    private String conferenceTitle;

    @JsonProperty("conferenceDate")
    @JsonView(View.PW.class)
    @Length(min=0, max=255)
    private String conferenceDate;

    @JsonProperty("conferenceLocation")
    @JsonView(View.PW.class)
    @Length(min=0, max=255)
    private String conferenceLocation;

    @JsonProperty("language")
    @JsonView(View.PW.class)
    @Length(min=0, max=70)
    private String language;

    @JsonProperty("publisher")
    @JsonView(View.PW.class)
    @Length(min=0, max=255)
    private String publisher;

    @JsonProperty("publisherLocation")
    @JsonView(View.PW.class)
    @Length(min=0, max=255)
    private String publisherLocation;

    @JsonProperty("doi")
    @JsonView(View.PW.class)
    @Length(min=0, max=2000)
    private String doi;

    @JsonProperty("issn")
    @JsonView(View.PW.class)
    @Length(min=0, max=20)
    private String issn;

    @JsonProperty("isbn")
    @JsonView(View.PW.class)
    @Length(min=0, max=30)
    private String isbn;

    @JsonProperty("collaboration")
    @JsonView(View.PW.class)
    @Length(min=0, max=4000)
    private String collaboration;

    @JsonProperty("usgsCitation")
    @JsonView(View.PW.class)
    @Length(min=0, max=4000)
    private String usgsCitation;

    @JsonProperty("productDescription")
    @JsonView(View.PW.class)
    @Length(min=0, max=4000)
    private String productDescription;

    @JsonProperty("startPage")
    @JsonView(View.PW.class)
    @Length(min=0, max=20)
    private String startPage;

    @JsonProperty("endPage")
    @JsonView(View.PW.class)
    @Length(min=0, max=20)
    private String endPage;

    @JsonProperty("numberOfPages")
    @JsonView(View.PW.class)
    @Pattern(regexp=PubsConstants.SPACES_OR_NUMBER_REGEX)
    private String numberOfPages;

    @JsonProperty("onlineOnly")
    @JsonView(View.PW.class)
    @Pattern(regexp="[YN]")
    private String onlineOnly;

    @JsonProperty("additionalOnlineFiles")
    @JsonView(View.PW.class)
    @Pattern(regexp="[YN]")
    private String additionalOnlineFiles;

    @JsonProperty("temporalStart")
    @JsonView(View.PW.class)
    private LocalDate temporalStart;

    @JsonProperty("temporalEnd")
    @JsonView(View.PW.class)
    private LocalDate temporalEnd;

    @JsonProperty("notes")
    @JsonView(View.MP.class)
    private String notes;

    @JsonProperty("ipdsId")
    @JsonView(View.PW.class)
    @Length(min=0, max=15)
    private String ipdsId;

	@JsonProperty("ipdsReviewProcessState")
    @JsonView(View.MP.class)
	@Length(min = 0, max = 400)
	protected String ipdsReviewProcessState;

	@JsonProperty("ipdsInternalId")
    @JsonView(View.MP.class)
    @Pattern(regexp=PubsConstants.SPACES_OR_NUMBER_REGEX)
	protected String ipdsInternalId;

	@JsonIgnore
    @Valid
	protected Collection<PublicationContributor<?>> contributors;

    @JsonProperty("costCenters")
    @JsonView(View.PW.class)
    @Valid
    private Collection<PublicationCostCenter<?>> costCenters;

    @JsonProperty("links")
    @JsonView(View.PW.class)
    @Valid
    private Collection<PublicationLink<?>> links;
    
    @JsonProperty("scale")
    @JsonView(View.PW.class)
    @Pattern(regexp=PubsConstants.SPACES_OR_NUMBER_REGEX)
    private String scale;
    
    @JsonProperty("projection")
    @JsonView(View.PW.class)
    @Length(min=0, max=500)
    private String projection;
    
    @JsonProperty("datum")
    @JsonView(View.PW.class)
    @Length(min=0, max=500)
    private String datum;
    
    @JsonProperty("country")
    @JsonView(View.PW.class)
    @Length(min=0, max=500)
    private String country;
    
    @JsonProperty("state")
    @JsonView(View.PW.class)
    @Length(min=0, max=500)
    private String state;
    
    @JsonProperty("county")
    @JsonView(View.PW.class)
    @Length(min=0, max=500)
    private String county;
    
    @JsonProperty("city")
    @JsonView(View.PW.class)
    @Length(min=0, max=500)
    private String city;
    
    @JsonProperty("otherGeospatial")
    @JsonView(View.PW.class)
    @Length(min=0, max=500)
    private String otherGeospatial;
    
    @JsonProperty("geographicExtents")
    @JsonView(View.PW.class)
    private String geographicExtents;

    @JsonProperty("volume")
    @JsonView(View.PW.class)
    @Length(min=0, max=50)
    private String volume;

    @JsonProperty("issue")
    @JsonView(View.PW.class)
    @Length(min=0, max=20)
    private String issue;

    @JsonProperty("edition")
    @JsonView(View.PW.class)
    @Length(min=0, max=4000)
    private String edition;

    @JsonProperty("publicComments")
    @JsonView(View.PW.class)
    @Length(min=0, max=4000)
    private String comments;

    @JsonProperty("contact")
    @JsonView(View.PW.class)
    @Length(min=0, max=4000)
    private String contact;

    @JsonProperty("tableOfContents")
    @JsonView(View.PW.class)
    private String tableOfContents;

    @JsonProperty("publishingServiceCenter")
    @JsonView(View.PW.class)
    @Valid
    private PublishingServiceCenter publishingServiceCenter;

    @JsonProperty("publishedDate")
    @JsonView(View.PW.class)
    private LocalDate publishedDate;
    
    @JsonProperty("revisedDate")
    @JsonView(View.PW.class)
    private LocalDate revisedDate;
    
    @JsonIgnore
    private Collection<PublicationInteraction> interactions;
    
    @JsonView(View.PW.class)
    private Publication<?> isPartOf;
    
    @JsonView(View.PW.class)
    private Publication<?> supersededBy;
    
    @JsonProperty("sourceDatabase")
    @JsonView(View.MP.class)
    private String sourceDatabase;
    
    @JsonProperty("published")
    @JsonView(View.MP.class)
    private Boolean published;

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

    public Collection<PublicationContributor<?>> getContributors() {
        return contributors;
    }

    public void setContributors(final Collection<PublicationContributor<?>> inContributors) {
    	contributors = inContributors;
    }

    @JsonProperty("contributors")
    @JsonView(View.PW.class)
    public Map<String, Collection<PublicationContributor<?>>> getContributorsToMap() {
    	if (null != contributors && !contributors.isEmpty()) {
    		Map<String, Collection<PublicationContributor<?>>> rtn = new HashMap<>();
	    	for (Iterator<PublicationContributor<?>> i = contributors.iterator(); i.hasNext();) {
	    		PublicationContributor<?> contributor = i.next();
	    		//So, this is a lot of work to make sure that we don't NPE in the event that the contributor
	    		// hasn't been fully populated (mostly likely due to validation errors on the publication) 
	    		String key = "unknown";
	    		if (null != contributor.getContributorType()) {
		    		if (null != contributor.getContributorType().getText()) {
		    			key = contributor.getContributorType().getText().toLowerCase();
		    		} else if (null != contributor.getContributorType().getId()) {
		    			ContributorType ct = ContributorType.getDao().getById(contributor.getContributorType().getId());
		    			if (null != ct) {
		    				key = ct.getText().toLowerCase();
		    			}
		    		}
	    		}
	    		if (!rtn.containsKey(key)) {
	    			rtn.put(key, new ArrayList<PublicationContributor<?>>());
	    		}
	    		rtn.get(key).add(contributor);
	    	}
	    	return rtn;
    	} else {
    		return null;
    	}
    }

    @JsonProperty("contributors")
    public void setContributorsFromMap(final Map<String, Collection<PublicationContributor<?>>> inContributors) {
    	if (null == contributors) {
    		contributors = new ArrayList<PublicationContributor<?>>();
    	} else {
    		contributors.clear();
    	}
    	if (null != inContributors) {
	    	for (Entry<String, Collection<PublicationContributor<?>>> contributorEntry : inContributors.entrySet()) {
	    		if (null != contributorEntry.getValue()) {
	    			contributors.addAll(contributorEntry.getValue());
	    		}
	    	}
    	}
    }

    public Collection<PublicationLink<?>> getLinks() {
        return links;
    }

    public void setLinks(final Collection<PublicationLink<?>> inLinks) {
        links = inLinks;
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
    @JsonView(View.PW.class)
    @Override
	public LocalDateTime getUpdateDate() {
		return super.getUpdateDate();
	}

    @JsonIgnore
    @Override
    public void setUpdateDate(final LocalDateTime inUpdateDate) {
    	super.setUpdateDate(inUpdateDate);
    }

	public PublishingServiceCenter getPublishingServiceCenter() {
		return publishingServiceCenter;
	}

	public void setPublishingServiceCenter(final PublishingServiceCenter inPublishingServiceCenter) {
		publishingServiceCenter = inPublishingServiceCenter;
	}

	public LocalDate getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(final LocalDate inPublishedDate) {
		publishedDate = inPublishedDate;
	}

	public LocalDate getRevisedDate() {
		return revisedDate;
	}

	public void setRevisedDate(final LocalDate inRevisedDate) {
		revisedDate = inRevisedDate;
	}

    @JsonView(View.PW.class)
	public Collection<PublicationInteraction> getInteractions() {
		return interactions;
	}

	@JsonIgnore
	public void setInteractions(final Collection<PublicationInteraction> inInteractions) {
		interactions = inInteractions;
	}

	public Publication<?> getIsPartOf() {
		return isPartOf;
	}

	public void setIsPartOf(final Publication<?> inIsPartOf) {
		isPartOf = inIsPartOf;
	}

	public Publication<?> getSupersededBy() {
		return supersededBy;
	}

	public void setSupersededBy(final Publication<?> inSupersededBy) {
		supersededBy = inSupersededBy;
	}
	
    @Override
    public String getText() {
        return indexId + " - " + publicationYear + " - " + title;
    }
    
    public String getSourceDatabase() {
		return sourceDatabase;
	}

	public void setSourceDatabase(final String inSourceDatabase) {
		sourceDatabase = inSourceDatabase;
	}

	public Boolean isPublished() {
		return published;
	}

	public void setPublished(final Boolean inPublished) {
		published = inPublished;
	}

	/**
     * @return the publicationDao
     */
    public static IPublicationDao getPublicationDao() {
        return publicationDao;
    }

    /**
     * The setter for publicationDao.
     * 
     * @param inPublicationDao the publicationDao to set
     */
    @Autowired
    public void setPublicationDao(final IPublicationDao inPublicationDao) {
        publicationDao = inPublicationDao;
    }

}
