package gov.usgs.cida.pubs.webservice.pw;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.cida.pubs.dao.intfc.IDeletedPublicationDao;
import gov.usgs.cida.pubs.domain.DeletedPublication;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.query.DeletedPublicationFilter;
import gov.usgs.cida.pubs.webservice.MvcService;

@RestController
@RequestMapping(value = "publication/deleted", produces={MediaType.APPLICATION_JSON_VALUE})
public class DeletedPublicationMvcService extends MvcService<DeletedPublication> {

	private final IDeletedPublicationDao deletedPublicationDao;

	@Autowired
	public DeletedPublicationMvcService(final IDeletedPublicationDao deletedPublicationDao) {
		this.deletedPublicationDao = deletedPublicationDao;
	}

	@GetMapping
	@RequestMapping(method=RequestMethod.GET)
	public SearchResults getDeletedPublications(HttpServletResponse response, DeletedPublicationFilter deletedPublicationFilter) {
		setHeaders(response);
		DeletedPublicationFilter withPagingSetFilter = getFilterWithPaging(deletedPublicationFilter);
		SearchResults searchResults = getCountAndPaging(withPagingSetFilter);
		searchResults.setRecords(deletedPublicationDao.getByFilter(withPagingSetFilter));
		return searchResults;
	}

	protected SearchResults getCountAndPaging(DeletedPublicationFilter deletedPublicationFilter) {
		SearchResults results = new SearchResults();
		results.setPageSize(deletedPublicationFilter.getPageSize());
		results.setPageRowStart(deletedPublicationFilter.getPageRowStart());
		results.setPageNumber(deletedPublicationFilter.getPageNumber());
		results.setRecordCount(deletedPublicationDao.getObjectCount(deletedPublicationFilter));
		return results;
	}

	protected DeletedPublicationFilter getFilterWithPaging(DeletedPublicationFilter deletedPublicationFilter) {
		DeletedPublicationFilter withPagingSetFilter = new DeletedPublicationFilter();
		withPagingSetFilter.setDeletedSince(deletedPublicationFilter.getDeletedSince());
		if (isInteger(deletedPublicationFilter.getPageSize()) && Integer.parseInt(deletedPublicationFilter.getPageSize()) > 0) {
			//We will respect a fetchsize if given.
			Integer fetchSize = Integer.parseInt(deletedPublicationFilter.getPageSize());

			if (isInteger(deletedPublicationFilter.getPageNumber()) && Integer.parseInt(deletedPublicationFilter.getPageNumber()) > 0) {
				// But the page number is only respected when provided with a fetchsize
				Integer pageNumber = Integer.parseInt(deletedPublicationFilter.getPageNumber());
				queryParams.put("offset", (pageNumber - 1) * fetchSize);
				withPagingSetFilter.setPageRowStart();
				return withPagingSetFilter;
			}
		}
	}
}
