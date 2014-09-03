package gov.usgs.cida.pubs.webservice.mp;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.json.ResponseView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.webservice.MvcService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author drsteini
 *
 */
@Controller
public class MpPublicationMvcService extends MvcService<MpPublication> {
    private static final Logger LOG = LoggerFactory.getLogger(MpPublicationMvcService.class);

    //SQL config for single search 
    private static final String SEARCH_TERM_ORDERBY = "display_to_public_date"; //TODO this is temporary until we get PUB DATE
    private static final String SEARCH_TERM_ORDERBY_DIR = "DESC";
    
    @Autowired
    private IMpPublicationBusService mpPublicationBusService;
    
    @Autowired
    private IMpPublicationBusService busService;

    @RequestMapping(value={"/mppublication/{publicationId}"}, method=RequestMethod.GET, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseView(IMpView.class)
    public @ResponseBody MpPublication getMpPublication(HttpServletRequest request, HttpServletResponse response,
                @PathVariable("publicationId") String publicationId) {
        LOG.debug("getMpPublication");
        MpPublication rtn = new MpPublication();
        if (validateParametersSetHeaders(request, response)) {
            rtn = busService.getObject(PubsUtilities.parseInteger(publicationId));
        }
        //TODO set http status to 404 on a not found?
        return rtn;
    }

    @RequestMapping(value = "mppublications", method = RequestMethod.GET,  produces="application/json")
    @ResponseView(IMpView.class)
    public @ResponseBody SearchResults getPubs(
    		@RequestParam(value="q", required=false) String searchTerms, //single string search
            @RequestParam(value="title", required=false) String[] title,
            @RequestParam(value="abstract", required=false) String[] pubAbstract,
            @RequestParam(value="author", required=false) String[] author,
            @RequestParam(value="prodId", required=false) String[] prodId,
            @RequestParam(value="indexId", required=false) String[] indexId,
            @RequestParam(value="ipdsId", required=false) String[] ipdsId,
            @RequestParam(value="year", required=false) String[] year,
            @RequestParam(value="startYear", required=false) String[] yearStart,
            @RequestParam(value="endYear", required=false) String[] yearEnd,
    		@RequestParam(value="contributingOffice", required=false) String[] contributingOffice,
            @RequestParam(value="seriesName", required=false) String[] reportSeries,
            @RequestParam(value="reportNumber", required=false) String[] reportNumber,
            @RequestParam(value="page_row_start", required=false, defaultValue = "0") String pageRowStart,
            @RequestParam(value="page_size", required=false, defaultValue = "25") String pageSize,
            HttpServletResponse response) {

        Map<String, Object> filters = new HashMap<String, Object>();

    	configureSingleSearchFilters(filters, searchTerms);
    	
    	addToFiltersIfNotNull(filters, "title", title);
    	addToFiltersIfNotNull(filters, "abstract", pubAbstract);
    	addToFiltersIfNotNull(filters, "author", author);
    	addToFiltersIfNotNull(filters, "id", prodId);
    	addToFiltersIfNotNull(filters, "indexId", indexId);
    	addToFiltersIfNotNull(filters, "ipdsId", ipdsId);
    	addToFiltersIfNotNull(filters, "year", year);
    	addToFiltersIfNotNull(filters, "yearStart", yearStart);
    	addToFiltersIfNotNull(filters, "yearEnd", yearEnd);
    	addToFiltersIfNotNull(filters, "contributingOffice", contributingOffice);
    	addToFiltersIfNotNull(filters, "reportSeries", reportSeries);
    	addToFiltersIfNotNull(filters, "reportNumber", reportNumber);
    	addToFiltersIfNotNull(filters, "pageRowStart", pageRowStart);
    	addToFiltersIfNotNull(filters, "pageSize", pageSize);
        
        List<MpPublication> pubs = mpPublicationBusService.getObjects(filters);
        Integer totalPubsCount = mpPublicationBusService.getObjectCount(filters);
        SearchResults results = new SearchResults();
        results.setPageSize(pageSize);
        results.setPageRowStart(pageRowStart);
        results.setRecords(pubs);
        results.setRecordCount(totalPubsCount);
        
        return results;
    }
    
    /**
     * Configures the filters/orderby settings to support single search
     * @param filters
     * @return
     */
    private Map<String, Object> configureSingleSearchFilters(Map<String, Object> filters, String searchTerms) {
        if (StringUtils.isNotEmpty(searchTerms)) {
	    	filters.put("searchTerms", searchTerms.split("[\\s+,+]"));
	    	updateOrderBy(filters, SEARCH_TERM_ORDERBY, SEARCH_TERM_ORDERBY_DIR);
        }
    	return filters;
    }

    @RequestMapping(value = "mppublications", method = RequestMethod.POST, produces="application/json")
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody MpPublication createPub(@RequestBody MpPublication pub, HttpServletResponse response) {
        setHeaders(response);
        MpPublication newPub = busService.createObject(pub);
        if (null != newPub && newPub.getValErrors().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return newPub;
    }

    @RequestMapping(value = "mppublication/{id}", method = RequestMethod.PUT, produces="application/json")
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody MpPublication updateMpPublication(@RequestBody MpPublication pub, @PathVariable String id, HttpServletResponse response) {
        setHeaders(response);
        MpPublication updPub = busService.updateObject(pub);
        if (null != updPub && updPub.getValErrors().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return updPub;
    }

    @RequestMapping(value = "mppublications/{id}", method = RequestMethod.DELETE, produces="application/json")
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody ValidationResults deletePub(@PathVariable String id, HttpServletResponse response) {
        MpPublication pub = new MpPublication();
        pub.setId(id);
        ValidationResults rtn = busService.deleteObject(pub);
        if (null != rtn && rtn.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return rtn;
    }


//    @RequestMapping(value = "mppublications/{id}/publish", method = RequestMethod.POST, produces="application/json")
//    @ResponseBody
//    @Transactional
//    public Map<String,? extends Object> publishPubs(@PathVariable String id, HttpServletResponse response) {
//        return buildResponseMap(response, null, null, busService.publish(parseId(id)));
//    }

}
