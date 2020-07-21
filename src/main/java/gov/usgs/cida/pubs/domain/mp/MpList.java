package gov.usgs.cida.pubs.domain.mp;


import java.io.Serializable;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import io.swagger.v3.oas.annotations.media.Schema;

@Component
@JsonPropertyOrder({"id", "text", "description", "type"})
public class MpList extends BaseDomain<MpList>  implements Serializable {

	private static final long serialVersionUID = 4584540523823086709L;

	private static IDao<MpList> mpListDao;

	public static final int IPDS_JOURNAL_ARTICLES = 3;

	public static final int IPDS_OTHER_PUBS = 4;

	public static final int USGS_DATA_RELEASES = 6;

	public static final int PENDING_USGS_SERIES = 9;

	public static final int USGS_WEBSITE = 24;

	public static final int IPDS_USGS_NUMBERED_SERIES = 275;

	public enum MpListType {
		PUBS,
		SPN;
	}

	@JsonProperty("text")
	@Length(min=0, max=500)
	private String text;

	@JsonProperty("description")
	@Length(min=0, max=2000)
	private String description;

	@JsonProperty("type")
	private MpListType type;

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

	public MpListType getType() {
		return type;
	}

	public void setType(final MpListType inType) {
		type = inType;
	}

	public static IDao<MpList> getDao() {
		return mpListDao;
	}

	@Autowired
	@Qualifier("mpListDao")
	@Schema(hidden = true)
	public void setMpListDao(final IDao<MpList> inMpListDao) {
		mpListDao = inMpListDao;
	}
}