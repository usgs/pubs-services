package gov.usgs.cida.pubs.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@Controller
public class AuthController {

	private final IMpPublicationBusService mpPublicationBusService;

	@Autowired
	public AuthController(@Qualifier("mpPublicationBusService")
			final IMpPublicationBusService mpPublicationBusService) {
		this.mpPublicationBusService = mpPublicationBusService;
	}

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstants.API_KEY_NAME) })
	@RequestMapping(value="auth/logout", method=RequestMethod.POST)
	@ResponseStatus(value=HttpStatus.OK)
	public void logout() {
		mpPublicationBusService.releaseLocksUser(PubsUtilities.getUsername());
	}

}
