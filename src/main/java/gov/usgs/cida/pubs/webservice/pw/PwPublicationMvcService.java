package gov.usgs.cida.pubs.webservice.pw;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.mime.MIME;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.PublicationDao;
import gov.usgs.cida.pubs.dao.pw.PwPublicationDao;
import gov.usgs.cida.pubs.dao.resulthandler.StreamingResultHandler;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.transform.TransformerFactory;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.webservice.MvcService;
import io.swagger.annotations.ApiParam;
import java.io.OutputStream;
import java.util.List;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.ServletWebRequest;

@RestController
@RequestMapping(value="publication")
@ResponseBody 
public class PwPublicationMvcService extends MvcService<PwPublication> {

	private final IPwPublicationBusService busService;
	private final String warehouseEndpoint;
	private final ContentNegotiationStrategy contentStrategy;
	private final TransformerFactory transformerFactory;
	
	@Autowired
	public PwPublicationMvcService(@Qualifier("pwPublicationBusService")
			final IPwPublicationBusService busService,
			@Qualifier("warehouseEndpoint")
			final String warehouseEndpoint,
			final ContentNegotiationStrategy contentStrategy,
			final TransformerFactory transformerFactory
	) {
		this.busService = busService;
		this.warehouseEndpoint = warehouseEndpoint;
		this.contentStrategy = contentStrategy;
		this.transformerFactory = transformerFactory;
	}

	@GetMapping(produces={MediaType.APPLICATION_JSON_VALUE,
			PubsConstants.MEDIA_TYPE_XLSX_VALUE, PubsConstants.MEDIA_TYPE_CSV_VALUE, PubsConstants.MEDIA_TYPE_TSV_VALUE})
	@JsonView(View.PW.class)
	public void getPubs(
			@RequestParam(value=PublicationDao.Q, required=false) String q,
			@RequestParam(value=PwPublicationDao.G, required=false) String g,
			@RequestParam(value=PublicationDao.TITLE, required=false) String[] title,
			@RequestParam(value=PublicationDao.PUB_ABSTRACT, required=false) String[] pubAbstract,
			@RequestParam(value=PublicationDao.CONTRIBUTOR, required=false) String[] contributor,
			@RequestParam(value=PublicationDao.ORCID, required=false) String[] orcid,
			@RequestParam(value=PublicationDao.DOI, required=false) Boolean doi,
			@RequestParam(value=PublicationDao.PROD_ID, required=false) String[] prodId,
			@RequestParam(value=PublicationDao.INDEX_ID, required=false) String[] indexId,
			@RequestParam(value=PublicationDao.IPDS_ID, required=false) String[] ipdsId,
			@RequestParam(value=PublicationDao.YEAR, required=false) String[] year,
			@RequestParam(value=PublicationDao.START_YEAR, required=false) String startYear,
			@RequestParam(value=PublicationDao.END_YEAR, required=false) String endYear,
			@RequestParam(value=PublicationDao.CONTRIBUTING_OFFICE, required=false) String[] contributingOffice,
			@RequestParam(value=PublicationDao.TYPE_NAME, required=false) String[] typeName,
			@RequestParam(value=PublicationDao.SUBTYPE_NAME, required=false) String[] subtypeName,
			@RequestParam(value=PublicationDao.SERIES_NAME, required=false) String[] seriesName,
			@RequestParam(value=PublicationDao.REPORT_NUMBER, required=false) String[] reportNumber,
			@RequestParam(value=PublicationDao.LINK_TYPE, required=false) String[] linkType,
			@RequestParam(value=BaseDao.PAGE_ROW_START, required=false) String pageRowStart,
			@RequestParam(value=BaseDao.PAGE_NUMBER, required=false) String pageNumber,
			@RequestParam(value=BaseDao.PAGE_SIZE, required=false) String pageSize,
			@RequestParam(value=PwPublicationDao.PUB_X_DAYS, required=false) String pubXDays,
			@RequestParam(value=PwPublicationDao.PUB_DATE_LOW, required=false) String pubDateLow,
			@RequestParam(value=PwPublicationDao.PUB_DATE_HIGH, required=false) String pubDateHigh,
			@RequestParam(value=PwPublicationDao.MOD_X_DAYS, required=false) String modXDays,
			@RequestParam(value=PwPublicationDao.MOD_DATE_LOW, required=false) String modDateLow,
			@RequestParam(value=PwPublicationDao.MOD_DATE_HIGH, required=false) String modDateHigh,
			@RequestParam(value=PublicationDao.ORDER_BY, required=false) String orderBy,
			@RequestParam(value=PwPublicationDao.CHORUS, required=false) Boolean chorus,
			@ApiParam(allowableValues=PubsConstants.MEDIA_TYPE_JSON_EXTENSION + "," + PubsConstants.MEDIA_TYPE_CSV_EXTENSION + "," + PubsConstants.MEDIA_TYPE_TSV_EXTENSION + "," + PubsConstants.MEDIA_TYPE_XLSX_EXTENSION)
			@RequestParam(value=PubsConstants.CONTENT_PARAMETER_NAME, required=false, defaultValue="json") String mimeType,
			HttpServletResponse response, HttpServletRequest request) {

		setHeaders(response);

		//Note that paging is only applied to the json format
		Map<String, Object> filters = buildFilters(chorus, contributingOffice, contributor, orcid, doi, endYear, g, null,
				indexId, ipdsId, null, modDateHigh, modDateLow, modXDays, orderBy, null, null,
				null, prodId, pubAbstract, pubDateHigh, pubDateLow, pubXDays, q, linkType, reportNumber,
				seriesName, startYear, subtypeName, title, typeName, year);

		filters.put("url", warehouseEndpoint + "/publication/");

		if (PubsConstants.MEDIA_TYPE_JSON_EXTENSION.equalsIgnoreCase(mimeType)) {
			filters.putAll(buildPaging(pageRowStart, pageSize, pageNumber));
		}
		streamResults(filters, mimeType, response);
	}

