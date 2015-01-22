package gov.usgs.cida.pubs.webservice.mp;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.json.ResponseView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;
import gov.usgs.cida.pubs.webservice.MvcService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
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
@RequestMapping(value = "mppublications", produces="application/json")
public class MpPublicationMvcService extends MvcService<MpPublication> {
    private static final Logger LOG = LoggerFactory.getLogger(MpPublicationMvcService.class);

    private final IBusService<Publication<?>> pubBusService;
    private final IMpPublicationBusService busService;

    @Autowired
	public MpPublicationMvcService(@Qualifier("publicationBusService")
    		final IBusService<Publication<?>> pubBusService,
    		@Qualifier("mpPublicationBusService")
    		final IMpPublicationBusService busService) {
    	this.pubBusService = pubBusService;
    	this.busService = busService;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    @ResponseView(IMpView.class)
    public @ResponseBody SearchResults getPubs(
    		@RequestParam(value="q", required=false) String searchTerms, //single string search
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
            @RequestParam(value="page_row_start", required=false, defaultValue = "0") String pageRowStart,
            @RequestParam(value="page_size", required=false, defaultValue = "25") String pageSize,
            @RequestParam(value="listId", required=false) String[] listId,
			HttpServletResponse response) {

        setHeaders(response);

        Map<String, Object> filters = new HashMap<>();

    	configureSingleSearchFilters(filters, searchTerms);

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
    	addToFiltersIfNotNull(filters, "pageRowStart", pageRowStart);
    	addToFiltersIfNotNull(filters, "pageSize", pageSize);
    	addToFiltersIfNotNull(filters, "listId", listId);
    	
    	filters.put("orderby", buildOrderBy(null));

        List<Publication<?>> pubs = pubBusService.getObjects(filters);
        Integer totalPubsCount = pubBusService.getObjectCount(filters);
        SearchResults results = new SearchResults();
        results.setPageSize(pageSize);
        results.setPageRowStart(pageRowStart);
        results.setRecords(pubs);
        results.setRecordCount(totalPubsCount);

        return results;
    }

    @RequestMapping(value="{publicationId}", method=RequestMethod.GET)
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody MpPublication getMpPublication(HttpServletRequest request, HttpServletResponse response,
                @PathVariable("publicationId") String publicationId) {
        LOG.debug("getMpPublication");
        setHeaders(response);
        Integer id = PubsUtilities.parseInteger(publicationId);
        MpPublication rtn = new MpPublication();
        ValidatorResult locked = busService.checkAvailability(id);
        if (null == locked) {
        	rtn = busService.getObject(id);
	        if (null == rtn) {
	        	response.setStatus(HttpStatus.NOT_FOUND.value());
	        }
        } else {
        	rtn.addValidatorResult(locked);
        	response.setStatus(HttpStatus.CONFLICT.value());
        }
        return rtn;
    }

    @RequestMapping(value="{indexId}/preview", method=RequestMethod.GET)
    @ResponseView(IMpView.class)
    @Transactional(readOnly = true)
    public @ResponseBody MpPublication getMpPublicationPreview(HttpServletRequest request, HttpServletResponse response,
                @PathVariable("indexId") String indexId) {
        LOG.debug("getMpPublication");
        setHeaders(response);
        MpPublication rtn = busService.getByIndexId(indexId);
        if (null == rtn) {
        	response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        return rtn;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody MpPublication createPub(@RequestBody MpPublication pub, HttpServletResponse response) {
        setHeaders(response);
        MpPublication newPub = busService.createObject(pub);
        if (null != newPub && newPub.getValidationErrors().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return newPub;
    }

    @RequestMapping(value = "{publicationId}", method = RequestMethod.PUT)
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody MpPublication updateMpPublication(@RequestBody MpPublication pub, @PathVariable String publicationId,
    		HttpServletResponse response) {
        setHeaders(response);
        Integer id = PubsUtilities.parseInteger(publicationId);
        MpPublication rtn = pub;
        ValidatorResult locked = busService.checkAvailability(id);
        if (null == locked) {
        	rtn = busService.updateObject(pub);
            if (null != rtn && rtn.getValidationErrors().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
        	rtn.addValidatorResult(locked);
        	response.setStatus(HttpStatus.CONFLICT.value());
        }
        return rtn;
    }

    @RequestMapping(value = "{publicationId}", method = RequestMethod.DELETE)
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody ValidationResults deletePub(@PathVariable String publicationId, HttpServletResponse response) {
        setHeaders(response);
        Integer id = PubsUtilities.parseInteger(publicationId);
        ValidationResults rtn = new ValidationResults();
        ValidatorResult locked = busService.checkAvailability(id);
        if (null == locked) {
	        rtn = busService.deleteObject(id);
	        if (null != rtn && rtn.isEmpty()) {
	        	response.setStatus(HttpServletResponse.SC_OK);
	        } else {
	        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        }
        } else {
        	rtn.addValidatorResult(locked);
        	response.setStatus(HttpStatus.CONFLICT.value());
        }
        return rtn;
    }

    @RequestMapping(value = "publish", method = RequestMethod.POST)
    @ResponseView(IMpView.class)
	@Transactional
	public @ResponseBody ValidationResults publishPub(@RequestBody MpPublication pub, HttpServletResponse response) {
        setHeaders(response);
        ValidationResults rtn = new ValidationResults();
        ValidatorResult locked = busService.checkAvailability(pub.getId());
        if (null == locked) {
        	rtn = busService.publish(pub.getId());
	        if (null != rtn && rtn.isEmpty()) {
	        	response.setStatus(HttpServletResponse.SC_OK);
	        } else if (null != rtn && rtn.toString().contains("Publication does not exist.")) {
	        	response.setStatus(HttpStatus.NOT_FOUND.value());
	        } else {
	        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        }
        } else {
        	rtn.addValidatorResult(locked);
        	response.setStatus(HttpStatus.CONFLICT.value());
        }
        return rtn;
	}

    @RequestMapping(value = "release", method = RequestMethod.POST)
    @ResponseView(IMpView.class)
	@Transactional
	public @ResponseBody ValidationResults releasePub(@RequestBody MpPublication pub, HttpServletResponse response) {
        setHeaders(response);
        ValidationResults rtn = new ValidationResults();
        ValidatorResult locked = busService.checkAvailability(pub.getId());
        if (null == locked) {
        	response.setStatus(HttpServletResponse.SC_OK);
        	busService.releaseLocksPub(pub.getId());
        } else {
        	rtn.addValidatorResult(locked);
        	response.setStatus(HttpStatus.CONFLICT.value());
        }
        return rtn;
	}
	
}
