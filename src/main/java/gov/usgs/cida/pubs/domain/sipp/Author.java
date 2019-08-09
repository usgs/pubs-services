package gov.usgs.cida.pubs.domain.sipp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Author {

	@JsonProperty("IPNumber")
	private String ipNumber;
	@JsonProperty("AuthorName")
	private String authorName;
	@JsonProperty("AuthorNameText")
	private String authorNameText;
	@JsonProperty("ORCID")
	private String orcid;
	@JsonProperty("CostCenter")
	private String costCenter;
	@JsonProperty("ContributorRole")
	private String contributorRole;
	@JsonProperty("NonUSGSAffiliation")
	private String nonUSGSAffiliation;
	@JsonProperty("NonUSGSContributor")
	private String nonUSGSContributor;
	@JsonProperty("Rank")
	private String rank;
	@JsonProperty("Created")
	private String created;
	@JsonProperty("CreatedBy")
	private String createdBy;
	@JsonProperty("Modified")
	private String modified;
	@JsonProperty("ModifiedBy")
	private String modifiedBy;
//  </Author>
//  <Author>
//    <IPNumber>IP-108541</IPNumber>
//    <AuthorName>Cravens, Amanda Emily</AuthorName>
//    <AuthorNameText>Cravens, Amanda Emily</AuthorNameText>
//    <ORCID>0000-0002-0271-7967</ORCID>
//    <CostCenter>Fort Collins Science Center</CostCenter>
//    <ContributorRole>1</ContributorRole>
//    <NonUSGSAffiliation xsi:nil="true" />
//    <NonUSGSContributor xsi:nil="true" />
//    <Rank>2</Rank>
//    <Created>2019-05-22T17:04:05</Created>
//    <CreatedBy>Cravens, Amanda Emily</CreatedBy>
//    <Modified>2019-05-22T17:56:01</Modified>
//    <ModifiedBy>Cravens, Amanda Emily</ModifiedBy>
//  </Author>
//</Authors>
	public String getIpNumber() {
		return ipNumber;
	}
	public void setIpNumber(String ipNumber) {
		this.ipNumber = ipNumber;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getAuthorNameText() {
		return authorNameText;
	}
	public void setAuthorNameText(String authorNameText) {
		this.authorNameText = authorNameText;
	}
	public String getOrcid() {
		return orcid;
	}
	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}
	public String getCostCenter() {
		return costCenter;
	}
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}
	public String getContributorRole() {
		return contributorRole;
	}
	public void setContributorRole(String contributorRole) {
		this.contributorRole = contributorRole;
	}
	public String getNonUSGSAffiliation() {
		return nonUSGSAffiliation;
	}
	public void setNonUSGSAffiliation(String nonUSGSAffiliation) {
		this.nonUSGSAffiliation = nonUSGSAffiliation;
	}
	public String getNonUSGSContributor() {
		return nonUSGSContributor;
	}
	public void setNonUSGSContributor(String nonUSGSContributor) {
		this.nonUSGSContributor = nonUSGSContributor;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
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
