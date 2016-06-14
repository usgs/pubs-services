package gov.usgs.cida.pubs.domain.pw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.json.View;

/**
 * @author drsteini
 *
 */
@Component
public class PwPublication extends Publication<PwPublication> {

	private static final long serialVersionUID = 1176886529474726822L;

	private static IPwPublicationDao pwPublicationDao;

	@JsonProperty("scienceBaseUri")
	@JsonView(View.PW.class)
	private String scienceBaseUri;


	@JsonProperty("chorus")
	@JsonView(View.PW.class)
	private Chorus chorus;

	public String getScienceBaseUri() {
		return scienceBaseUri;
	}

	public void setScienceBaseUri(String scienceBaseUri) {
		this.scienceBaseUri = scienceBaseUri;
	}

	public Chorus getChorus() {
		return chorus;
	}

	public void setChorus(Chorus chorus) {
		this.chorus = chorus;
	}

	/**
	 * @return the pwPublicationDao
	 */
	public static IPwPublicationDao getDao() {
		return pwPublicationDao;
	}

	/**
	 * The setter for pwPublicationDao.
	 * @param inPwPublicationDao the pwPublicationDao to set
	 */
	@Autowired
	public void setPwPublicationDao(final IPwPublicationDao inPwPublicationDao) {
		pwPublicationDao = inPwPublicationDao;
	}

}
