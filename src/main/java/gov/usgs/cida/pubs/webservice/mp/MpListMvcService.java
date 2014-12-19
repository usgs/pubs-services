package gov.usgs.cida.pubs.webservice.mp;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.webservice.MvcService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "lists", produces="application/json")
@ResponseBody 
public class MpListMvcService extends MvcService<MpList> {

	private final IBusService<MpList> busService;

    @Autowired
	public  MpListMvcService(@Qualifier("mpListBusService")
    		final IBusService<MpList> busService) {
    	this.busService = busService;
    }

	@RequestMapping(method = RequestMethod.GET)
	public Collection<MpList> getLists(HttpServletResponse response) {
		setHeaders(response);
		Map<String, Object> filters = new HashMap<String, Object>();
		return busService.getObjects(filters);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public MpList createList(@RequestBody MpList mpList, HttpServletResponse response) {
		setHeaders(response);
		MpList newList = busService.createObject(mpList);
        if (null != newList && newList.getValidationErrors().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return newList;
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	@Transactional
	public MpList updateList(@RequestBody MpList mpList, @PathVariable String id, HttpServletResponse response) {
		setHeaders(response);
		MpList rtn =  busService.updateObject(mpList);
        if (null != rtn && rtn.getValidationErrors().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return rtn;
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@Transactional
	public ValidationResults deleteList(@PathVariable String id, HttpServletResponse response) {
		setHeaders(response);
		ValidationResults rtn =  busService.deleteObject(PubsUtilities.parseInteger(id));
        if (null != rtn && rtn.isEmpty()) {
        	response.setStatus(HttpServletResponse.SC_OK);
        } else {
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return rtn;
	}

}
