package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.validation.ValidatorResult;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author drsteini
 *
 */
@JsonPropertyOrder({"id", "indexID", "displayToPublicDate", "lastModifiedDate", "publicationType", "publicationSubtype",
	"largerWorkType", "largerWorkTitle", "seriesTitle", "costCenters", "subseriesTitle", "seriesNumber", "chapter",
	"subchapterNumber", "title", "docAbstract", "usgsCitation", "collaboration", "language", "publisher", "publisherLocation",
	"publicationYear", "conferenceTitle", "conferenceDate", "conferenceLocation", "doi", "issn", "isbn", "ipdsId",
	"productDescription", "startPage", "endPage", "numberOfPages", "onlineOnly", "additionalOnlineFiles", "temporalStart", 
	"temporalEnd", "notes", "ipdsReviewProcessState", "ipdsInternalId", "lockUsername","authors", "editors", "links", "contact", 
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

	//TODO the following might be a hack - check on refactoring ValidationErrors so this is not needed.
    @JsonProperty("validationErrors")
    @JsonView(IMpView.class)
    public List<ValidatorResult> getValErrors() {
        if (null != validationErrors) {
            return validationErrors.getValidatorResults();
        } else {
            return new ArrayList<>();
        }
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
