package gov.usgs.cida.pubs.domain.mp;


import gov.usgs.cida.pubs.dao.intfc.IMpListDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;
import gov.usgs.cida.pubs.validation.constraint.NoChildren;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Component
@NoChildren(groups = DeleteChecks.class)
@JsonPropertyOrder({"id", "text", "description", "type"})
public class MpList extends BaseDomain<MpList>  implements Serializable {

	private static final long serialVersionUID = 4584540523823086709L;

	private static IMpListDao mpListDao;

	public static final String IPDS_JOURNAL_ARTICLES = "3";

	public static final String IPDS_OTHER_PUBS = "4";

	public static final String USGS_DATA_RELEASES = "6";

	public static final String PENDING_USGS_SERIES = "9";

	public static final String USGS_WEBSITE = "24";

	public static final String IPDS_USGS_NUMBERED_SERIES = "275";

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
	@Length(min=0, max=500)
	private MpListType type;

	@JsonIgnore
	private Integer ipdsInternalId;

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

	public Integer getIpdsInternalId() {
		return ipdsInternalId;
	}

	public void setIpdsInternalId(final Integer inIpdsInternalId) {
		ipdsInternalId = inIpdsInternalId;
	}

	public static IMpListDao getDao() {
		return mpListDao;
	}

	@Autowired
	public void setMpListDao(final IMpListDao inMpListDao) {
		mpListDao = inMpListDao;
	}

}
