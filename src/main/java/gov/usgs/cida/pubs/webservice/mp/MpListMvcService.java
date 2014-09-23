package gov.usgs.cida.pubs.webservice.mp;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.mp.MpList;
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
public class MpListMvcService extends MvcService<MpList> {

	private final IBusService<MpList> busService;

    @Autowired
    MpListMvcService(@Qualifier("mpListBusService")
    		final IBusService<MpList> busService) {
    	this.busService = busService;
    }

	@RequestMapping(value = "lists", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody Collection<MpList> getLists(HttpServletResponse response) {
		Map<String, Object> filters = new HashMap<String, Object>();
		return busService.getObjects(filters);
	}
	
	@RequestMapping(value = "lists", method = RequestMethod.POST, produces="application/json")
	@Transactional
	public @ResponseBody MpList createList(@RequestBody MpList mpList, HttpServletResponse response) {
		return busService.createObject(mpList);
	}
	
	@RequestMapping(value = "lists/{id}", method = RequestMethod.PUT, produces="application/json")
	@Transactional
	public @ResponseBody MpList updateList(@RequestBody MpList mpList, @PathVariable String id, HttpServletResponse response) {
		return busService.updateObject(mpList);
	}
	
	@RequestMapping(value = "lists/{id}", method = RequestMethod.DELETE, produces="application/json")
	@Transactional
	public @ResponseBody ValidationResults deleteList(@PathVariable String id, HttpServletResponse response) {
		MpList list = new MpList();
		list.setId(id);
		return busService.deleteObject(list);
	}

}
