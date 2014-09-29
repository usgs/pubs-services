package gov.usgs.cida.pubs.busservice.mp;

import gov.usgs.cida.pubs.busservice.BusService;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.validation.ValidationResults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MpListPublicationBusService extends BusService<MpListPublication> {

    @Autowired
    MpListPublicationBusService(final Validator validator) {
    	this.validator = validator;
    }

    @Override
	@Transactional
	public MpListPublication createObject(MpListPublication object) {
//TODO		beginPublicationEdit(object.getMpPublication().getId());
		
		MpListPublication rtnListPub = new MpListPublication();
		Set<ConstraintViolation<MpListPublication>> validations = validator.validate(object);
		if (!validations.isEmpty()) {
			rtnListPub.setValidationErrors(validations);
		} else {
			MpListPublication.getDao().add(object);
			rtnListPub = getMe(object);
		}
		return rtnListPub;
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.core.busservice.intfc.IBusService#deleteObject(Integer)
	 */
	@Override
	@Transactional
	public ValidationResults deleteObject(Integer objectId) {
		MpListPublication.getDao().deleteById(objectId);
		return null;
	}
	
	private MpListPublication getMe(final MpListPublication object) {
		//We have a two column primary, so do a dance to get the row back out... 
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("publicationId", object.getMpPublication().getId());
		params.put("listId", object.getMpList().getId());
		List<MpListPublication> listPubs = MpListPublication.getDao().getByMap(params);
		return 1 == listPubs.size() ? listPubs.get(0) : null;
	}

}
