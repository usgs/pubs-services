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

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

    @JsonProperty("indexID")
    @JsonView(IMpView.class)
    @Length(min=1, max=100)
    private String indexId;

    @JsonProperty("display-to-public-date")
    @JsonView(IMpView.class)
    @JsonDeserialize(using=PubsJsonLocalDateTimeDeSerializer.class)
    @JsonSerialize(using=PubsJsonLocalDateTimeSerializer.class)
    @NotNull
    private LocalDateTime displayToPublicDate;

    @JsonProperty("type")
    @JsonView(IMpView.class)
    @NotNull
    private PublicationType publicationType;

    @JsonProperty("genre")
    @JsonView(IMpView.class)
    @NotNull
    private PublicationSubtype publicationSubtype;

    @JsonProperty("collection-title")
    @JsonView(IMpView.class)
    private PublicationSeries publicationSeries;

    @JsonProperty("number")
    @JsonView(IMpView.class)
    @Length(min=0, max=100)
    private String seriesNumber;

    @JsonProperty("subseries-title")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String subseriesTitle;

    @JsonProperty("chapter-number")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String chapter;

    @JsonProperty("sub-chapter-number")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String subchapter;

    @JsonProperty("title")
    @JsonView(IMpView.class)
    @Length(min=1, max=2000)
    private String title;

    @JsonProperty("abstract")
    @JsonView(IMpView.class)
    private String docAbstract;

    @JsonProperty("language")
    @JsonView(IMpView.class)
    @Length(min=0, max=70)
    private String language;

    @JsonProperty("publisher")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String publisher;

    @JsonProperty("publisher-place")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String publisherLocation;

    @JsonProperty("DOI")
    @JsonView(IMpView.class)
    @Length(min=0, max=2000)
    private String doiName;

    @JsonProperty("ISSN")
    @JsonView(IMpView.class)
    @Length(min=0, max=20)
    private String issn;

    @JsonProperty("ISBN")
    @JsonView(IMpView.class)
    @Length(min=0, max=30)
    private String isbn;

    @JsonProperty("collaboration")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String collaboration;

    @JsonProperty("usgs-citation")
    @JsonView(IMpView.class)
    @Length(min=0, max=255)
    private String usgsCitation;

    @JsonProperty("contact")
    @JsonView(IMpView.class)
    private Contact contact;

    @JsonProperty("product-description")
    @JsonView(IMpView.class)
    @Length(min = 0, max = 2000)
    private String productDescription;

    @JsonProperty("page-first")
    @JsonView(IMpView.class)
    @Length(min = 0, max = 20)
    private String startPage;

    @JsonProperty("page-last")
    @JsonView(IMpView.class)
    @Length(min = 0, max = 20)
    private String endPage;

    @JsonProperty("number-of-pages")
    @JsonView(IMpView.class)
    @Digits(integer = 4, fraction = 0)
    private String numberOfPages;

    @JsonProperty("online-only")
    @JsonView(IMpView.class)
    @Length(min = 0, max = 1)
    private String onlineOnly;

    @JsonProperty("additional-online-files")
    @JsonView(IMpView.class)
    @Length(min = 0, max = 1)
    private String additionalOnlineFiles;

    @JsonProperty("temporal-start")
    @JsonView(IMpView.class)
    @JsonSerialize(using = PubsJsonLocalDateSerializer.class)
    @JsonDeserialize(using = PubsJsonLocalDateDeSerializer.class)
    private LocalDate temporalStart;

    @JsonProperty("temporal-end")
    @JsonView(IMpView.class)
    @JsonSerialize(using = PubsJsonLocalDateSerializer.class)
    @JsonDeserialize(using = PubsJsonLocalDateDeSerializer.class)
    private LocalDate temporalEnd;

    @JsonProperty("notes")
    @JsonView(IMpView.class)
    @Length(min = 0, max = 400000)
    private String notes;

    @JsonProperty("ipds-id")
    @JsonView(IMpView.class)
    @Length(min = 0, max = 15)
    private String ipdsId;

    @JsonProperty("author")
    @JsonView(IMpView.class)
    private Collection<PublicationContributor<?>> authors;

    @JsonProperty("editor")
    @JsonView(IMpView.class)
    private Collection<PublicationContributor<?>> editors;

    @JsonProperty("cost-center")
    @JsonView(IMpView.class)
    private Collection<PublicationCostCenter<?>> costCenters;

    @JsonProperty("links")
    @JsonView(IMpView.class)
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

    /**
     * @return the publicationSeries
     */
    public PublicationSeries getPublicationSeries() {
        return publicationSeries;
    }

    /**
     * @param inPublicationSeries the publicationSeries to set
     */
    public void setPublicationSeries(final PublicationSeries inPublicationSeries) {
        publicationSeries = inPublicationSeries;
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

    /**
     * @return the subchapter
     */
    public String getSubchapter() {
        return subchapter;
    }

    /**
     * @param inSubchapter the subchapter to set
     */
    public void setSubchapter(final String inSubchapter) {
        subchapter = inSubchapter;
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

    public String getDoiName() {
        return doiName;
    }

    public void setDoiName(final String inDoiName) {
        doiName = inDoiName;
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

}
