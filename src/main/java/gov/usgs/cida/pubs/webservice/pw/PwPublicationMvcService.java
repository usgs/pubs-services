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
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.transform.DelimitedTransformer;
import gov.usgs.cida.pubs.transform.JsonTransformer;
import gov.usgs.cida.pubs.transform.PublicationColumns;
import gov.usgs.cida.pubs.transform.XlsxTransformer;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;
import gov.usgs.cida.pubs.webservice.MvcService;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value="publication")
@ResponseBody 
public class PwPublicationMvcService extends MvcService<PwPublication> {

	private final IPwPublicationBusService busService;
	private final String warehouseEndpoint;

	@Autowired
	public PwPublicationMvcService(@Qualifier("pwPublicationBusService")
			final IPwPublicationBusService busService,
			@Qualifier("warehouseEndpoint")
			final String warehouseEndpoint) {
		this.busService = busService;
		this.warehouseEndpoint = warehouseEndpoint;
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
				null, prodId, pubAbstract, pubDateHigh, pubDateLow, pubXDays, q, reportNumber,
				seriesName, startYear, subtypeName, title, typeName, year);

		filters.put("url", warehouseEndpoint + "/publication/");

		if (PubsConstants.MEDIA_TYPE_JSON_EXTENSION.equalsIgnoreCase(mimeType)) {
			filters.putAll(buildPaging(pageRowStart, pageSize, pageNumber));
		}
		streamResults(filters, mimeType, response);
	}

	protected void streamResults(Map<String, Object> filters, String mimeType, HttpServletResponse response) {
		response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
		response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + mimeType);
		String statement = PwPublicationDao.NS;

		try {
			ITransformer transformer;
			switch (mimeType) {
			case PubsConstants.MEDIA_TYPE_TSV_EXTENSION:
				transformer = new DelimitedTransformer(response.getOutputStream(), PublicationColumns.getMappings(), "\t");
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstants.MEDIA_TYPE_TSV_VALUE);
				break;
			case PubsConstants.MEDIA_TYPE_XLSX_EXTENSION:
				transformer = new XlsxTransformer(response.getOutputStream(), PublicationColumns.getMappings());
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstants.MEDIA_TYPE_XLSX_VALUE);
				break;
			case PubsConstants.MEDIA_TYPE_CSV_EXTENSION:
				transformer = new DelimitedTransformer(response.getOutputStream(), PublicationColumns.getMappings(), ",");
				statement = statement + PwPublicationDao.GET_STREAM_BY_MAP;
				response.setContentType(PubsConstants.MEDIA_TYPE_CSV_VALUE);
				break;
			default:
				//Let json be the default
				transformer = new JsonTransformer(response.getOutputStream(), getCountAndPaging(filters));
				statement = statement + PwPublicationDao.GET_BY_MAP;
				response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
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

	@GetMapping(value="{indexId}", produces=MediaType.APPLICATION_JSON_VALUE)
	@JsonView(View.PW.class)
	public PwPublication getPwPublication(HttpServletRequest request, HttpServletResponse response,
				@PathVariable("indexId") String indexId) {
		setHeaders(response);
		PwPublication rtn = busService.getByIndexId(indexId);
		if (null == rtn) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return rtn;
	}

}
