package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.validation.ValidationResults;

import javax.validation.constraints.Digits;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author drsteini
 *
 */
//@JsonTypeName ("mpPublication")
@JsonPropertyOrder({"id", "type", "genre", "collection-title", "number", "subseries-title", "chapter-number",
    "sub-chapter-number", "title", "abstract", "language", "publisher", "publisher-place", "DOI", "ISSN", "ISBN", "number-of-pages",
    "page-first", "page-last", "author", "editor", "display-to-public-date", "indexID", "collaboration", 
    "usgs-citation", "cost-center", "links", "product-description", "online-only", "additional-online-files",
    "temporal-start", "temporal-end", "notes", "contact", "ipds-id", "ipds-review-process-state", "ipds-internal-id",
    "validationErrors"})
public class MpPublication extends Publication<MpPublication> {

    private static final long serialVersionUID = 8072814759958143994L;

    private static IMpPublicationDao mpPublicationDao;

    @JsonProperty("ipds-review-process-state")
    @Length(min=0, max=400)
    private String ipdsReviewProcessState;

    @JsonProperty("ipds-internal-id")
    @Digits(integer=28, fraction=0)
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

    @JsonView(IMpView.class)
    @Override
    public ValidationResults getValidationErrors() {
        return validationErrors;
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
