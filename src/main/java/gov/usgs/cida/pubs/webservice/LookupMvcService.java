package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.ContributorTypeDao;
import gov.usgs.cida.pubs.dao.CorporateContributorDao;
import gov.usgs.cida.pubs.dao.CostCenterDao;
import gov.usgs.cida.pubs.dao.LinkFileTypeDao;
import gov.usgs.cida.pubs.dao.LinkTypeDao;
import gov.usgs.cida.pubs.dao.OutsideAffiliationDao;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.dao.PublicationSeriesDao;
import gov.usgs.cida.pubs.dao.PublicationSubtypeDao;
import gov.usgs.cida.pubs.dao.PublicationTypeDao;
import gov.usgs.cida.pubs.dao.PublishingServiceCenterDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.json.ResponseView;
import gov.usgs.cida.pubs.json.view.intfc.ILookupView;

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
@RequestMapping(value="lookup", method=RequestMethod.GET, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
public class LookupMvcService extends MvcService<PublicationType> {
    private static final Logger LOG = LoggerFactory.getLogger(LookupMvcService.class);

    @RequestMapping("publicationtypes")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<PublicationType> getPublicationTypes(HttpServletRequest request, HttpServletResponse response,
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

    @RequestMapping("publicationtype/{publicationTypeId}/publicationsubtypes")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<PublicationSubtype> getPublicationSubypesREST(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value=TEXT_SEARCH, required=false) String[] text,
                @PathVariable("publicationTypeId") String publicationTypeId) {
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

    @RequestMapping("publicationsubtypes")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<PublicationSubtype> getPublicationSubypesQuery(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value=TEXT_SEARCH, required=false) String[] text,
                @RequestParam(value="publicationtypeid", required=false) String[] publicationTypeId) {
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

    @RequestMapping("publicationtype/{publicationTypeId}/publicationsubtype/{publicationSubtypeId}/publicationseries")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<PublicationSeries> getPublicationSeriesREST(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable("publicationTypeId") String publicationTypeId,
    		@PathVariable("publicationSubtypeId") String publicationSubtypeId,
    		@RequestParam(value=TEXT_SEARCH, required=false) String[] text,
    		@RequestParam(value=ACTIVE_SEARCH, required=false) String[] active) {
        LOG.debug("publicationSeries");
        Collection<PublicationSeries> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            Map<String, Object> filters = new HashMap<>();
            filters.put(PublicationSeriesDao.SUBTYPE_SEARCH, publicationSubtypeId);
            if (null != text && 0 < text.length) {
                filters.put(PublicationSeriesDao.TEXT_SEARCH, text[0]);
            }
            if (null != active && 0 < active.length) {
                filters.put(PublicationSeriesDao.ACTIVE_SEARCH, active[0].toUpperCase());
            }
            rtn = PublicationSeries.getDao().getByMap(filters);
        }
        return rtn;
    }

    @RequestMapping("publicationseries")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<PublicationSeries> getPublicationSeriesQuery(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value=TEXT_SEARCH, required=false) String[] text,
                @RequestParam(value="publicationSubtypeId", required=false) String[] publicationSubtypeId,
                @RequestParam(value=ACTIVE_SEARCH, required=false) String[] active) {
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
            if (null != active && 0 < active.length) {
                filters.put(PublicationSeriesDao.ACTIVE_SEARCH, active[0].toUpperCase());
            }
            rtn = PublicationSeries.getDao().getByMap(filters);
        }
        return rtn;
    }

    @RequestMapping("costcenters")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<Affiliation<?>> getCostCenters(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value=TEXT_SEARCH, required=false) String[] text,
                @RequestParam(value=ACTIVE_SEARCH, required=false) String[] active) {
        LOG.debug("CostCenter");
        Collection<Affiliation<?>> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            Map<String, Object> filters = new HashMap<>();
            if (null != text && 0 < text.length) {
                filters.put(CostCenterDao.TEXT_SEARCH, text[0]);
            }
            if (null != active && 0 < active.length) {
                filters.put(CostCenterDao.ACTIVE_SEARCH, active[0].toUpperCase());
            }
            rtn = CostCenter.getDao().getByMap(filters);
        }
        return rtn;
    }
	
	@RequestMapping("outsideaffiliates")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<Affiliation<?>> getOutsideAffiliates(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value=TEXT_SEARCH, required=false) String[] text,
                @RequestParam(value=ACTIVE_SEARCH, required=false) String[] active) {
        LOG.debug("OutsideAffiliate");
        Collection<Affiliation<?>> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            Map<String, Object> filters = new HashMap<>();
            if (null != text && 0 < text.length) {
                filters.put(OutsideAffiliationDao.TEXT_SEARCH, text[0]);
            }
            if (null != active && 0 < active.length) {
                filters.put(OutsideAffiliationDao.ACTIVE_SEARCH, active[0].toUpperCase());
            }
            rtn = OutsideAffiliation.getDao().getByMap(filters);
        }
        return rtn;
    }

    @RequestMapping("contributortypes")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<ContributorType> getContributorTypes(HttpServletRequest request, HttpServletResponse response,
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

    @RequestMapping("linktypes")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<LinkType> getLinkTypes(HttpServletRequest request, HttpServletResponse response,
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

    @RequestMapping("linkfiletypes")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<LinkFileType> getLinkFileTypes(HttpServletRequest request, HttpServletResponse response,
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

    @RequestMapping("people")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<Contributor<?>> getContributorsPeople(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value=TEXT_SEARCH, required=false) String[] text) {
        LOG.debug("Contributor - People");
        Collection<Contributor<?>> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            Map<String, Object> filters = new HashMap<>();
            if (null != text && 0 < text.length) {
                filters.put(PersonContributorDao.TEXT_SEARCH, text[0]);
            }
            rtn = PersonContributor.getDao().getByMap(filters);
        }
        return rtn;
    }

    @RequestMapping("corporations")
    @ResponseView(ILookupView.class)
    public @ResponseBody Collection<Contributor<?>> getContributorsCorporations(HttpServletRequest request, HttpServletResponse response,
                @RequestParam(value=TEXT_SEARCH, required=false) String[] text) {
        LOG.debug("Contributor - Corporations");
        Collection<Contributor<?>> rtn = new ArrayList<>();
        if (validateParametersSetHeaders(request, response)) {
            Map<String, Object> filters = new HashMap<>();
            if (null != text && 0 < text.length) {
                filters.put(CorporateContributorDao.TEXT_SEARCH, text[0]);
            }
            rtn = CorporateContributor.getDao().getByMap(filters);
        }
        return rtn;
    }

    @RequestMapping("publishingServiceCenters")
    @ResponseView(ILookupView.class)
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

}
