package gov.usgs.cida.pubs.domain.sipp;

import org.apache.commons.lang3.StringUtils;

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
		return StringUtils.trimToNull(ipNumber);
	}
	public void setIpNumber(String ipNumber) {
		this.ipNumber = ipNumber;
	}
	public String getNoteComment() {
		return StringUtils.trimToNull(noteComment);
	}
	public void setNoteComment(String noteComment) {
		this.noteComment = noteComment;
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
