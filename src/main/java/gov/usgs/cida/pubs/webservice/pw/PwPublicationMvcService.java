package gov.usgs.cida.pubs.webservice.pw;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IPwPublicationBusService;
import gov.usgs.cida.pubs.dao.resulthandler.StreamingResultHandler;
import gov.usgs.cida.pubs.domain.Message;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.transform.DelimitedTransformer;
import gov.usgs.cida.pubs.transform.PublicationColumns;
import gov.usgs.cida.pubs.transform.XlsxTransformer;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.webservice.MvcService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.entity.mime.MIME;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonView;

@Controller
@RequestMapping(value="publication", method=RequestMethod.GET)
public class PwPublicationMvcService extends MvcService<PwPublication> {
	
	private static final int MAX_PAGE_SIZE = 5000;
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
    
    @RequestMapping(produces={MediaType.APPLICATION_JSON_VALUE,
    		PubsConstants.MEDIA_TYPE_XLSX_VALUE, PubsConstants.MEDIA_TYPE_CSV_VALUE, PubsConstants.MEDIA_TYPE_TSV_VALUE})
    @JsonView(View.PW.class)
    public @ResponseBody SearchResults getPubs(
    		@RequestParam(value="q", required=false) String searchTerms, //single string search
    		@RequestParam(value="g", required=false) String geospatial,
            @RequestParam(value="title", required=false) String[] title,
            @RequestParam(value="abstract", required=false) String[] pubAbstract,
            @RequestParam(value="contributor", required=false) String[] contributor,
            @RequestParam(value="prodId", required=false) String[] prodId,
            @RequestParam(value="indexId", required=false) String[] indexId,
            @RequestParam(value="ipdsId", required=false) String[] ipdsId,
            @RequestParam(value="year", required=false) String[] year,
            @RequestParam(value="startYear", required=false) String yearStart,
            @RequestParam(value="endYear", required=false) String yearEnd,
    		@RequestParam(value="contributingOffice", required=false) String[] contributingOffice,
            @RequestParam(value="typeName", required=false) String[] typeName,
            @RequestParam(value="subtypeName", required=false) String[] subtypeName,
            @RequestParam(value="seriesName", required=false) String[] reportSeries,
            @RequestParam(value="reportNumber", required=false) String[] reportNumber,
            @RequestParam(value="page_row_start", required=false) String pageRowStart,
            @RequestParam(value="page_number", required=false) String pageNumber,
            @RequestParam(value="page_size", required=false) String pageSize,
            @RequestParam(value="pub_x_days", required=false) String pubXDays,
            @RequestParam(value="pub_date_low", required=false) String pubDateLow,
            @RequestParam(value="pub_date_high", required=false) String pubDateHigh,
            @RequestParam(value="mod_x_days", required=false) String modXDays,
            @RequestParam(value="mod_date_low", required=false) String modDateLow,
            @RequestParam(value="mod_date_high", required=false) String modDateHigh,
            @RequestParam(value="orderBy", required=false) String orderBy,
			HttpServletResponse response, HttpServletRequest request) {

        setHeaders(response);

        Map<String, Object> filters = new HashMap<>();

    	configureSingleSearchFilters(filters, searchTerms);
    	
    	configureGeospatialFilter(filters, geospatial);

    	addToFiltersIfNotNull(filters, "title", title);
    	addToFiltersIfNotNull(filters, "abstract", pubAbstract);
    	filters.putAll(configureContributorFilter("contributor", contributor));
    	addToFiltersIfNotNull(filters, "id", prodId);
    	addToFiltersIfNotNull(filters, "indexId", indexId);
    	addToFiltersIfNotNull(filters, "ipdsId", ipdsId);
    	addToFiltersIfNotNull(filters, "year", year);
    	addToFiltersIfNotNull(filters, "yearStart", yearStart);
    	addToFiltersIfNotNull(filters, "yearEnd", yearEnd);
    	addToFiltersIfNotNull(filters, "contributingOffice", contributingOffice);
    	addToFiltersIfNotNull(filters, "typeName", typeName);
    	addToFiltersIfNotNull(filters, "subtypeName", subtypeName);
    	addToFiltersIfNotNull(filters, "reportSeries", reportSeries);
    	addToFiltersIfNotNull(filters, "reportNumber", reportNumber);
    	addToFiltersIfNotNull(filters, "pubXDays", pubXDays);
    	addToFiltersIfNotNull(filters, "pubDateLow", pubDateLow);
    	addToFiltersIfNotNull(filters, "pubDateHigh", pubDateHigh);
    	addToFiltersIfNotNull(filters, "modXDays", modXDays);
    	addToFiltersIfNotNull(filters, "modDateLow", modDateLow);
    	addToFiltersIfNotNull(filters, "modDateHigh", modDateHigh);
    	addToFiltersIfNotNull(filters, "orderBy", orderBy);

    	filters.put("url", warehouseEndpoint + "/publication/");

        SearchResults results = null;
        String mimeType = request.getParameter(PubsConstants.CONTENT_PARAMETER_NAME);
    	if (null == mimeType || PubsConstants.MEDIA_TYPE_JSON_EXTENSION.equalsIgnoreCase(mimeType)) {
   			filters.putAll(buildPaging(pageRowStart, pageSize, pageNumber));
   			results = getResults(filters, response);
    	} else {
    		streamResults(filters, mimeType, response);
    	}
        return results;
    }
    	
