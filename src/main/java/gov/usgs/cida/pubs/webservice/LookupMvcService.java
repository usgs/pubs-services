package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.intfc.ILookup.LookupView;
import gov.usgs.cida.pubs.json.ResponseView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/lookup")
public class LookupMvcService extends MvcService<PublicationType> {
    private static final Logger LOG = LoggerFactory.getLogger(LookupMvcService.class);

    @RequestMapping(value={"/publicationtypes"}, method=RequestMethod.GET, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseView(LookupView.class)
    public @ResponseBody Collection<PublicationType> getPublicationTypes(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value="text", required=false) String[] text) {
        LOG.debug("publicationType");
        Collection<PublicationType> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            Map<String, Object> filters = new HashMap<>();
            if (null != text && 0 < text.length) {
                filters.put("name", text[0]);
            }
            rtn = PublicationType.getDao().getByMap(filters);
        }
        return rtn;
    }

    @RequestMapping(value={"/publicationtype/{publicationTypeId}/publicationsubtypes"}, method=RequestMethod.GET, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseView(LookupView.class)
    public @ResponseBody Collection<PublicationSubtype> getPublicationSubypesREST(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value="text", required=false) String[] text,
                @PathVariable("publicationTypeId") String publicationTypeId) {
        LOG.debug("publicationSubtype");
        Collection<PublicationSubtype> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
            Map<String, Object> filters = new HashMap<>();
            filters.put("publicationTypeId", publicationTypeId);
            if (null != text && 0 < text.length) {
                filters.put("name", text[0]);
            }
            rtn = PublicationSubtype.getDao().getByMap(filters);
        }
        return rtn;
    }

    @RequestMapping(value={"/publicationsubtypes"}, method=RequestMethod.GET, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseView(LookupView.class)
    public @ResponseBody Collection<PublicationSubtype> getPublicationSubypesQuery(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value="text", required=false) String[] text,
                @RequestParam(value="publicationtypeid", required=false) String[] publicationTypeId) {
        LOG.debug("publicationSubtype");
        Collection<PublicationSubtype> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
            Map<String, Object> filters = new HashMap<>();
            if (null != publicationTypeId && 0 < publicationTypeId.length) {
                filters.put("publicationTypeId", publicationTypeId[0]);
            }
            if (null != text && 0 < text.length) {
                filters.put("name", text[0]);
            }
            rtn = PublicationSubtype.getDao().getByMap(filters);
            }
        return rtn;
    }

    @RequestMapping(value={"/publicationtype/{publicationTypeId}/publicationsubtype/{publicationSubtypeId}/publicationseries"}, method=RequestMethod.GET, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseView(LookupView.class)
    public @ResponseBody Collection<PublicationSeries> getPublicationSeriesREST(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value="text", required=false) String[] text,
                @PathVariable("publicationTypeId") String publicationTypeId,
                @PathVariable("publicationSubtypeId") String publicationSubtypeId) {
        LOG.debug("publicationSeries");
        Collection<PublicationSeries> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
            Map<String, Object> filters = new HashMap<>();
            filters.put("publicationSubtypeId", publicationSubtypeId);
            if (null != text && 0 < text.length) {
                filters.put("name", text[0]);
            }
            rtn = PublicationSeries.getDao().getByMap(filters);
        }
        return rtn;
    }

    @RequestMapping(value={"/publicationseries"}, method=RequestMethod.GET, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseView(LookupView.class)
    public @ResponseBody Collection<PublicationSeries> getPublicationSeriesQuery(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value="text", required=false) String[] text,
                @RequestParam(value="publicationsubtypeid", required=false) String[] publicationSubtypeId) {
        LOG.debug("publicationSeries");
        Collection<PublicationSeries> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
            Map<String, Object> filters = new HashMap<>();
            if (null != publicationSubtypeId && 0 < publicationSubtypeId.length) {
                filters.put("publicationSubtypeId", publicationSubtypeId[0]);
            }
            if (null != text && 0 < text.length) {
                filters.put("name", text[0]);
            }
            rtn = PublicationSeries.getDao().getByMap(filters);
        }
        return rtn;
    }

    @RequestMapping(value={"/costcenters"}, method=RequestMethod.GET, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseView(LookupView.class)
    public @ResponseBody Collection<CostCenter> getCostCenters(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value="text", required=false) String[] text) {
        LOG.debug("CostCenter");
        Collection<CostCenter> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
            Map<String, Object> filters = new HashMap<>();
            if (null != text && 0 < text.length) {
                filters.put("name", text[0]);
            }
            rtn = CostCenter.getDao().getByMap(filters);
        }
        return rtn;
    }

}
