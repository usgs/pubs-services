package gov.usgs.cida.pubs.domain.sipp;

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
//  </Task>
//  <Task>
//    <IPNumber>IP-108541</IPNumber>
//    <Comments>Approver changed</Comments>
//    <TaskStartDate>2019-06-24T18:06:03</TaskStartDate>
//    <TaskCompletionDate>2019-06-25T15:12:31</TaskCompletionDate>
//    <Status>Reassign</Status>
//    <task>Bureau Approval</task>
//    <TaskAssignedTo>Carter, Janet M.</TaskAssignedTo>
//    <NextTask>Bureau Approval</NextTask>
//    <TaskApprover>Carter, Janet M.</TaskApprover>
//    <Created>2019-06-25T15:12:32</Created>
//    <CreatedBy>Carter, Janet M.</CreatedBy>
//    <Modified>2019-06-25T15:12:32</Modified>
//    <ModifiedBy>Carter, Janet M.</ModifiedBy>
//  </Task>
//  <Task>
//    <IPNumber>IP-108541</IPNumber>
//    <Comments xsi:nil="true" />
//    <TaskStartDate>2019-06-24T18:05:58</TaskStartDate>
//    <TaskCompletionDate>2019-06-24T18:06:03</TaskCompletionDate>
//    <Status>Approve</Status>
//    <task>Center Approval</task>
//    <TaskAssignedTo>Powell, Janine</TaskAssignedTo>
//    <NextTask>Bureau Approval</NextTask>
//    <TaskApprover>Schuster, Rudy</TaskApprover>
//    <Created>2019-06-24T18:06:03</Created>
//    <CreatedBy>Schuster, Rudy</CreatedBy>
//    <Modified>2019-06-24T18:06:03</Modified>
//    <ModifiedBy>Schuster, Rudy</ModifiedBy>
//  </Task>
//  <Task>
//    <IPNumber>IP-108541</IPNumber>
//    <Comments xsi:nil="true" />
//    <TaskStartDate>2019-06-22T21:36:14</TaskStartDate>
//    <TaskCompletionDate>2019-06-24T18:05:58</TaskCompletionDate>
//    <Status>Approve</Status>
//    <task>Supervisory Approval</task>
//    <TaskAssignedTo>Schuster, Rudy</TaskAssignedTo>
//    <NextTask>Center Approval</NextTask>
//    <TaskApprover>Schuster, Rudy</TaskApprover>
//    <Created>2019-06-24T18:06:00</Created>
//    <CreatedBy>Schuster, Rudy</CreatedBy>
//    <Modified>2019-06-24T18:06:00</Modified>
//    <ModifiedBy>Schuster, Rudy</ModifiedBy>
//  </Task>
//  <Task>
//    <IPNumber>IP-108541</IPNumber>
//    <Comments xsi:nil="true" />
//    <TaskStartDate>2019-05-24T17:09:37</TaskStartDate>
//    <TaskCompletionDate>2019-06-22T21:36:14</TaskCompletionDate>
//    <Status>Approve</Status>
//    <task>Reconcile Peer Review</task>
//    <TaskAssignedTo>Schuster, Rudy</TaskAssignedTo>
//    <NextTask>Supervisory Approval</NextTask>
//    <TaskApprover>Cravens, Amanda Emily</TaskApprover>
//    <Created>2019-06-22T21:36:15</Created>
//    <CreatedBy>Cravens, Amanda Emily</CreatedBy>
//    <Modified>2019-06-22T21:36:15</Modified>
//    <ModifiedBy>Cravens, Amanda Emily</ModifiedBy>
//  </Task>
//  <Task>
//    <IPNumber>IP-108541</IPNumber>
//    <Comments xsi:nil="true" />
//    <TaskStartDate>2019-05-22T19:14:09</TaskStartDate>
//    <TaskCompletionDate>2019-05-24T17:09:37</TaskCompletionDate>
//    <Status>Approve</Status>
//    <task>Accept for Peer Review</task>
//    <TaskAssignedTo>Cravens, Amanda Emily</TaskAssignedTo>
//    <NextTask>Reconcile Peer Review</NextTask>
//    <TaskApprover>Schuster, Rudy</TaskApprover>
//    <Created>2019-05-24T17:09:37</Created>
//    <CreatedBy>Schuster, Rudy</CreatedBy>
//    <Modified>2019-05-24T17:09:37</Modified>
//    <ModifiedBy>Schuster, Rudy</ModifiedBy>
//  </Task>
//  <Task>
//    <IPNumber>IP-108541</IPNumber>
//    <Comments xsi:nil="true" />
//    <TaskStartDate>2019-05-22T17:04:05</TaskStartDate>
//    <TaskCompletionDate>2019-05-22T17:04:05</TaskCompletionDate>
//    <Status>Create</Status>
//    <task xsi:nil="true" />
//    <TaskAssignedTo>Cravens, Amanda Emily</TaskAssignedTo>
//    <NextTask>Request Peer Review</NextTask>
//    <TaskApprover xsi:nil="true" />
//    <Created>2019-05-22T17:04:05</Created>
//    <CreatedBy>Cravens, Amanda Emily</CreatedBy>
//    <Modified>2019-05-22T17:04:05</Modified>
//    <ModifiedBy>Cravens, Amanda Emily</ModifiedBy>
//  </Task>
//  <Task>
//    <IPNumber>IP-108541</IPNumber>
//    <Comments xsi:nil="true" />
//    <TaskStartDate>2019-05-22T17:04:04</TaskStartDate>
//    <TaskCompletionDate>2019-05-22T19:14:09</TaskCompletionDate>
//    <Status>Approve</Status>
//    <task>Request Peer Review</task>
//    <TaskAssignedTo>Schuster, Rudy</TaskAssignedTo>
//    <NextTask>Accept for Peer Review</NextTask>
//    <TaskApprover>Cravens, Amanda Emily</TaskApprover>
//    <Created>2019-05-22T19:14:09</Created>
//    <CreatedBy>Cravens, Amanda Emily</CreatedBy>
//    <Modified>2019-05-22T19:14:09</Modified>
//    <ModifiedBy>Cravens, Amanda Emily</ModifiedBy>
//  </Task>
	public String getIpNumber() {
		return ipNumber;
	}
	public void setIpNumber(String ipNumber) {
		this.ipNumber = ipNumber;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getTaskStartDate() {
		return taskStartDate;
	}
	public void setTaskStartDate(String taskStartDate) {
		this.taskStartDate = taskStartDate;
	}
	public String getTaskCompletionDate() {
		return taskCompletionDate;
	}
	public void setTaskCompletionDate(String taskCompletionDate) {
		this.taskCompletionDate = taskCompletionDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskAssignedTo() {
		return taskAssignedTo;
	}
	public void setTaskAssignedTo(String taskAssignedTo) {
		this.taskAssignedTo = taskAssignedTo;
	}
	public String getNextTask() {
		return nextTask;
	}
	public void setNextTask(String nextTask) {
		this.nextTask = nextTask;
	}
	public String getTaskApprover() {
		return taskApprover;
	}
	public void setTaskApprover(String taskApprover) {
		this.taskApprover = taskApprover;
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
