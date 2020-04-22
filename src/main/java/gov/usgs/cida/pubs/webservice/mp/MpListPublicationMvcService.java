package gov.usgs.cida.pubs.webservice.mp;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.intfc.IMpListPublicationBusService;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.utility.PubsUtils;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.webservice.MvcService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping(value = "lists/{listId}/pubs", produces=PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE)
public class MpListPublicationMvcService extends MvcService<MpListPublication> {

	private final IMpListPublicationBusService busService;

	@Autowired
	public MpListPublicationMvcService(@Qualifier("mpListPublicationBusService")
										final IMpListPublicationBusService busService) {
		this.busService = busService;
	}

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@PostMapping
	@Transactional
	public @ResponseBody Collection<MpListPublication> addPubToList(@PathVariable String listId,
			@RequestParam("publicationId") String[] publicationIds, HttpServletResponse response) {
		setHeaders(response);
		response.setStatus(HttpServletResponse.SC_CREATED);
		return busService.addPubToList(PubsUtils.parseInteger(listId), publicationIds);
	}

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@DeleteMapping(value = "{publicationId}")
	@Transactional
	public @ResponseBody ValidationResults removePubFromList(@PathVariable String listId,
			@PathVariable("publicationId") String publicationId, HttpServletResponse response) {
		setHeaders(response);
		return busService.removePubFromList(PubsUtils.parseInteger(listId), PubsUtils.parseInteger(publicationId));
	}

}
