package gov.usgs.cida.pubs.domain.mp;

import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.validation.ValidationResults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author drsteini
 *
 */
@Component
@JsonPropertyOrder({"id", "indexId", "displayToPublicDate", "lastModifiedDate", "publicationType", "publicationSubtype",
	"largerWorkType", "largerWorkTitle", "largerWorkSubtype", "seriesTitle", "costCenters", "subseriesTitle", "seriesNumber", "chapter",
	"subchapterNumber","displayTitle", "title", "docAbstract", "usgsCitation", "collaboration", "language", "publisher", "publisherLocation",
	"publicationYear", "conferenceTitle", "conferenceDate", "conferenceLocation", "doi", "issn", "isbn", "ipdsId",
	"productDescription", "startPage", "endPage", "numberOfPages", "onlineOnly", "additionalOnlineFiles", "temporalStart", 
	"temporalEnd", "notes", "ipdsReviewProcessState", "ipdsInternalId", "lockUsername","contributors", "links", 
	"scale", "projection", "datum", "country", "state", "county","city", "otherGeospatial", "geographicExtents", "volume", "issue",
	"edition", "comments", "contact", "tableOfContents", "publishedDateStatement", "publishingServiceCenter", "contact",
	"isPartOf", "supersededBy", "sourceDatabase", "published", "validationErrors"})
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
	@JsonView(View.PW.class)
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
	@Autowired
	public void setMpPublicationDao(final IMpPublicationDao inMpPublicationDao) {
		mpPublicationDao = inMpPublicationDao;
	}

}
