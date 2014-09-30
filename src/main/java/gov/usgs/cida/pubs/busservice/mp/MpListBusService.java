package gov.usgs.cida.pubs.busservice.mp;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.busservice.BusService;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;

public class MpListBusService extends BusService<MpList> {

    @Autowired
    MpListBusService(final Validator validator) {
    	this.validator = validator;
    }

    @Override
	public List<MpList> getObjects(final Map<String, Object> filters) {
		List<MpList> lists = MpList.getDao().getByMap(filters);
		return lists;
	}

	@Override
	@Transactional
	public MpList createObject(final MpList object) {
		MpList rtnList = new MpList();
		if (null != object) {
			Set<ConstraintViolation<MpList>> validations = validator.validate(object);
			if (!validations.isEmpty()) {
				rtnList.setValidationErrors(validations);
			} else {
				Integer id = MpList.getDao().add(object);
				rtnList = MpList.getDao().getById(id);
			}
		}
		return rtnList;
	}

	@Override
	@Transactional
	public MpList updateObject(final MpList object) {
		MpList rtnList = new MpList();
		if (null != object) {
			Set<ConstraintViolation<MpList>> validations = validator.validate(object);
			if (!validations.isEmpty()) {
				rtnList.setValidationErrors(validations);
			} else {
				MpList.getDao().update(object);
				rtnList = MpList.getDao().getById(object.getId());
			}
		}
		return rtnList;
	}

	@Override
	@Transactional
	public ValidationResults deleteObject(final Integer objectId) {
		MpList list = MpList.getDao().getById(objectId);
		if (null == list) {
			list = new MpList();
		} else {
			//only delete if we found it...
			Set<ConstraintViolation<MpList>> validations = validator.validate(list, DeleteChecks.class);
			if (!validations.isEmpty()) {
				list.setValidationErrors(validations);
			} else {
				MpList.getDao().delete(list);
			}
		}
		return list.getValidationErrors();
	}

}
