package gov.usgs.cida.pubs.domain.query;

import java.time.LocalDateTime;

public class DeletedPublicationFilter extends PagingFilter {

	private LocalDateTime deletedSince;

	public LocalDateTime getDeletedSince() {
		return deletedSince;
	}
	public void setDeletedSince(LocalDateTime deletedSince) {
		this.deletedSince = deletedSince;
	}
	@Override
	public String getPageNumber() {
		return pageSize == null ? null : pageNumber;
	}
	@Override
	public String getPageRowStart() {
		return pageRowStart;
	}
}
