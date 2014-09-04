package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.json.ResponseView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ContributorMvcService extends MvcService<Contributor<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(ContributorMvcService.class);
	
	@Autowired
	private IBusService corporateContributorBusService;
	
	@Autowired
	private IBusService personContributorBusService;
	
    @RequestMapping(value={"/contributor/{contributorId}"}, method=RequestMethod.GET, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseView(IMpView.class)
    public @ResponseBody Contributor<?> getContributor(HttpServletRequest request, HttpServletResponse response,
                @PathVariable("contributorId") String contributorId) {
        LOG.debug("getContributor");
        Contributor<?> rtn = null;
        if (validateParametersSetHeaders(request, response)) {
            rtn = Contributor.getDao().getById(PubsUtilities.parseInteger(contributorId));
        }
        return rtn;
    }
	
	@RequestMapping(value = {"/person/{contributorId}"}, method = RequestMethod.GET, produces = PubsConstants.MIME_TYPE_APPLICATION_JSON)
	@ResponseView(IMpView.class)
	public @ResponseBody
	Contributor<?> getPerson(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("contributorId") String contributorId) {
		LOG.debug("getPerson");
		Contributor<?> rtn = null;
		if (validateParametersSetHeaders(request, response)) {
			rtn = (Contributor) personContributorBusService.getObject(PubsUtilities.parseInteger(contributorId));
		}
		return rtn;
	}
	
	@RequestMapping(value = {"/usgscontributor"}, method = RequestMethod.POST, produces = PubsConstants.MIME_TYPE_APPLICATION_JSON)
	@ResponseView(IMpView.class)
	public @ResponseBody
	Contributor<?> createUsgsContributor(@RequestBody UsgsContributor person
			) {
		LOG.debug("createUsgsContributor");
		Contributor<?> result = null;
		if (null != person) {
			Contributor createdPerson = (Contributor) personContributorBusService.createObject(person);
			if (null != createdPerson) {
				Integer id = createdPerson.getId();
				result = (Contributor) personContributorBusService.getObject(id);
			}
			
		}
		return result;
	}
	
	@RequestMapping(value = {"/outsidecontributor"}, method = RequestMethod.POST, produces = PubsConstants.MIME_TYPE_APPLICATION_JSON)
	@ResponseView(IMpView.class)
	public @ResponseBody
	Contributor<?> createUsgsContributor(@RequestBody OutsideContributor person
			) {
		LOG.debug("createUsgsContributor");
		Contributor<?> result = null;
		if (null != person) {
			Contributor createdPerson = (Contributor) personContributorBusService.createObject(person);
			if (null != createdPerson) {
				Integer id = createdPerson.getId();
				result = (Contributor) personContributorBusService.getObject(id);
			}
			
		}
		return result;
	}
	
	@RequestMapping(value = {"/person"}, method = RequestMethod.POST, produces = PubsConstants.MIME_TYPE_APPLICATION_JSON)
	@ResponseView(IMpView.class)
	public @ResponseBody
	Contributor<?> createPerson(@RequestBody PersonContributor person
			) {
		LOG.debug("createPerson");
		Contributor<?> result = null;
		if (null != person) {
			Contributor createdPerson = (Contributor) personContributorBusService.createObject(person);
			if (null != createdPerson) {
				Integer id = createdPerson.getId();
				result = (Contributor) personContributorBusService.getObject(id);
			}
			
		}
		return result;
	}
	
	@RequestMapping(value = {"/corporation/{contributorId}"}, method = RequestMethod.GET, produces = PubsConstants.MIME_TYPE_APPLICATION_JSON)
	@ResponseView(IMpView.class)
	public @ResponseBody
	Contributor<?> getCorporation(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("contributorId") String contributorId) {
		LOG.debug("getCorporation");
		Contributor<?> rtn = null;
		if (validateParametersSetHeaders(request, response)) {
			rtn = (Contributor) corporateContributorBusService.getObject(PubsUtilities.parseInteger(contributorId));
		}
		return rtn;
	}
	
	@RequestMapping(value = {"/corporation"}, method = RequestMethod.POST, produces = PubsConstants.MIME_TYPE_APPLICATION_JSON)
	@ResponseView(IMpView.class)
	public @ResponseBody
	Contributor<?> createCorporation(@RequestBody CorporateContributor corporation
			) {
		LOG.debug("createCorporation");
		Contributor<?> result = null;
		if (null != corporation) {
			Contributor createdCorporation = (Contributor) corporateContributorBusService.createObject(corporation);
			if (null != createdCorporation) {
				Integer id = createdCorporation.getId();
				result = (Contributor) corporateContributorBusService.getObject(id);
			}
			
		}
		return result;
	}
}
