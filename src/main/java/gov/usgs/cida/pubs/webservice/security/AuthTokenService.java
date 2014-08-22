package gov.usgs.cida.pubs.webservice.security;

import gov.usgs.cida.auth.client.AuthClient;
import gov.usgs.cida.auth.model.AuthToken;
import gov.usgs.cida.pubs.PubsConstants;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Controller
public class AuthTokenService {
	
	@Autowired
	private AuthClient authClient;
	
    @RequestMapping(value={"/auth/ad/token"}, method=RequestMethod.POST, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseStatus(value=HttpStatus.OK)
    public @ResponseBody ObjectNode getToken(
    		@RequestParam(value="username", required=false) String username, //single string search
            @RequestParam(value="password", required=false) String password) {
    	AuthToken token = authClient.getNewToken(username, password);
    	ObjectNode node = JsonNodeFactory.instance.objectNode();
    	node.put("token", token.getTokenId());
    	node.put("expires", token.getExpires() != null ? token.getExpires().toString() : "");
        return node;
    }

    @RequestMapping(value={"/auth/logout"}, method=RequestMethod.POST, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseStatus(value=HttpStatus.OK)
    public @ResponseBody ObjectNode logout(HttpServletRequest request) {
    	boolean invalidated = authClient.invalidateToken(TokenSecurityFilter.getTokenFromHeader(request));
    	ObjectNode node = JsonNodeFactory.instance.objectNode();
    	node.put("status", invalidated ? "success" : "failed");
        return node;
    }
}
