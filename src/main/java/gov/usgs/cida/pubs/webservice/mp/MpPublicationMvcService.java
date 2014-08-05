package gov.usgs.cida.pubs.webservice.mp;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.json.ResponseView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.webservice.MvcService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author drsteini
 *
 */
@Controller
public class MpPublicationMvcService extends MvcService<MpPublication> {

    private static final Logger LOG = LoggerFactory.getLogger(MpPublicationMvcService.class);

//    private IBusService<Publication<?>> pubBusService;
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
        return rtn;
    }

//    @RequestMapping(value = "publications", method = RequestMethod.GET,  produces="application/json")
//    @ResponseBody
//    public Map<String,? extends Object> getPubs(@RequestParam(value="list_id", required=false) String[] listId,
//            @RequestParam(value="auth_first", required=false) String authFirst,
//            @RequestParam(value="auth_last", required=false) String authLast,
//            @RequestParam(value="prod_id", required=false) String prodId,
//            @RequestParam(value="index_id", required=false) String indexId,
//            @RequestParam(value="pub_type", required=false) String pubType,
//            @RequestParam(value="title", required=false) String title,
//            @RequestParam(value="year", required=false) String year,
//            @RequestParam(value="year_start", required=false) String yearStart,
//            @RequestParam(value="year_end", required=false) String yearEnd,
//            @RequestParam(value="journal", required=false) String journal,
//            @RequestParam(value="report_series", required=false) String reportSeries,
//            @RequestParam(value="report_number", required=false) String reportNumber,
//            @RequestParam(value="page_row_start", required=false) String pageRowStart,
//            @RequestParam(value="page_size", required=false) String pageSize,
//            @RequestParam(value="orderby", required=false) String orderby,
//            @RequestParam(value="orderby_dir", required=false) String orderbyDir,
//            HttpServletResponse response) {
//
//        Map<String, Object> filters = new HashMap<String, Object>();
//
//        if ( ! PubsUtilities.isNullOrEmpty(listId)) {
//            filters.put("listId", PubsUtilities.cleanStringArray(listId));
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(authFirst)) {
//            filters.put("authFirst", authFirst);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(authLast)) {
//            filters.put("authLast", authLast);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(indexId)) {
//            filters.put("indexId", indexId);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(pubType)) {
//            filters.put("pubType", pubType);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(title)) {
//            filters.put("title", title);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(year)) {
//            filters.put("year", year);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(yearStart)) {
//            filters.put("yearStart", yearStart);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(yearEnd)) {
//            filters.put("yearEnd", yearEnd);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(journal)) {
//            filters.put("journal", journal);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(reportSeries)) {
//            filters.put("reportSeries", reportSeries);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(reportNumber)) {
//            filters.put("reportNumber", reportNumber);
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(orderby)) {
//            if (orderby.equalsIgnoreCase("reportnumber")) {
//                filters.put("orderby", "series_number");
//            } else {
//                filters.put("orderby", orderby);
//            }
//        }
//        if ( ! PubsUtilities.isNullOrEmpty(orderbyDir)) {
//            filters.put("orderbyDir", orderbyDir);
//        }
//
//        if (!PubsUtilities.isNullOrEmpty(prodId)) {
//            filters.put("prodId", parseId(prodId));
//        }
//        if (!PubsUtilities.isNullOrEmpty(pageRowStart)) {
//            filters.put("pageRowStart", parseId(pageRowStart));
//        }
//        if (!PubsUtilities.isNullOrEmpty(pageSize)) {
//            filters.put("pageSize", parseId(pageSize));
//        }
//
//        List<Publication<?>> pubs = pubBusService.getObjects(filters);
//        Integer totalPubsCount = pubBusService.getObjectCount(filters);
//        return buildResponseMap(response, pubs, totalPubsCount);
//    }

    @RequestMapping(value = "mppublications", method = RequestMethod.POST, produces="application/json")
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody MpPublication createPub(@RequestBody MpPublication pub, HttpServletResponse response) {
        setHeaders(response);
        MpPublication newPub = busService.createObject(pub);
        return newPub;
    }

    @RequestMapping(value = "mppublication/{id}", method = RequestMethod.PUT, produces="application/json")
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody MpPublication updateMpPublication(@RequestBody MpPublication pub, @PathVariable String id, HttpServletResponse response) {
        setHeaders(response);
        MpPublication updPub = busService.updateObject(pub);
        return updPub;
    }

    @RequestMapping(value = "mppublications/{id}", method = RequestMethod.DELETE, produces="application/json")
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody ValidationResults deletePub(@PathVariable String id, HttpServletResponse response) {
        MpPublication pub = new MpPublication();
        pub.setId(id);
        return busService.deleteObject(pub);
    }

//
//    @RequestMapping(value = "publications/publish", method = RequestMethod.POST, produces="application/json")
//    @ResponseBody
//    @Transactional
//    public Map<String,? extends Object> publishPubs(@RequestParam("prod_id") String prodId, HttpServletResponse response) {
//        return buildResponseMap(response, null, null, busService.publish(parseId(prodId)));
//    }

}
