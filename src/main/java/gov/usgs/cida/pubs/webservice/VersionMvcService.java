package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.domain.ApplicationVersion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="version")
public class VersionMvcService {

	@GetMapping
	public String getVersion() {
		return ApplicationVersion.getVersion();
	}
}