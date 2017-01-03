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
	@NotNull
	private String indexId;

	@JsonProperty("displayToPublicDate")
	@JsonView(View.PW.class)
	@NotNull(groups = PublishChecks.class)
	private LocalDateTime displayToPublicDate;

	@JsonProperty("publicationYear")
	@JsonView(View.PW.class)
	private String publicationYear;

	@JsonProperty("noYear")
	@JsonView(View.PW.class)
	private Boolean noYear;

	@JsonProperty("publicationType")
	@JsonView(View.PW.class)
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
	@NotNull
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

	@JsonProperty("noUsgsAuthors")
	@JsonView(View.MP.class)
	private Boolean noUsgsAuthors;

	public String getIndexId() {
		return indexId;
	}

	public void setIndexId(final String inIndexId) {
		indexId = inIndexId;
	}

	public LocalDateTime getDisplayToPublicDate() {
		return displayToPublicDate;
	}

	public void setDisplayToPublicDate(final LocalDateTime inDisplayToPublicDate) {
		displayToPublicDate = inDisplayToPublicDate;
	}

	@NotNull
	public PublicationType getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(final PublicationType inPublicationType) {
		publicationType = inPublicationType;
	}

	public PublicationSubtype getPublicationSubtype() {
		return publicationSubtype;
	}

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

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(final String inSeriesNumber) {
		seriesNumber = inSeriesNumber;
	}

	public String getSubseriesTitle() {
		return subseriesTitle;
	}

	public void setSubseriesTitle(final String inSubseriesTitle) {
		subseriesTitle = inSubseriesTitle;
	}

	public String getChapter() {
		return chapter;
	}

	public void setChapter(final String inChapter) {
		chapter = inChapter;
	}

	public String getSubchapterNumber() {
		return subchapterNumber;
	}

	public void setSubchapterNumber(final String inSubchapterNumber) {
		subchapterNumber = inSubchapterNumber;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String inTitle) {
		title = inTitle;
	}

	public String getDocAbstract() {
		return docAbstract;
	}

	public void setDocAbstract(final String inDocAbstract) {
		docAbstract = inDocAbstract;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(final String inLanguage) {
		language = inLanguage;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(final String inPublisher) {
		publisher = inPublisher;
	}

	public String getPublisherLocation() {
		return publisherLocation;
	}

	public void setPublisherLocation(final String inPublisherLocation) {
		publisherLocation = inPublisherLocation;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(final String inDoi) {
		doi = inDoi;
	}

	public String getIssn() {
		return issn;
	}

	public void setIssn(final String inIssn) {
		issn = inIssn;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(final String inIsbn) {
		isbn = inIsbn;
	}

	public String getCollaboration() {
		return collaboration;
	}

	public void setCollaboration(final String inCollaboration) {
		collaboration = inCollaboration;
	}

	public String getUsgsCitation() {
		return usgsCitation;
	}

	public void setUsgsCitation(final String inUsgsCitation) {
		usgsCitation = inUsgsCitation;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(final String inProductDescription) {
		productDescription = inProductDescription;
	}

	public String getStartPage() {
		return startPage;
	}

	public void setStartPage(final String inStartPage) {
		startPage = inStartPage;
	}

	public String getEndPage() {
		return endPage;
	}

	public void setEndPage(final String inEndPage) {
		endPage = inEndPage;
	}

	public String getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(final String inNumberOfPages) {
		numberOfPages = inNumberOfPages;
	}

	public String getOnlineOnly() {
		return onlineOnly;
	}

	public void setOnlineOnly(final String inOnlineOnly) {
		onlineOnly = inOnlineOnly;
	}

	public String getAdditionalOnlineFiles() {
		return additionalOnlineFiles;
	}

	public void setAdditionalOnlineFiles(final String inAdditionalOnlineFiles) {
		additionalOnlineFiles = inAdditionalOnlineFiles;
	}

	public LocalDate getTemporalStart() {
		return temporalStart;
	}

	public void setTemporalStart(final LocalDate inTemporalStart) {
		temporalStart = inTemporalStart;
	}

	public LocalDate getTemporalEnd() {
		return temporalEnd;
	}

	public void setTemporalEnd(final LocalDate inTemporalEnd) {
		temporalEnd = inTemporalEnd;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getIpdsId() {
		return ipdsId;
	}

	public void setIpdsId(final String inIpdsId) {
		ipdsId = inIpdsId;
	}

	public String getIpdsReviewProcessState() {
		return ipdsReviewProcessState;
	}

	public void setIpdsReviewProcessState(final String inIpdsReviewProcessState) {
		this.ipdsReviewProcessState = inIpdsReviewProcessState;
	}

	public void setIpdsInternalId(final String inIpdsInternalId) {
		ipdsInternalId = inIpdsInternalId;
	}

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
		if (null != getContributors() && !getContributors().isEmpty()) {
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

	public Boolean isNoYear() {
		return null == noYear ? false : noYear;
	}

	public void setNoYear(Boolean noYear) {
		this.noYear = noYear;
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
		return indexId + " - " + (isNoYear() ? "No Year" : publicationYear) + " - " + title;
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

	public Boolean isNoUsgsAuthors() {
		return noUsgsAuthors;
	}

	public void setNoUsgsAuthors(final Boolean inNoUsgsAuthors) {
		noUsgsAuthors = inNoUsgsAuthors;
	}

	public static IPublicationDao getPublicationDao() {
		return publicationDao;
	}

	@Autowired
	public void setPublicationDao(final IPublicationDao inPublicationDao) {
		publicationDao = inPublicationDao;
	}

}
