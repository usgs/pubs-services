package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.busservice.BusService;
import gov.usgs.cida.pubs.dao.mp.MpListDao;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpList.MpListType;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;

public class MpListBusService extends BusService<MpList> {

    @Autowired
    MpListBusService(final Validator validator) {
    	this.validator = validator;
    }

    @Override
	public List<MpList> getObjects(final Map<String, Object> filters) {
    	if (PubsUtilities.isSpnOnly()) {
    		filters.put(MpListDao.LIST_TYPE_SEARCH, MpListType.SPN);
    	}
		return MpList.getDao().getByMap(filters);
	}

}
