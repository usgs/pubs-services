package gov.usgs.cida.pubs.webservice.mp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.webservice.MvcService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping(value = "lists", produces=PubsConstantsHelper.MEDIA_TYPE_APPLICATION_JSON_UTF8_VALUE)
@ResponseBody 
public class MpListMvcService extends MvcService<MpList> {

	private final IBusService<MpList> busService;

	@Autowired
	public MpListMvcService(@Qualifier("mpListBusService") final IBusService<MpList> busService) {
		this.busService = busService;
	}

	@ApiOperation(value = "", authorizations = { @Authorization(value=PubsConstantsHelper.API_KEY_NAME) })
	@GetMapping
	public Collection<MpList> getLists(HttpServletResponse response) {
		setHeaders(response);
		Map<String, Object> filters = new HashMap<String, Object>();
		return busService.getObjects(filters);
	}
}
