package gov.usgs.cida.pubs.domain;

import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.json.PubsStringDeserializer;

import javax.validation.constraints.Digits;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author drsteini
 *
 */
//@JsonTypeName ("mpPublication")
@JsonPropertyOrder({"id", "type", "genre", "collection-title", "number", "subseries-title", "chapter-number",
    "sub-chapter-number", "title", "abstract", "language", "publisher", "publisher-place", "DOI", "ISSN", "ISBN", "number-of-pages",
    "page-first", "page-last", "author", "editor", "display-to-public-date", "indexID", "collaboration", 
    "usgs-citation", "cost-center", "links", "product-description", "online-only", "additional-online-files",
    "temporal-start", "temporal-end", "notes", "contact", "ipds-id", "ipds-review-process-state", "ipds-internal-id"})
public class MpPublication extends Publication<MpPublication> {

    private static final long serialVersionUID = 8072814759958143994L;

    private static IMpPublicationDao mpPublicationDao;

    @Length(min=0, max=400)
    @JsonProperty("ipds-review-process-state")
    @JsonDeserialize(using=PubsStringDeserializer.class)
    private String ipdsReviewProcessState;

    @Digits(integer=28, fraction=0)
    @JsonProperty("ipds-internal-id")
    @JsonDeserialize(using=PubsStringDeserializer.class)
    private String ipdsInternalId;

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

    /**
     * @return the mpPublicationDao
     */
    public static IMpPublicationDao getDao() {
        return mpPublicationDao;
    }

    /**
     * The setter for mpPublicationDao.
     * @param inMpPublicationDao the MpPublicationDao to set
     */
    public void setMpPublicationDao(final IMpPublicationDao inMpPublicationDao) {
        mpPublicationDao = inMpPublicationDao;
    }

}
