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

import gov.usgs.cida.pubs.busservice.intfc.IMpListPublicationBusService;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.webservice.MvcService;

@RestController
@RequestMapping(value = "lists/{listId}/pubs", produces="application/json")
public class MpListPublicationMvcService extends MvcService<MpListPublication> {

	private final IMpListPublicationBusService busService;

	@Autowired
	public MpListPublicationMvcService(@Qualifier("mpListPublicationBusService")
										final IMpListPublicationBusService busService) {
		this.busService = busService;
	}

	@PostMapping
	@Transactional
	public @ResponseBody Collection<MpListPublication> addPubToList(@PathVariable String listId,
			@RequestParam("publicationId") String[] publicationIds, HttpServletResponse response) {
		setHeaders(response);
		response.setStatus(HttpServletResponse.SC_CREATED);
		return busService.addPubToList(PubsUtilities.parseInteger(listId), publicationIds);
	}

	@DeleteMapping(value = "{publicationId}")
	@Transactional
	public @ResponseBody ValidationResults removePubFromList(@PathVariable String listId,
			@PathVariable("publicationId") String publicationId, HttpServletResponse response) {
		setHeaders(response);
		return busService.removePubFromList(PubsUtilities.parseInteger(listId), PubsUtilities.parseInteger(publicationId));
	}

}
