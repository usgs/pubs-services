package gov.usgs.cida.pubs.webservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.usgs.cida.pubs.domain.ApplicationVersion;

@Controller
public class VersionMvcService {

    @RequestMapping(value="version", method=RequestMethod.GET)
    @ResponseBody
    public String getVersion() {
        return ApplicationVersion.getVersion();
    }

}
