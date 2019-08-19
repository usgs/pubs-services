package gov.usgs.cida.pubs.domain.sipp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Note {

	@JsonProperty("IPNumber")
	private String ipNumber;
	@JsonProperty("NoteComment")
	private String noteComment;
	@JsonProperty("Created")
	private String created;
	@JsonProperty("CreatedBy")
	private String createdBy;
	@JsonProperty("Modified")
	private String modified;
	@JsonProperty("ModifiedBy")
	private String modifiedBy;
	public String getIpNumber() {
		return ipNumber;
	}
	public void setIpNumber(String ipNumber) {
		this.ipNumber = ipNumber;
	}
	public String getNoteComment() {
		return noteComment;
	}
	public void setNoteComment(String noteComment) {
		this.noteComment = noteComment;
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
