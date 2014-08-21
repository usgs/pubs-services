package gov.usgs.cida.pubs.webservice.security;

import gov.usgs.cida.pubs.PubsConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Controller
public class AuthTokenService {
    @RequestMapping(value={"/auth/ad/token"}, method=RequestMethod.POST, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseStatus(value=HttpStatus.OK)
    public @ResponseBody ObjectNode getToken(HttpServletRequest request, HttpServletResponse response) {
    	//TODO get token from a CIDA lib
    	ObjectNode node = JsonNodeFactory.instance.objectNode();
    	node.put("token", "a-random-token");
        return node;
    }

    @RequestMapping(value={"/auth/logout"}, method=RequestMethod.POST, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseStatus(value=HttpStatus.OK)
    public @ResponseBody ObjectNode logout(HttpServletRequest request, HttpServletResponse response) {
    	ObjectNode node = JsonNodeFactory.instance.objectNode();
    	node.put("status", "success");
        return node;
    }
}
