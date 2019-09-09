package gov.usgs.cida.pubs.webservice.pw;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.mime.MIME;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;
import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.intfc.IPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.dao.resulthandler.StreamingResultHandler;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationFilterParams;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.transform.CrossrefTransformer;
import gov.usgs.cida.pubs.transform.DelimitedTransformer;
import gov.usgs.cida.pubs.transform.JsonTransformer;
import gov.usgs.cida.pubs.transform.PublicationColumnsHelper;
import gov.usgs.cida.pubs.transform.XlsxTransformer;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.webservice.MvcService;

@RestController
@RequestMapping(value="publication")
@ResponseBody
public class PwPublicationMvcService extends MvcService<PwPublication> {

	private final IPwPublicationBusService busService;
	private final ConfigurationService configurationService;
	private final ContentNegotiationStrategy contentStrategy;
	private final Configuration templateConfiguration;
	private final IPublicationBusService pubBusService;
	@Autowired
	public PwPublicationMvcService(
			@Qualifier("pwPublicationBusService")
			IPwPublicationBusService busService,
			ConfigurationService configurationService,
			ContentNegotiationStrategy contentStrategy,
			@Qualifier("freeMarkerConfiguration")
			Configuration templateConfiguration,
			IPublicationBusService publicationBusService
	) {
		this.busService = busService;
		this.configurationService = configurationService;
		this.contentStrategy = contentStrategy;
		this.templateConfiguration = templateConfiguration;
		this.pubBusService = publicationBusService;
	}

	@GetMapping(produces={MediaType.APPLICATION_JSON_VALUE,
			PubsConstantsHelper.MEDIA_TYPE_XLSX_VALUE, PubsConstantsHelper.MEDIA_TYPE_CSV_VALUE, PubsConstantsHelper.MEDIA_TYPE_TSV_VALUE})
	@JsonView(View.PW.class)
	public void getStreamPubs(
			HttpServletResponse response, HttpServletRequest request, PublicationFilterParams filterParams,
			@RequestParam(value=PubsConstantsHelper.CONTENT_PARAMETER_NAME, required=false, defaultValue="json") String mimeType) {

		setHeaders(response);

		//Note that paging is only applied to the json format
		Map<String, Object> filters = buildFilters(filterParams);

		filters.put("url", configurationService.getWarehouseEndpoint() + "/publication/");

		if (PubsConstantsHelper.MEDIA_TYPE_JSON_EXTENSION.equalsIgnoreCase(mimeType)) {
			filters.putAll(buildPaging(filterParams.getPageRowStart(), filterParams.getPageSize(), filterParams.getPageNumber()));
		}
		streamResults(filters, mimeType, response);
	}

