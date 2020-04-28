package gov.usgs.cida.pubs.webservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RedirectController {

	@Value("${swagger.apiDocsUrl}")
	private String swaggerApiDocsUrl;

	@RequestMapping(value="version", method=RequestMethod.GET)
	public String getVersion() {
		return "redirect:/about/info";
	}

	@RequestMapping(value="swagger", method=RequestMethod.GET)
	public String getSwagger() {
		return "redirect:/swagger-ui/index.html?url=" + swaggerApiDocsUrl;
	}
}