    protected SearchResults getResults(Map<String, Object> filters, HttpServletResponse response) {
    	SearchResults results = new SearchResults();
    	String pageSize = filters.get("pageSize").toString();
    	Integer pageSizeInt = PubsUtilities.parseInteger(pageSize);
    	if (null != pageSizeInt && pageSizeInt > MAX_PAGE_SIZE) {
			response.setStatus(HttpStatus.PAYLOAD_TOO_LARGE.value());
			results = new Message("Max pageSize is " + MAX_PAGE_SIZE);
		} else {
			List<PwPublication> pubs = busService.getObjects(filters);
			Integer totalPubsCount = busService.getObjectCount(filters);
			results.setRecords(pubs);
			results.setPageSize(pageSize);
			results.setPageRowStart(filters.get("pageRowStart").toString());
			if (null != filters.get("pageNumber")) {
				results.setPageNumber(filters.get("pageNumber").toString());
			}
			results.setRecordCount(totalPubsCount);
		}
        return results;
    }

    protected void streamResults(Map<String, Object> filters, String mimeType, HttpServletResponse response) {
        response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
		response.setHeader(MIME.CONTENT_DISPOSITION, "attachment; filename=publications." + mimeType);
   	
    	try {
			ITransformer transformer;
			switch (mimeType) {
			case PubsConstants.MEDIA_TYPE_TSV_EXTENSION:
				transformer = new DelimitedTransformer(response.getOutputStream(), PublicationColumns.getMappings(), "\t");
		    	response.setContentType(PubsConstants.MEDIA_TYPE_TSV_VALUE);
				break;
			case PubsConstants.MEDIA_TYPE_XLSX_EXTENSION:
				transformer = new XlsxTransformer(response.getOutputStream(), PublicationColumns.getMappings());
		    	response.setContentType(PubsConstants.MEDIA_TYPE_XLSX_VALUE);
				break;
			default:
				//Let csv be the default
				transformer = new DelimitedTransformer(response.getOutputStream(), PublicationColumns.getMappings(), ",");
		    	response.setContentType(PubsConstants.MEDIA_TYPE_CSV_VALUE);
				break;
			}
				
			PwPublication.getDao().stream(filters, new StreamingResultHandler<PwPublication>(transformer));
			
			if (transformer instanceof XlsxTransformer) {
				((XlsxTransformer) transformer).finishWorkbook();
			} else {
				((OutputStream) transformer).flush();
			}
		
    	} catch(IOException e) {
    		throw new RuntimeException(e);
    	}
    	
    	response.setStatus(HttpStatus.OK.value());
    }
    
	@RequestMapping(value="{indexId}", produces=MediaType.APPLICATION_JSON_VALUE)
	@JsonView(View.PW.class)
    public @ResponseBody PwPublication getPwPublication(HttpServletRequest request, HttpServletResponse response,
                @PathVariable("indexId") String indexId) {
        setHeaders(response);
        PwPublication rtn = busService.getByIndexId(indexId);
        if (null == rtn) {
        	response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        return rtn;
    }

}