	protected void streamResults(Map<String, Object> filters, String mimeType, HttpServletResponse response) {
		response.setCharacterEncoding(PubsConstantsHelper.DEFAULT_ENCODING);
		String statement = PwPublicationDao.NS;

		try {
			ITransformer transformer;
			SearchResults searchResults = null;
			switch (mimeType) {
			case PubsConstantsHelper.MEDIA_TYPE_TSV_EXTENSION:
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstantsHelper.MEDIA_TYPE_TSV_VALUE);
				response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + mimeType);
				transformer = new DelimitedTransformer(response.getOutputStream(), PublicationColumnsHelper.getMappings(), "\t");
				break;
			case PubsConstantsHelper.MEDIA_TYPE_XLSX_EXTENSION:
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstantsHelper.MEDIA_TYPE_XLSX_VALUE);
				response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + mimeType);
				transformer = new XlsxTransformer(response.getOutputStream(), PublicationColumnsHelper.getMappings());
				break;
			case PubsConstantsHelper.MEDIA_TYPE_CSV_EXTENSION:
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstantsHelper.MEDIA_TYPE_CSV_VALUE);
				response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + mimeType);
				transformer = new DelimitedTransformer(response.getOutputStream(), PublicationColumnsHelper.getMappings(), ",");
				break;
			default:
				//Let json be the default
				searchResults = getCountAndPaging(filters);
				statement = statement + PwPublicationDao.GET_BY_MAP;
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				transformer = new JsonTransformer(response.getOutputStream(), searchResults);
				break;
			}
			busService.stream(statement, filters, new StreamingResultHandler<PwPublication>(transformer));

			transformer.end();

		} catch(IOException e) {
			throw new RuntimeException(e);
		}

		response.setStatus(HttpStatus.OK.value());
	}

	protected SearchResults getCountAndPaging(Map<String, Object> filters) {
		SearchResults results = new SearchResults();
		results.setPageSize(filters.get(BaseDao.PAGE_SIZE).toString());
		results.setPageRowStart(filters.get(BaseDao.PAGE_ROW_START).toString());
		Object pageNumber = filters.get(BaseDao.PAGE_NUMBER);
		results.setPageNumber(null == pageNumber ? null : pageNumber.toString());
		results.setRecordCount(busService.getObjectCount(filters));
		return results;
	}

	@GetMapping(
		value="{indexId}",
		produces={
			MediaType.APPLICATION_JSON_VALUE, 
			PubsConstantsHelper.MEDIA_TYPE_CROSSREF_VALUE
		}
	)
	@JsonView(View.PW.class)
	public PwPublication getPwPublication(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("indexId") String indexId
		) throws HttpMediaTypeNotAcceptableException, IOException {
		setHeaders(response);
		List<MediaType> mediaTypes = contentStrategy.resolveMediaTypes(new ServletWebRequest(request));
		if(isCrossRefRequest(mediaTypes)) {
			getPwPublicationCrossRef(indexId, response);
			return null;
		} else {
			return getPwPublicationJSON(indexId, response);
		}
	}

	/**
	 * Get all USGS Numbered and Unnumbered Series with DOIs and Contributors
	 * as Crossref XML.
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@GetMapping(
		value = "/crossref",
		produces = { PubsConstantsHelper.MEDIA_TYPE_CROSSREF_VALUE }
	)
	public void getBulkCrossref(HttpServletRequest request,
		HttpServletResponse response) throws IOException{
		String statement = PwPublicationDao.NS + PwPublicationDao.GET_CROSSREF_PUBLICATIONS;

		Map<String, Object> filters = ImmutableMap.of(PwPublicationDao.SUBTYPE_ID, new int[]{
				PublicationSubtype.USGS_NUMBERED_SERIES,
				PublicationSubtype.USGS_UNNUMBERED_SERIES
			}
		);
		try (
				OutputStream outputStream = response.getOutputStream();
				CrossrefTransformer transformer = new CrossrefTransformer(
						outputStream,
						templateConfiguration,
						configurationService,
						pubBusService,
						ContributorType.AUTHOR_KEY,
						ContributorType.EDITOR_KEY
					);
			) {
			response.setCharacterEncoding(PubsConstantsHelper.DEFAULT_ENCODING);

			response.setContentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF_VALUE);
			response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + PubsConstantsHelper.MEDIA_TYPE_CROSSREF_EXTENSION);
			busService.stream(statement, filters, new StreamingResultHandler<>(transformer));
			transformer.end();
		}
	}

	/**
	 * 
	 * @param mediaTypes the media types as specified by the user in the request headers
	 * @return true if the user has requested crossref xml either through the query string or the headers, false otherwise
	 */
	protected boolean isCrossRefRequest(List<MediaType> mediaTypes){
		boolean isCrossRefRequest = false;
		if (null != mediaTypes && mediaTypes.contains(PubsConstantsHelper.MEDIA_TYPE_CROSSREF)) {
			isCrossRefRequest = true;
		}
		return isCrossRefRequest;
	}

	public PwPublication getPwPublicationJSON(String indexId, HttpServletResponse response){
		PwPublication rtn = busService.getByIndexId(indexId);
		if (null == rtn) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return rtn;
	}

	/**
	 * If the specified publication exists and is a USGS Series, responds with Crossref XML.
	 * Otherwise responds with a 404 Not Found.
	 * 
	 * @param indexId publication index
	 * @param response
	 * @throws IOException 
	 */
	public void getPwPublicationCrossRef(String indexId, HttpServletResponse response) throws IOException {
		PwPublication pub = busService.getByIndexId(indexId);
		if (null == pub || !isUsgsSeries(pub)) {
			response.sendError(HttpStatus.NOT_FOUND.value());
		} else {
			writeCrossrefForPub(response, pub);
		}
	}

	/**
	 * Writes the specified USGS Series Publication to Crossref XML.
	 * This method will error if the specified publication is not a USGS Series.
	 * @param response
	 * @param pub
	 * @throws IOException 
	 */
	protected void writeCrossrefForPub(HttpServletResponse response, PwPublication pub) throws IOException {
		try (
				OutputStream outputStream = response.getOutputStream();
				CrossrefTransformer transformer = new CrossrefTransformer(
						outputStream,
						templateConfiguration,
						configurationService,
						pubBusService,
						ContributorType.AUTHOR_KEY,
						ContributorType.EDITOR_KEY
					);
			) {
			response.setCharacterEncoding(PubsConstantsHelper.DEFAULT_ENCODING);
			response.setContentType(PubsConstantsHelper.MEDIA_TYPE_CROSSREF_VALUE);
			response.setHeader(MIME.CONTENT_DISPOSITION, "inline");
			transformer.write(pub);
			transformer.end();
		}
	}

	protected boolean isUsgsSeries(PwPublication pub){
		PublicationSubtype subtype = pub.getPublicationSubtype();
		return PubsUtils.isUsgsNumberedSeries(subtype) || PubsUtils.isUsgsUnnumberedSeries(subtype);
	}
}
