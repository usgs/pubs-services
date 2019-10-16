package gov.usgs.cida.pubs.webservice.mp;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.busservice.intfc.ISippProcess;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.Publication;
import gov.usgs.cida.pubs.domain.SearchResults;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.query.PublicationFilterParams;
import gov.usgs.cida.pubs.domain.sipp.SippMpPubRequest;
import gov.usgs.cida.pubs.json.View;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;
import gov.usgs.cida.pubs.webservice.MvcService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping(value = "mppublications", produces="application/json")
public class MpPublicationMvcService extends MvcService<MpPublication> {
	private static final Logger LOG = LoggerFactory.getLogger(MpPublicationMvcService.class);

	private final IBusService<Publication<?>> pubBusService;
	private final IMpPublicationBusService busService;
	private final ISippProcess sippProcess;

	@Autowired
	public MpPublicationMvcService(@Qualifier("publicationBusService")
			final IBusService<Publication<?>> pubBusService,
			@Qualifier("mpPublicationBusService")
			final IMpPublicationBusService busService,
			@Qualifier("sippProcess")
			final ISippProcess sippProcess) {
		this.pubBusService = pubBusService;
		this.busService = busService;
		this.sippProcess = sippProcess;
	}

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@GetMapping
	@JsonView(View.MP.class)
	public @ResponseBody SearchResults getPubs(HttpServletResponse response, PublicationFilterParams filterParams,
			@RequestParam(value=BaseDao.PAGE_SIZE, required=false, defaultValue = "25") String pageSize,
			@RequestParam(value=BaseDao.PAGE_ROW_START, required=false, defaultValue = "0") String pageRowStart) {

		setHeaders(response);

		// set Query parameter defaults
		filterParams.setGlobal(filterParams.getGlobal() == null ? "true" : filterParams.getGlobal());

		Map<String, Object> filters = buildFilters(filterParams);

		List<Publication<?>> pubs = pubBusService.getObjects(filters);
		Integer totalPubsCount = pubBusService.getObjectCount(filters);
		SearchResults results = new SearchResults();
		results.setPageSize(pageSize);
		results.setPageRowStart(pageRowStart);
		results.setRecords(pubs);
		results.setRecordCount(totalPubsCount);

		return results;
	}

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@GetMapping(value="{publicationId}")
	@JsonView(View.MP.class)
	@Transactional
	public @ResponseBody MpPublication getMpPublication(HttpServletRequest request, HttpServletResponse response,
				@PathVariable("publicationId") String publicationId) {
		LOG.debug("getMpPublication");
		setHeaders(response);
		Integer id = PubsUtils.parseInteger(publicationId);
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

	@GetMapping(value="{indexId}/preview")
	@JsonView(View.MP.class)
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

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@PostMapping
	@JsonView(View.MP.class)
	@Transactional
	public @ResponseBody MpPublication createPub(@RequestBody MpPublication pub, HttpServletResponse response) {
		setHeaders(response);
		MpPublication newPub = busService.createObject(pub);
		if (null != newPub && newPub.isValid()) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return newPub;
	}

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@PostMapping(value="/sipp")
	@JsonView(View.MP.class)
	@Transactional
	public @ResponseBody MpPublication createPubViaSipp(@Valid @RequestBody SippMpPubRequest req, HttpServletResponse response) {
		setHeaders(response);
		MpPublication newPub = sippProcess.processInformationProduct(req.getProcessTypeEnum(), req.getIpNumber());
		if (null != newPub && newPub.isValid()) {
			response.setStatus(HttpServletResponse.SC_CREATED);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return newPub;
	}

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@PutMapping(value = "{publicationId}")
	@JsonView(View.MP.class)
	@Transactional
	public @ResponseBody MpPublication updateMpPublication(@RequestBody MpPublication pub, @PathVariable String publicationId, HttpServletResponse response) {
		setHeaders(response);
	
		MpPublication rtn = pub;
		ValidatorResult idNotMatched = PubsUtils.validateIdsMatch(publicationId, pub);

		if (null == idNotMatched) {
			Integer id = PubsUtils.parseInteger(publicationId);
			ValidatorResult locked = busService.checkAvailability(id);
			if (null == locked) {
				rtn = busService.updateObject(pub);
				if (null != rtn && rtn.isValid()) {
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
			} else {
				rtn.addValidatorResult(locked);
				response.setStatus(HttpStatus.CONFLICT.value());
			}
		} else {
			rtn.addValidatorResult(idNotMatched);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return rtn;
	}

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@DeleteMapping(value = "{publicationId}")
	@JsonView(View.MP.class)
	@Transactional
	public @ResponseBody ValidationResults deletePub(@PathVariable String publicationId, HttpServletResponse response) {
		setHeaders(response);
		Integer id = PubsUtils.parseInteger(publicationId);
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

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@DeleteMapping(value = "{publicationId}/purge")
	@JsonView(View.PW.class)
	@Transactional
	public @ResponseBody ValidationResults purgePub(@PathVariable String publicationId, HttpServletResponse response) {
		setHeaders(response);
		Integer id = PubsUtils.parseInteger(publicationId);
		ValidationResults rtn = new ValidationResults();
		ValidatorResult locked = busService.checkAvailability(id);
		if (null == locked) {
			rtn = busService.purgePublication(id);
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

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@PostMapping(value = "publish")
	@JsonView(View.MP.class)
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

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@PostMapping(value="release")
	@JsonView(View.MP.class)
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
