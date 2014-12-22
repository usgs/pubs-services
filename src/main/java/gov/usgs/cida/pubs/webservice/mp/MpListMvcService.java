package gov.usgs.cida.pubs.webservice.mp;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.webservice.MvcService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
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
	
}
