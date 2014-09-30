package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.busservice.intfc.IMpListPublicationBusService;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MpListPublicationBusService extends MpBusService<MpListPublication> implements IMpListPublicationBusService {

    @Autowired
    MpListPublicationBusService(final Validator validator) {
    	this.validator = validator;
    }

	@Override
	@Transactional
	public Collection<MpListPublication> addPubToList(Integer listId, String[] publicationIds) {
		List<MpListPublication> newlistPubs = new ArrayList<MpListPublication>();
		if (null != listId && null != publicationIds && 0 < publicationIds.length) {
			MpList mpList = new MpList();
			mpList.setId(listId);
			for (String publicationId : publicationIds) {
				beginPublicationEdit(PubsUtilities.parseInteger(publicationId));
				MpListPublication listPub = new MpListPublication();
				MpPublication mpPub = new MpPublication();
				mpPub.setId(publicationId);
				listPub.setMpList(mpList);
				listPub.setMpPublication(mpPub);
				Set<ConstraintViolation<MpListPublication>> validations = validator.validate(listPub);
				if (!validations.isEmpty()) {
					listPub.setValidationErrors(validations);
				} else {
					MpListPublication.getDao().add(listPub);
				}
				newlistPubs.add(listPub);
			}
		}
		return newlistPubs;
	}

	@Override
	@Transactional
	public ValidationResults removePubFromList(Integer listId, Integer publicationId) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("listId", listId);
        filters.put("publicationId", publicationId);
		for (MpListPublication mpListPublication : MpListPublication.getDao().getByMap(filters)) {
			MpListPublication.getDao().deleteById(mpListPublication.getId());
		}
		return new ValidationResults();
	}

}