	protected void streamResults(Map<String, Object> filters, String mimeType, HttpServletResponse response) {
		response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
		String statement = PwPublicationDao.NS;

		try {
			ITransformer transformer;
			SearchResults searchResults = null;
			switch (mimeType) {
			case PubsConstants.MEDIA_TYPE_TSV_EXTENSION:
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstants.MEDIA_TYPE_TSV_VALUE);
				response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + mimeType);
				break;
			case PubsConstants.MEDIA_TYPE_XLSX_EXTENSION:
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstants.MEDIA_TYPE_XLSX_VALUE);
				response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + mimeType);
				break;
			case PubsConstants.MEDIA_TYPE_CSV_EXTENSION:
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstants.MEDIA_TYPE_CSV_VALUE);
				response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + mimeType);
				break;
			default:
				//Let json be the default
				searchResults = getCountAndPaging(filters);
				statement = statement + PwPublicationDao.GET_BY_MAP;
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
				break;
			}
			transformer = transformerFactory.getTransformer(mimeType, response.getOutputStream(), searchResults);
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
			PubsConstants.MEDIA_TYPE_CROSSREF_VALUE
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
		
		if(isCrossRefRequest(mediaTypes)){
			getPwPublicationCrossRef(indexId, response);
			return null;
		} else {
			return getPwPublicationJSON(indexId, response);
		}
	}
	
	/**
	 * 
	 * @param mediaTypes the media types as specified by the user in the request headers
	 * @return true if the user has requested crossref xml either through the query string or the headers, false otherwise
	 */
	protected boolean isCrossRefRequest(List<MediaType> mediaTypes){
		boolean isCrossRefRequest = false;
		if (null != mediaTypes && mediaTypes.contains(PubsConstants.MEDIA_TYPE_CROSSREF)) {
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
		try (OutputStream outputStream = response.getOutputStream()) {
			response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
			response.setContentType(PubsConstants.MEDIA_TYPE_CROSSREF_VALUE);
			response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + PubsConstants.MEDIA_TYPE_CROSSREF_EXTENSION);
			ITransformer transformer = transformerFactory.getTransformer(PubsConstants.MEDIA_TYPE_CROSSREF_EXTENSION, outputStream, null);
			transformer.write(pub);
			transformer.end();
		}
	}
	
	protected boolean isUsgsSeries(PwPublication pub){
		PublicationSubtype subtype = pub.getPublicationSubtype();
		return PubsUtilities.isUsgsNumberedSeries(subtype) || PubsUtilities.isUsgsUnnumberedSeries(subtype);
	}
}
