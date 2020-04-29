package gov.usgs.cida.pubs.webservice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.intfc.IDeletedPublicationDao;
import gov.usgs.cida.pubs.domain.DeletedPublication;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.query.DeletedPublicationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Deleted Publications", description = "Access deleted publications")
@RestController
@RequestMapping(value = "publication/deleted", produces={PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE})
public class DeletedPublicationMvcService extends MvcService<DeletedPublication> {

	private final IDeletedPublicationDao deletedPublicationDao;

	@Autowired
	public DeletedPublicationMvcService(final IDeletedPublicationDao deletedPublicationDao) {
		this.deletedPublicationDao = deletedPublicationDao;
	}

	@GetMapping
	@RequestMapping(method=RequestMethod.GET)
	@Operation(
			description = "Return a list of Deleted Publicationss.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = SearchResults.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "deletedSince",
							description = "Filter to only Publications deleted since this date. For example 2019-01-15.",
							schema = @Schema(type = "string", format = "date")
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "page_number",
							description = "Used in conjunction with page_size to page through the deleted Publications.",
							schema = @Schema(type = "integer")
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "page_size",
							description = "Used in conjunction with page_number to page through the deleted Publications.",
							schema = @Schema(type = "integer")
							)
			}
		)
	public SearchResults getDeletedPublications(HttpServletResponse response,
			@Parameter(hidden=true) @Valid DeletedPublicationFilter deletedPublicationFilter) {
		setHeaders(response);
		SearchResults searchResults = getCountAndPaging(deletedPublicationFilter);
		searchResults.setRecords(deletedPublicationDao.getByFilter(deletedPublicationFilter));
		return searchResults;
	}

	protected SearchResults getCountAndPaging(DeletedPublicationFilter deletedPublicationFilter) {
		SearchResults results = new SearchResults();
		results.setPageSize(deletedPublicationFilter.getPage_size());
		results.setPageRowStart(deletedPublicationFilter.getPage_row_start());
		results.setPageNumber(deletedPublicationFilter.getPage_number());
		results.setRecordCount(deletedPublicationDao.getObjectCount(deletedPublicationFilter));
		return results;
	}
}
