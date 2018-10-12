package gov.usgs.cida.pubs.busservice.mp;

import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.ConfigurationService;
import gov.usgs.cida.pubs.busservice.BusService;
import gov.usgs.cida.pubs.dao.mp.MpListDao;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpList.MpListType;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Service
public class MpListBusService extends BusService<MpList> {

	protected ConfigurationService configurationService;

	@Autowired
	MpListBusService(final Validator validator,
			final ConfigurationService configurationService) {
		this.validator = validator;
		this.configurationService = configurationService;
	}

	@Override
	public List<MpList> getObjects(final Map<String, Object> filters) {
		if (PubsUtilities.isSpnOnly(configurationService)) {
			filters.put(MpListDao.LIST_TYPE_SEARCH, MpListType.SPN);
		}
		return MpList.getDao().getByMap(filters);
	}

}
