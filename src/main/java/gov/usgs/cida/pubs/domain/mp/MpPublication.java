package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.json.view.intfc.IPwView;
import gov.usgs.cida.pubs.validation.ValidationResults;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author drsteini
 *
 */
@JsonPropertyOrder({"id", "indexId", "displayToPublicDate", "lastModifiedDate", "publicationType", "publicationSubtype",
	"largerWorkType", "largerWorkTitle", "largerWorkSubtype", "seriesTitle", "costCenters", "subseriesTitle", "seriesNumber", "chapter",
	"subchapterNumber", "title", "docAbstract", "usgsCitation", "collaboration", "language", "publisher", "publisherLocation",
	"publicationYear", "conferenceTitle", "conferenceDate", "conferenceLocation", "doi", "issn", "isbn", "ipdsId",
	"productDescription", "startPage", "endPage", "numberOfPages", "onlineOnly", "additionalOnlineFiles", "temporalStart", 
	"temporalEnd", "notes", "ipdsReviewProcessState", "ipdsInternalId", "lockUsername","authors", "editors", "links", 
	"scale", "projection", "datum", "country", "state", "county","city", "otherGeospatial", "geographicExtents", "volume", "issue",
	"edition", "comments", "contact", "tableOfContents",
    "validationErrors"})
public class MpPublication extends Publication<MpPublication> {

    private static final long serialVersionUID = 8072814759958143994L;

    private static IMpPublicationDao mpPublicationDao;
    
    @JsonIgnore
    private String lockUsername;
    
    public String getLockUsername() {
		return lockUsername;
	}

	public void setLockUsername(final String inLockUsername) {
		lockUsername = inLockUsername;
	}

    @JsonProperty("validationErrors")
    @JsonView(IPwView.class)
    @JsonUnwrapped
    @Override
    public ValidationResults getValidationErrors() {
    	return super.getValidationErrors();
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
