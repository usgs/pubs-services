package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping(produces=MediaType.APPLICATION_JSON_VALUE)
public class AffliliationMvcService extends MvcService<Affiliation<?>> {

	private static final Logger LOG = LoggerFactory.getLogger(AffliliationMvcService.class);
	
	private IBusService<CostCenter> costCenterBusService;
	private IBusService<OutsideAffiliation> outsideAffiliationBusService;

	@Autowired
	public AffliliationMvcService(@Qualifier("costCenterBusService") IBusService<CostCenter> costCenterBusService,
			@Qualifier("outsideAffiliationBusService") IBusService<OutsideAffiliation> outsideAffiliationBusService) {
		this.costCenterBusService = costCenterBusService;
		this.outsideAffiliationBusService = outsideAffiliationBusService;
	}

	@GetMapping(value={"/costcenter"})
	@JsonView(View.MP.class)
	public List<CostCenter> getCostCenters(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("getCostCenters");
		setHeaders(response);
		Map<String, Object> filters = null;
		List<CostCenter> rtn = costCenterBusService.getObjects(filters);
		if (null == rtn) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return rtn;
	}

	@GetMapping(value={"/costcenter/{id}"})
	@JsonView(View.MP.class)
	public CostCenter getCostCenter(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id) {
		LOG.debug("getCostCenter");
		setHeaders(response);
		CostCenter rtn = costCenterBusService.getObject(PubsUtilities.parseInteger(id));
		if (null == rtn) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return rtn;
	}

	@PostMapping(value={"/costcenter"})
	@JsonView(View.MP.class)
	@Transactional
	public CostCenter createCostCenter(@RequestBody CostCenter costCenter, HttpServletResponse response) {
		LOG.debug("createCostCenter");
		setHeaders(response);
		CostCenter result = costCenterBusService.createObject(costCenter);
		if (null != result && result.getValidationErrors().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return result;
	}

	@DeleteMapping(value="/costcenter/{id}")
	@Transactional
	@JsonView(View.MP.class)
	public @ResponseBody ValidationResults deleteCostCenter(@PathVariable String id, HttpServletResponse response) {
		LOG.debug("deleteCostCenter");
		setHeaders(response);
		ValidationResults result = costCenterBusService.deleteObject(PubsUtilities.parseInteger(id));
		if (null != result && result.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return result;
	}

	@PutMapping(value="/costcenter/{id}")
	@JsonView(View.MP.class)
	@Transactional
	public CostCenter updateCostCenter(@RequestBody CostCenter costCenter, @PathVariable String id, HttpServletResponse response) {
		LOG.debug("updateCostCenter");
		setHeaders(response);
		
		CostCenter result = costCenter;
		ValidatorResult idNotMatched = PubsUtilities.validateIdsMatch(id, costCenter);
		
		if (null != idNotMatched) {
			result = costCenterBusService.updateObject(costCenter);
		} else {
			result.addValidatorResult(idNotMatched);
		}
		
		if (null != result && (null == result.getValidationErrors() || result.getValidationErrors().isEmpty())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return result;
	}


	@GetMapping(value={"/outsideaffiliation"})
	@JsonView(View.MP.class)
	public List<OutsideAffiliation> getOutsideAffiliations(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("getOutsideAffiliations");
		setHeaders(response);
		Map<String, Object> filters = null;
		List<OutsideAffiliation> rtn = outsideAffiliationBusService.getObjects(filters);
		if (null == rtn) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return rtn;
	}

	@GetMapping(value={"/outsideaffiliation/{id}"})
	@JsonView(View.MP.class)
	public OutsideAffiliation getOutsideAffiliation(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id) {
		LOG.debug("getOutsideAffiliation");
		setHeaders(response);
		OutsideAffiliation rtn = outsideAffiliationBusService.getObject(PubsUtilities.parseInteger(id));
		if (null == rtn) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}
		return rtn;
	}

	@PostMapping(value={"/outsideaffiliation"})
	@JsonView(View.MP.class)
	@Transactional
	public OutsideAffiliation createOutsideAffiliation(@RequestBody OutsideAffiliation outsideAffiliation, HttpServletResponse response) {
		LOG.debug("createOutsideAffiliation");
		setHeaders(response);
		OutsideAffiliation result = outsideAffiliationBusService.createObject(outsideAffiliation);
		if (null != result && result.getValidationErrors().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return result;
	}

	@DeleteMapping(value="/outsideaffiliation/{id}")
	@Transactional
	@JsonView(View.MP.class)
	public @ResponseBody ValidationResults deleteOutsideAffiliation(@PathVariable String id, HttpServletResponse response) {
		LOG.debug("deleteOutsideAffiliation");
		setHeaders(response);
		ValidationResults result = outsideAffiliationBusService.deleteObject(PubsUtilities.parseInteger(id));
		if (null != result && result.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return result;
	}

	@PutMapping(value="/outsideaffiliation/{id}")
	@JsonView(View.MP.class)
	@Transactional
	public OutsideAffiliation updateOutsideAffiliation(@RequestBody OutsideAffiliation outsideAffiliation, @PathVariable String id, HttpServletResponse response) {
		LOG.debug("updateOutsideAffiliation");
		setHeaders(response);
		
		OutsideAffiliation result = outsideAffiliation;
		ValidatorResult idNotMatched = PubsUtilities.validateIdsMatch(id, outsideAffiliation);
		
		if (null == idNotMatched) {
			result = outsideAffiliationBusService.updateObject(outsideAffiliation);
		} else {
			result.addValidatorResult(idNotMatched);
		}

		if (null != result && (null == result.getValidationErrors() || result.getValidationErrors().isEmpty())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return result;
	}
}