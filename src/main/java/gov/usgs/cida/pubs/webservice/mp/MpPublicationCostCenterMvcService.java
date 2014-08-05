package gov.usgs.cida.pubs.webservice.mp;

import gov.usgs.cida.pubs.busservice.intfc.IListBusService;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.json.ResponseView;
import gov.usgs.cida.pubs.json.view.intfc.IMpView;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.webservice.MvcService;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MpPublicationCostCenterMvcService extends MvcService<MpPublicationCostCenter> {

    private static final Logger LOG = LoggerFactory.getLogger(MpPublicationCostCenterMvcService.class);

    @Autowired
    private IListBusService<PublicationCostCenter<MpPublicationCostCenter>> busService;

    @RequestMapping(value = "mppublication/{publicationId}/costcenter/{id}", method = RequestMethod.DELETE, produces="application/json")
    @ResponseView(IMpView.class)
    @Transactional
    public @ResponseBody ValidationResults deleteMpPublicationCostCenter(@PathVariable String publicationId, @PathVariable String id,
            HttpServletResponse response) {
        LOG.debug("deleteMpPublicationCostCenter");
        setHeaders(response);
        MpPublicationCostCenter pubCostCenter = new MpPublicationCostCenter();
        pubCostCenter.setPublicationId(publicationId);
        CostCenter costCenter = new CostCenter();
        costCenter.setId(id);
        pubCostCenter.setCostCenter(costCenter);
        return busService.deleteObject(pubCostCenter);
    }

}
