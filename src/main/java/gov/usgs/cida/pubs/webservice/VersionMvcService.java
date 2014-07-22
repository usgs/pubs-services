package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.ApplicationVersion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VersionMvcService {

    @RequestMapping(value="version", method=RequestMethod.GET)
    @ResponseBody
    public String getVersion() {
        return ApplicationVersion.getVersion();
    }

}
