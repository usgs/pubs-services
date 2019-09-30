package gov.usgs.cida.pubs.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import gov.usgs.cida.pubs.dao.intfc.IDeletedPublicationDao;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Component
public class DeletedPublication extends BaseDomain<DeletedPublication> {
	private static IDeletedPublicationDao deletedPublicationDao;

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("indexId")
	private String indexId;

	@JsonProperty("title")
	private String title;

	@JsonProperty("doi")
	private String doi;

	@JsonProperty("deleteDate")
	private LocalDateTime deleteDate;

	@JsonIgnore
	private String deleteUsername;

	public DeletedPublication() {
	}

	public DeletedPublication(Integer id,
			String indexId,
			String title,
			String doi,
			LocalDateTime deleteDate,
			String deleteUsername) {
		this.id = id;
		this.indexId= indexId;
		this.title = title;
		this.doi = doi;
		this.deleteDate = deleteDate;
		this.deleteUsername = deleteUsername;
	}

	public DeletedPublication(PwPublication pwPublication) {
		id = pwPublication.getId();
		indexId= pwPublication.getIndexId();
		title = pwPublication.getTitle();
		doi = pwPublication.getDoi();
		deleteUsername = PubsUtils.getUsername();
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIndexId() {
		return indexId;
	}
	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public LocalDateTime getDeleteDate() {
		return deleteDate;
	}
	public void setDeleteDate(LocalDateTime deleteDate) {
		this.deleteDate = deleteDate;
	}
	public String getDeleteUsername() {
		return deleteUsername;
	}
	public void setDeleteUsername(String deleteUsername) {
		this.deleteUsername = deleteUsername;
	}

	public static IDeletedPublicationDao getDao() {
		return deletedPublicationDao;
	}
	@Autowired
	public void setPublicationDao(final IDeletedPublicationDao inDeletedPublicationDao) {
		deletedPublicationDao = inDeletedPublicationDao;
	}

	@Override
	public String toString() {
		return "DeletedPublication[id:" + id + ":indexId:" + indexId + ":title:"
				+ title + ":doi:" + doi + ":deleteDate:" + deleteDate + ":deleteUsername:"
				+ deleteUsername + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DeletedPublication dp = (DeletedPublication) o;
		return Objects.equals(id, dp.id)
				&& Objects.equals(indexId, dp.indexId)
				&& Objects.equals(title, dp.title)
				&& Objects.equals(doi, dp.doi)
				&& Objects.equals(deleteDate, dp.deleteDate)
				&& Objects.equals(deleteUsername, dp.deleteUsername);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id,
				indexId,
				title,
				doi,
				deleteDate,
				deleteUsername);
	}
}
