package gov.usgs.cida.pubs.domain.mp;


import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;
import gov.usgs.cida.pubs.validation.constraint.NoChildren;
import gov.usgs.cida.pubs.validation.mp.MpListProtected;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

@NoChildren(groups = DeleteChecks.class)
@MpListProtected(groups = DeleteChecks.class, protectedIds = MpList.PROTECTED_IDS)
public class MpList extends BaseDomain<MpList>  implements Serializable {

	private static final long serialVersionUID = 4584540523823086709L;

	private static IDao<MpList> mpListDao;
	
	public static final String IPDS_JOURNAL_ARTICLES = "3";

	public static final String IPDS_OTHER_PUBS = "4";
	
	public static final String PENDING_USGS_SERIES = "9";

	public static final String IPDS_USGS_NUMBERED_SERIES = "275";

	public static final String WAF_ISSUES = "283";

	public static final String IPDS_CURR_YR_JOURNAL_ARTICLES = "303";
	
	public static final String PROTECTED_IDS =
			IPDS_JOURNAL_ARTICLES + "," +
			IPDS_OTHER_PUBS + "," +
			IPDS_USGS_NUMBERED_SERIES + "," +
			IPDS_CURR_YR_JOURNAL_ARTICLES + "," +
			WAF_ISSUES;

	@JsonProperty("text")
	private String text;

	@JsonProperty("description")
	private String description;
	
	@JsonProperty("type")
	private String type;
	
	public String getText() {
		return text;
	}

	public void setText(final String inText) {
		text = inText;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String inDescription) {
		description = inDescription;
	}

	public String getType() {
		return type;
	}

	public void setType(final String inType) {
		type = inType;
	}

	public static IDao<MpList> getDao() {
		return mpListDao;
	}

	public void setMpListDao(final IDao<MpList> inMpListDao) {
		mpListDao = inMpListDao;
	}

}
