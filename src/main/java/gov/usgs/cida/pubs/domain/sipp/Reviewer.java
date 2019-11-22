package gov.usgs.cida.pubs.domain.sipp;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reviewer {

	@JsonProperty("IPNumber")
	private String ipNumber;
	@JsonProperty("ReviewerType")
	private String reviewerType;
	@JsonProperty("ReviewerName")
	private String reviewerName;
	@JsonProperty("ReviewerAffiliation")
	private String reviewerAffiliation;
	@JsonProperty("Created")
	private String created;
	@JsonProperty("CreatedBy")
	private String createdBy;
	@JsonProperty("Modified")
	private String modified;
	@JsonProperty("ModifiedBy")
	private String modifiedBy;
	public String getIpNumber() {
		return StringUtils.trimToNull(ipNumber);
	}
	public void setIpNumber(String ipNumber) {
		this.ipNumber = ipNumber;
	}
	public String getReviewerType() {
		return StringUtils.trimToNull(reviewerType);
	}
	public void setReviewerType(String reviewerType) {
		this.reviewerType = reviewerType;
	}
	public String getReviewerName() {
		return StringUtils.trimToNull(reviewerName);
	}
	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}
	public String getReviewerAffiliation() {
		return StringUtils.trimToNull(reviewerAffiliation);
	}
	public void setReviewerAffiliation(String reviewerAffiliation) {
		this.reviewerAffiliation = reviewerAffiliation;
	}
	public String getCreated() {
		return StringUtils.trimToNull(created);
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getCreatedBy() {
		return StringUtils.trimToNull(createdBy);
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModified() {
		return StringUtils.trimToNull(modified);
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getModifiedBy() {
		return StringUtils.trimToNull(modifiedBy);
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}
