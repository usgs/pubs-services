package gov.usgs.cida.pubs.domain.pw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import gov.usgs.cida.pubs.json.View;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Chorus {

	@JsonProperty("doi")
	@JsonView(View.PW.class)
	private String doi;

	@JsonProperty("url")
	@JsonView(View.PW.class)
	private String url;

	@JsonProperty("publisher")
	@JsonView(View.PW.class)
	private String publisher;

	@JsonProperty("authors")
	@JsonView(View.PW.class)
	private String authors;

	@JsonProperty("journalName")
	@JsonView(View.PW.class)
	private String journalName;

	@JsonProperty("publicationDate")
	@JsonView(View.PW.class)
	private String publicationDate;

	@JsonProperty("auditedOn")
	@JsonView(View.PW.class)
	private String auditedOn;

	@JsonProperty("publiclyAccessibleDate")
	@JsonView(View.PW.class)
	private String pblcllyAccessDate;

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getJournalName() {
		return journalName;
	}

	public void setJournalName(String journalName) {
		this.journalName = journalName;
	}

	public String getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getAuditedOn() {
		return auditedOn;
	}

	public void setAuditedOn(String auditedOn) {
		this.auditedOn = auditedOn;
	}

	public String getPblcllyAccessDate() {
		return pblcllyAccessDate;
	}

	public void setPblcllyAccessDate(String pblcllyAccessDate) {
		this.pblcllyAccessDate = pblcllyAccessDate;
	}

}
