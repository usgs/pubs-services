package gov.usgs.cida.pubs.domain.sipp;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Task {

	@JsonProperty("IPNumber")
	private String ipNumber;
	@JsonProperty("Comments")
	private String comments;
	@JsonProperty("TaskStartDate")
	private String taskStartDate;
	@JsonProperty("TaskCompletionDate")
	private String taskCompletionDate;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("task")
	private String taskName;
	@JsonProperty("TaskAssignedTo")
	private String taskAssignedTo;
	@JsonProperty("NextTask")
	private String nextTask;
	@JsonProperty("TaskApprover")
	private String taskApprover;
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
	public String getComments() {
		return StringUtils.trimToNull(comments);
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getTaskStartDate() {
		return StringUtils.trimToNull(taskStartDate);
	}
	public void setTaskStartDate(String taskStartDate) {
		this.taskStartDate = taskStartDate;
	}
	public String getTaskCompletionDate() {
		return StringUtils.trimToNull(taskCompletionDate);
	}
	public void setTaskCompletionDate(String taskCompletionDate) {
		this.taskCompletionDate = taskCompletionDate;
	}
	public String getStatus() {
		return StringUtils.trimToNull(status);
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTaskName() {
		return StringUtils.trimToNull(taskName);
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskAssignedTo() {
		return StringUtils.trimToNull(taskAssignedTo);
	}
	public void setTaskAssignedTo(String taskAssignedTo) {
		this.taskAssignedTo = taskAssignedTo;
	}
	public String getNextTask() {
		return StringUtils.trimToNull(nextTask);
	}
	public void setNextTask(String nextTask) {
		this.nextTask = nextTask;
	}
	public String getTaskApprover() {
		return StringUtils.trimToNull(taskApprover);
	}
	public void setTaskApprover(String taskApprover) {
		this.taskApprover = taskApprover;
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
