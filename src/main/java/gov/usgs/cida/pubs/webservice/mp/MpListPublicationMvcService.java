package gov.usgs.cida.pubs.webservice.mp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.webservice.MvcService;

@Controller
public class MpListPublicationMvcService extends MvcService<MpListPublication> {

	private final IBusService<MpListPublication> busService;
	
    @Autowired
    MpListPublicationMvcService(@Qualifier("mpListPublicationBusService")
    		final IBusService<MpListPublication> busService) {
    	this.busService = busService;
    }

	@RequestMapping(value = "lists/{listId}/pubs", method = RequestMethod.POST, produces="application/json")
	@Transactional
	public @ResponseBody Collection<MpListPublication> addPubToList(@PathVariable String listId,
			@RequestParam("publicationId") String[] publicationIds, HttpServletResponse response) {
		List<MpListPublication> newlistPubs = new ArrayList<MpListPublication>();
		MpList mpList = new MpList();
		mpList.setId(listId);
		for (String publicationId : publicationIds) {
			MpListPublication listPub = new MpListPublication();
			MpPublication mpPub = new MpPublication();
			mpPub.setId(publicationId);
			listPub.setMpList(mpList);
			listPub.setMpPublication(mpPub);
			MpListPublication newListPub = busService.createObject(listPub);
			newlistPubs.add(newListPub);
		}
		return newlistPubs;
	}
	
	@RequestMapping(value = "lists/{listId}/pubs/delete", method = RequestMethod.POST, produces="application/json")
	@Transactional
	public @ResponseBody ValidationResults removePubsFromList(@PathVariable String listId,
			@RequestParam("publicationId") String[] publicationIds, HttpServletResponse response) {
		MpList mpList = new MpList();
		mpList.setId(listId);
		for (String publicationId : publicationIds) {
			MpListPublication listPub = new MpListPublication();
			MpPublication mpPub = new MpPublication();
			mpPub.setId(publicationId);
			listPub.setMpList(mpList);
			listPub.setMpPublication(mpPub);
			busService.deleteObject(listPub);
		}
		return null;
	}

}
