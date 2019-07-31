package gov.usgs.cida.pubs.webservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class VersionController {

	@RequestMapping(value="version", method=RequestMethod.GET)
	public String getVersion() {
		return "redirect:/about/info";
	}
}