package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.busservice.intfc.IMpListPublicationBusService;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.utility.PubsUtilities;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.ValidatorResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
		ValidationResults rtn = new ValidationResults();
		if (null != listId && null != publicationId) {
			Map<String, Object> filters = new HashMap<>();
			filters.put("mpListId", listId);
			filters.put("publicationId", publicationId);
			for (MpListPublication mpListPublication : MpListPublication.getDao().getByMap(filters)) {
				MpListPublication.getDao().deleteById(mpListPublication.getId());
			}
		} else {
			if (null == listId) {
				rtn.addValidatorResult(new ValidatorResult("listID", "Must be provided", SeverityLevel.FATAL, null));
			}
			if (null == publicationId) {
				rtn.addValidatorResult(new ValidatorResult("publicationId", "Must be provided", SeverityLevel.FATAL, null));
			}
		}
		return rtn;
	}

}
