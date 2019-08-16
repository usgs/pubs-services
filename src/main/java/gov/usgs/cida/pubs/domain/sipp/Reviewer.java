package gov.usgs.cida.pubs.domain.sipp;

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
//  </Reviewer>
//  <Reviewer>
//    <IPNumber>IP-108541</IPNumber>
//    <ReviewerType>USGS-Selected Peer</ReviewerType>
//    <ReviewerName>Jamie McEvoy</ReviewerName>
//    <ReviewerAffiliation>Montana State University</ReviewerAffiliation>
//    <Created>2019-05-22T22:57:22</Created>
//    <CreatedBy>Cravens, Amanda Emily</CreatedBy>
//    <Modified>2019-05-22T22:57:22</Modified>
//    <ModifiedBy>Cravens, Amanda Emily</ModifiedBy>
//  </Reviewer>
//</Reviewers>
	public String getIpNumber() {
		return ipNumber;
	}
	public void setIpNumber(String ipNumber) {
		this.ipNumber = ipNumber;
	}
	public String getReviewerType() {
		return reviewerType;
	}
	public void setReviewerType(String reviewerType) {
		this.reviewerType = reviewerType;
	}
	public String getReviewerName() {
		return reviewerName;
	}
	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}
	public String getReviewerAffiliation() {
		return reviewerAffiliation;
	}
	public void setReviewerAffiliation(String reviewerAffiliation) {
		this.reviewerAffiliation = reviewerAffiliation;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}
