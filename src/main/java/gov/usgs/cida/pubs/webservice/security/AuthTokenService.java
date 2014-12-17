package gov.usgs.cida.pubs.webservice.security;

import gov.usgs.cida.auth.model.AuthToken;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IMpPublicationBusService;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Controller
public class AuthTokenService {

	private final AuthenticationService authenticationService;
	private final IMpPublicationBusService busService;

	@Autowired
	public AuthTokenService(final AuthenticationService authenticationService,
			@Qualifier("mpPublicationBusService")
	final IMpPublicationBusService busService) {
		this.authenticationService = authenticationService;
		this.busService = busService;
	}

	@RequestMapping(value={"/auth/token"}, method=RequestMethod.POST, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
	@ResponseStatus(value=HttpStatus.OK)
	public @ResponseBody ObjectNode getToken(
			@RequestParam(value="username", required=false) String username, 
			@RequestParam(value="password", required=false) String password) throws UnauthorizedException {

		AuthToken token = authenticationService.authenticate(username, password);

		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("token", token.getTokenId());
		node.put("expires", token.getExpires() != null ? token.getExpires().toString() : "");
		return node;
	}

	@RequestMapping(value={"/auth/logout"}, method=RequestMethod.POST, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
	@ResponseStatus(value=HttpStatus.OK)
	public @ResponseBody ObjectNode logout(HttpServletRequest request) {
		busService.releaseLocksUser(PubsUtilities.getUsername());
		String token = TokenSecurityFilter.getTokenFromHeader(request);
		boolean invalidated = authenticationService.invalidateToken(token);
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("status", invalidated ? "success" : "failed");
		return node;
	}

	@ExceptionHandler({ UnauthorizedException.class })
	@ResponseBody
	public ObjectNode handleException(UnauthorizedException ex,
			HttpServletResponse response) {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("reason", ex.getMessage());
		return node;
	}
}
