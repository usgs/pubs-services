package gov.usgs.cida.pubs.webservice;

import static gov.usgs.cida.pubs.dao.BaseDao.TEXT_SEARCH;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.dao.LinkFileTypeDao;
import gov.usgs.cida.pubs.dao.LinkTypeDao;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.dao.PublicationTypeDao;
import gov.usgs.cida.pubs.dao.PublishingServiceCenterDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.domain.query.PersonContributorFilterParams;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.openapi.Lookup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Reference Lists", description = "Access reference lists")
@RestController
@RequestMapping(value="lookup", produces=PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE)
public class LookupMvcService extends MvcService<PublicationType> {
	private static final Logger LOG = LoggerFactory.getLogger(LookupMvcService.class);

	@GetMapping("publicationtypes")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Publication Types (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Publication Type Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							)
			}
		)
	public @ResponseBody Collection<PublicationType> getPublicationTypes(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text) {
		LOG.debug("publicationType");
		Collection<PublicationType> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			if (null != text && 0 < text.length) {
				filters.put(PublicationTypeDao.TEXT_SEARCH, text[0]);
			}
			rtn = PublicationType.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("publicationtype/{publicationTypeId}/publicationsubtypes")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Publication Subtypes via a REST like interface.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.PATH,
							name = "publicationTypeId",
							description = "Filter to a specific Publication Type.",
							schema = @Schema(type = "integer")
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Publication Subtype Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							)
			}
		)
	public @ResponseBody Collection<PublicationSubtype> getPublicationSubtypesREST(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text,
			@PathVariable("publicationTypeId") Integer publicationTypeId) {
		LOG.debug("publicationSubtype");
		Collection<PublicationSubtype> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			filters.put("publicationTypeId", publicationTypeId);
			if (null != text && 0 < text.length) {
				filters.put(PublicationSubtypeDao.TEXT_SEARCH, text[0]);
			}
			rtn = PublicationSubtype.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("publicationsubtypes")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Publication Subtypes (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Publication Subtype Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "publicationtypeid",
							description = "Filter to a specific Publication Type.",
							array = @ArraySchema(schema = @Schema(type = "integer"))
							)
			}
		)
	public @ResponseBody Collection<PublicationSubtype> getPublicationSubtypesQuery(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text,
			@RequestParam(value="publicationtypeid", required=false) Integer[] publicationTypeId) {
		LOG.debug("publicationSubtype");
		Collection<PublicationSubtype> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			if (null != publicationTypeId && 0 < publicationTypeId.length) {
				filters.put("publicationTypeId", publicationTypeId[0]);
			}
			if (null != text && 0 < text.length) {
				filters.put(PublicationSubtypeDao.TEXT_SEARCH, text[0]);
			}
			rtn = PublicationSubtype.getDao().getByMap(filters);
			}
		return rtn;
	}

	@GetMapping("publicationtype/{publicationTypeId}/publicationsubtype/{publicationSubtypeId}/publicationseries")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Publication Series Names via a REST like interface.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "publicationTypeId",
							description = "Filter to a specific Publication Type.",
							schema = @Schema(type = "integer")
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "publicationSubtypeId",
							description = "Filter to a specific Publication Subtype.",
							schema = @Schema(type = "integer")
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Publication Series Name.",
							array = @ArraySchema(schema = @Schema(type = "integer"))
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "active",
							description = "Filter to just active (true) or inactive (false) Publication Series.",
							schema = @Schema(type = "boolean")
							)
			}
		)
	public @ResponseBody Collection<PublicationSeries> getPublicationSeriesREST(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("publicationTypeId") Integer publicationTypeId,
			@PathVariable("publicationSubtypeId") Integer publicationSubtypeId,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text,
			@RequestParam(value=ACTIVE_SEARCH, required=false) Boolean active) {
		LOG.debug("publicationSeries");
		Collection<PublicationSeries> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, publicationSubtypeId);
			if (null != text && 0 < text.length) {
				filters.put(PublicationSeriesDao.TEXT_SEARCH, text[0]);
			}
			if (null != active) {
				filters.put(PublicationSeriesDao.ACTIVE_SEARCH, active);
			}
			rtn = PublicationSeries.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("publicationseries")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Publication Series Names (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Publication Series Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "active",
							description = "Filter to just active (true) or inactive (false) Publication Series.",
							schema = @Schema(type = "boolean")
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "publicationsubtypeid",
							description = "Filter to a specific Publication Subtype.",
							array = @ArraySchema(schema = @Schema(type = "integer"))
							)
			}
		)
	public @ResponseBody Collection<PublicationSeries> getPublicationSeriesQuery(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text,
			@RequestParam(value="publicationsubtypeid", required=false) Integer[] publicationSubtypeId,
			@RequestParam(value=ACTIVE_SEARCH, required=false) Boolean active) {
		LOG.debug("publicationSeries");
		Collection<PublicationSeries> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			if (null != publicationSubtypeId && 0 < publicationSubtypeId.length) {
				filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, publicationSubtypeId[0]);
			}
			if (null != text && 0 < text.length) {
				filters.put(PublicationSeriesDao.TEXT_SEARCH, text[0]);
			}
			if (null != active) {
				filters.put(PublicationSeriesDao.ACTIVE_SEARCH, active);
			}
			rtn = PublicationSeries.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("costcenters")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Cost Centers (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Cost Center Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "active",
							description = "Filter to just active (true) or inactive (false) Cost Centers.",
							schema = @Schema(type = "boolean")
							)
			}
		)
	public @ResponseBody Collection<CostCenter> getCostCentersLookup(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text,
			@RequestParam(value=ACTIVE_SEARCH, required=false) Boolean active) {
		LOG.debug("CostCenter");
		Collection<CostCenter> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			if (null != text && 0 < text.length) {
				filters.put(CostCenterDao.TEXT_SEARCH, text[0]);
			}
			if (null != active) {
				filters.put(CostCenterDao.ACTIVE_SEARCH, active);
			}
			rtn = CostCenter.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("outsideaffiliates")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Outside Affiliations (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Outside Affiliation Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "active",
							description = "Filter to just active (true) or inactive (false) Outside Affiliations.",
							schema = @Schema(type = "boolean")
							)
			}
		)
	public @ResponseBody Collection<OutsideAffiliation> getOutsideAffiliates(HttpServletRequest request,
			HttpServletResponse response,
				@RequestParam(value=TEXT_SEARCH, required=false) String[] text,
				@RequestParam(value=ACTIVE_SEARCH, required=false) Boolean active) {
		LOG.debug("OutsideAffiliate");
		Collection<OutsideAffiliation> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			if (null != text && 0 < text.length) {
				filters.put(OutsideAffiliationDao.TEXT_SEARCH, text[0]);
			}
			if (null != active) {
				filters.put(OutsideAffiliationDao.ACTIVE_SEARCH, active);
			}
			rtn = OutsideAffiliation.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("contributortypes")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Contributor Types (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Contributor Type Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							)
			}
		)
	public @ResponseBody Collection<ContributorType> getContributorTypes(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text) {
		LOG.debug("ContributorType");
		Collection<ContributorType> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			if (null != text && 0 < text.length) {
				filters.put(ContributorTypeDao.TEXT_SEARCH, text[0]);
			}
			rtn = ContributorType.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("linktypes")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Link Types (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Link Type Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							)
			}
		)
	public @ResponseBody Collection<LinkType> getLinkTypes(HttpServletRequest request,
				HttpServletResponse response,
				@RequestParam(value=TEXT_SEARCH, required=false) String[] text) {
		LOG.debug("LinkType");
		Collection<LinkType> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			if (null != text && 0 < text.length) {
				filters.put(LinkTypeDao.TEXT_SEARCH, text[0]);
			}
			rtn = LinkType.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("linkfiletypes")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Link File Types (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Link File Type Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							)
			}
		)
	public @ResponseBody Collection<LinkFileType> getLinkFileTypes(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text) {
		LOG.debug("LinkFileType");
		Collection<LinkFileType> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			if (null != text && 0 < text.length) {
				filters.put(LinkFileTypeDao.TEXT_SEARCH, text[0]);
			}
			rtn = LinkFileType.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("people")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of People Contributors.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "id",
							description = "Contributor ID for filtering.",
							array = @ArraySchema(schema = @Schema(type = "integer"))
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "corporation",
							description = "Filter to just Corporations (true).",
							schema = @Schema(type = "boolean")
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "usgs",
							description = "Filter to just USGS (true).",
							schema = @Schema(type = "boolean")
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "familyName",
							description = "A 'starts with' filter on Family Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "givenName",
							description = "A 'starts with' filter on Given Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "email",
							description = "A 'starts with' filter on Email.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "orcid",
							description = "A 'starts with' filter on ORCID.",
							array = @ArraySchema(schema = @Schema(type = "string")),
							example = "0000-0002-1825-0097"
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "preferred",
							description = "Filter to just Preferred (true) or Not Preferred (false) Person records.",
							schema = @Schema(type = "boolean")
							),
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'contains' filter on Name and Email.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							)
			}
		)
	public @ResponseBody Collection<Contributor<?>> getContributorsPeople(HttpServletRequest request,
			HttpServletResponse response,
			@Parameter(hidden=true) PersonContributorFilterParams filterParams) {
		Collection<Contributor<?>> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = buildFilters(filterParams);
			rtn = PersonContributor.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("corporations")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Corporate Contributors (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A text search on the Organization name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							)
			}
		)
	public @ResponseBody Collection<Contributor<?>> getContributorsCorporations(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text) {
		LOG.debug("Contributor - Corporations");
		Collection<Contributor<?>> rtn = new ArrayList<>();
		Map<String, Object> filters = new HashMap<>();
		filters.put(TEXT_SEARCH, configureContributorFilter(text));
		if (validateParametersSetHeaders(request, response)) {
			rtn = CorporateContributor.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("publishingServiceCenters")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Publishing Service Centers (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Publishing Service Center Name.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							)
			}
		)
	public @ResponseBody Collection<PublishingServiceCenter> getPublishingServiceCenters(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text) {
		LOG.debug("publishingServiceCenter");
		Collection<PublishingServiceCenter> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			Map<String, Object> filters = new HashMap<>();
			if (null != text && 0 < text.length) {
				filters.put(PublishingServiceCenterDao.TEXT_SEARCH, text[0]);
			}
			rtn = PublishingServiceCenter.getDao().getByMap(filters);
		}
		return rtn;
	}

	@GetMapping("publications")
	@JsonView(View.Lookup.class)
	@Operation(
			description = "Return a list of Publications (The entire list or only those matching the optional search parameter).",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "JSON representation of the list.",
							content = @Content(schema = @Schema(implementation = Lookup.class)))
			},
			parameters = {
					@Parameter(
							in = ParameterIn.QUERY,
							name = "text",
							description = "A 'starts with' filter on Index ID.",
							array = @ArraySchema(schema = @Schema(type = "string"))
							)
			}
		)
	public @ResponseBody Collection<Publication<?>> getPublications(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value=TEXT_SEARCH, required=false) String[] text) {
		LOG.debug("publication");
		Collection<Publication<?>> rtn = new ArrayList<>();
		if (validateParametersSetHeaders(request, response)) {
			String filter = null;
			if (null != text && 0 < text.length) {
				filter = text[0];
			}
			rtn = Publication.getPublicationDao().filterByIndexId(filter);
		}
		return rtn;
	}

}
