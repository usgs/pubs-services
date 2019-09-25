package gov.usgs.cida.pubs.domain.pw;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.validation.constraint.NoRelated;
import gov.usgs.cida.pubs.validation.constraint.PurgeChecks;

@Component
@NoRelated(groups = PurgeChecks.class)
public class PwPublication extends Publication<PwPublication> {

	private static final long serialVersionUID = 1176886529474726822L;

	private static IPwPublicationDao pwPublicationDao;

	@JsonProperty("scienceBaseUri")
	@JsonView(View.PW.class)
	private String scienceBaseUri;

	@JsonProperty("chorus")
	@JsonView(View.PW.class)
	private Chorus chorus;
	
	@JsonProperty("stores")
	@JsonView(View.PW.class)
	private Collection<PwStore> stores;

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

	public Collection<PwStore> getStores() {
		return stores;
	}

	public void setStores(Collection<PwStore> stores) {
		this.stores = stores;
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
