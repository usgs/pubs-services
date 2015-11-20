package gov.usgs.cida.pubs.webservice;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;

@Controller
@RequestMapping(value="publicationSeries", produces=MediaType.APPLICATION_JSON_VALUE)
public class PublicationSeriesMvcService extends MvcService<PublicationSeries> {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationSeriesMvcService.class);
	
	private IBusService<PublicationSeries> busService;

	@Autowired
	public PublicationSeriesMvcService(@Qualifier("publicationSeriesBusService")
			IBusService<PublicationSeries> busService) {
		this.busService = busService;
	}
	
    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody SearchResults getPublicationSeries(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value=TEXT_SEARCH, required=false) String[] text,
            @RequestParam(value="publicationsubtypeid", required=false) String[] publicationSubtypeId,
            @RequestParam(value=ACTIVE_SEARCH, required=false) String[] active,
            @RequestParam(value="page_row_start", required=false, defaultValue = "0") String pageRowStart,
            @RequestParam(value="page_number", required=false) String pageNumber,
            @RequestParam(value="page_size", required=false, defaultValue = "25") String pageSize) {
        setHeaders(response);
        LOG.debug("publicationSeries");
        setHeaders(response);
        Map<String, Object> filters = new HashMap<>();
        if (null != publicationSubtypeId && 0 < publicationSubtypeId.length) {
        	filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, publicationSubtypeId[0]);
    	}
        if (null != text && 0 < text.length) {
        	filters.put(PublicationSeriesDao.TEXT_SEARCH, text[0]);
        }
        if (null != active && 0 < active.length) {
        	filters.put(PublicationSeriesDao.ACTIVE_SEARCH, active[0].toUpperCase());
        }
    	filters.putAll(buildPaging(pageRowStart, pageSize, pageNumber));
        SearchResults results = new SearchResults();
        results.setPageSize(pageSize);
        results.setPageRowStart(pageRowStart);
        results.setRecords(busService.getObjects(filters));
        results.setRecordCount(busService.getObjectCount(filters));

        return results;
    }

    @RequestMapping(value={"/{id}"}, method=RequestMethod.GET)
    public @ResponseBody PublicationSeries getPublicationSeries(HttpServletRequest request, HttpServletResponse response,
                @PathVariable("id") String id) {
        LOG.debug("getPublicationSeries");
        setHeaders(response);
        PublicationSeries rtn = busService.getObject(PubsUtilities.parseInteger(id));
        if (null == rtn) {
        	response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        return rtn;
    }
	
	@RequestMapping(method=RequestMethod.POST)
	@Transactional
	public @ResponseBody PublicationSeries createPublicationSeries(@RequestBody PublicationSeries pubSeries, HttpServletResponse response) {
		LOG.debug("createPublicationSeries");
        setHeaders(response);
        PublicationSeries result = busService.createObject(pubSeries);
		if (null != result && result.getValidationErrors().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return result;
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
    @Transactional
    public @ResponseBody PublicationSeries updatePublicationSeries(@RequestBody PublicationSeries pubSeries, @PathVariable String id, HttpServletResponse response) {
		LOG.debug("updateUsgsContributor");
        setHeaders(response);
        PublicationSeries result = busService.updateObject(pubSeries);
        if (null != result && (null == result.getValidationErrors() || result.getValidationErrors().isEmpty())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return result;
    }

    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    @Transactional
    public @ResponseBody ValidationResults deletePublicationSeries(@PathVariable String id, HttpServletResponse response) {
        setHeaders(response);
        ValidationResults result = busService.deleteObject(PubsUtilities.parseInteger(id));
        if (null != result && result.isEmpty()) {
        	response.setStatus(HttpServletResponse.SC_OK);
        } else {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return result;
    }

}
