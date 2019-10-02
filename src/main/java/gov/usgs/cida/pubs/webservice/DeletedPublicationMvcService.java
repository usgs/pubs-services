package gov.usgs.cida.pubs.webservice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
	public SearchResults getDeletedPublications(HttpServletResponse response,
			@Valid DeletedPublicationFilter deletedPublicationFilter) {
		setHeaders(response);
		SearchResults searchResults = getCountAndPaging(deletedPublicationFilter);
		searchResults.setRecords(deletedPublicationDao.getByFilter(deletedPublicationFilter));
		return searchResults;
	}

	protected SearchResults getCountAndPaging(DeletedPublicationFilter deletedPublicationFilter) {
		SearchResults results = new SearchResults();
		results.setPageSize(String.valueOf(deletedPublicationFilter.getPageSize()));
		results.setPageRowStart(String.valueOf(deletedPublicationFilter.getPageRowStart()));
		results.setPageNumber(String.valueOf(deletedPublicationFilter.getPageNumber()));
		results.setRecordCount(deletedPublicationDao.getObjectCount(deletedPublicationFilter));
		return results;
	}
}
