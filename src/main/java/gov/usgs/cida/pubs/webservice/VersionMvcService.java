package gov.usgs.cida.pubs.webservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.usgs.cida.pubs.domain.ApplicationVersion;

@Controller
public class VersionMvcService {

	@GetMapping(value="version")
	@ResponseBody
	public String getVersion() {
		return ApplicationVersion.getVersion();
	}

}
