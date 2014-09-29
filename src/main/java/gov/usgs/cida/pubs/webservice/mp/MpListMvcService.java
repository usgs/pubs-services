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
public class MpListMvcService extends MvcService<MpList> {

	private final IBusService<MpList> busService;

    @Autowired
    MpListMvcService(@Qualifier("mpListBusService")
    		final IBusService<MpList> busService) {
    	this.busService = busService;
    }

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody Collection<MpList> getLists(HttpServletResponse response) {
		Map<String, Object> filters = new HashMap<String, Object>();
		return busService.getObjects(filters);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public @ResponseBody MpList createList(@RequestBody MpList mpList, HttpServletResponse response) {
		return busService.createObject(mpList);
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	@Transactional
	public @ResponseBody MpList updateList(@RequestBody MpList mpList, @PathVariable String id, HttpServletResponse response) {
		return busService.updateObject(mpList);
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	@Transactional
	public @ResponseBody ValidationResults deleteList(@PathVariable String id, HttpServletResponse response) {
		return busService.deleteObject(PubsUtilities.parseInteger(id));
	}

}
