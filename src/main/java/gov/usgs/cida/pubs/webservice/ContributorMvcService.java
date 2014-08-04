package gov.usgs.cida.pubs.webservice;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.json.ResponseView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ContributorMvcService extends MvcService<PublicationType> {
    private static final Logger LOG = LoggerFactory.getLogger(LookupMvcService.class);

    @RequestMapping(value={"/contributor/{contributorId}"}, method=RequestMethod.GET, produces=PubsConstants.MIME_TYPE_APPLICATION_JSON)
    @ResponseView(IMpView.class)
    public @ResponseBody Contributor getContributor(HttpServletRequest request, HttpServletResponse response,
                @PathVariable("contributorId") String contributorId) {
        LOG.debug("getContributor");
        Contributor rtn = new Contributor();
        if (validateParametersSetHeaders(request, response)) {
            response.setCharacterEncoding(PubsConstants.DEFAULT_ENCODING);
            rtn = Contributor.getDao().getById(PubsUtilities.parseInteger(contributorId));
        }
        return rtn;
    }

}
