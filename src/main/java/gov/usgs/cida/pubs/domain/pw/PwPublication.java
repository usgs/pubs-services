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
import io.swagger.v3.oas.annotations.media.Schema;

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

	public PwPublication() {
	}

	public PwPublication(Integer id, String indexId) {
		setId(id);
		setIndexId(indexId);
	}

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

	public static IPwPublicationDao getDao() {
		return pwPublicationDao;
	}

	@Autowired
	@Schema(hidden = true)
	public void setPwPublicationDao(final IPwPublicationDao inPwPublicationDao) {
		pwPublicationDao = inPwPublicationDao;
	}

}
