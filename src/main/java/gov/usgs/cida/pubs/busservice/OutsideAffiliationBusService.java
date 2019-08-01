package gov.usgs.cida.pubs.busservice;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.OutsideAffiliation;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;

@Service
public class OutsideAffiliationBusService extends BusService<OutsideAffiliation> {

	@Autowired
	OutsideAffiliationBusService(final Validator validator) {
		this.validator = validator;
	}

	@Override
	public OutsideAffiliation getObject(Integer objectId) {
		OutsideAffiliation result = null;
		if (null != objectId) {
			result = OutsideAffiliation.getDao().getById(objectId);
		}
		return result;
	}

	@Override
	public List<OutsideAffiliation> getObjects(Map<String, Object> filters) {
		return OutsideAffiliation.getDao().getByMap(filters);
	}

	@Override
	public Integer getObjectCount(Map<String, Object> filters) {
		return OutsideAffiliation.getDao().getObjectCount(filters);
	}

	@Override
	@Transactional
	public OutsideAffiliation updateObject(OutsideAffiliation object) {
		OutsideAffiliation result = object;
		if (null != object && null != object.getId()) {
			Integer id = object.getId();
			object.setValidationErrors(validator.validate(object));
			if (object.isValid()) {
				OutsideAffiliation.getDao().update(object);
				result = OutsideAffiliation.getDao().getById(id);
			}
		}
		return result;
	}

	@Override
	@Transactional
	public OutsideAffiliation createObject(OutsideAffiliation object) {
		OutsideAffiliation result = object;
		if (null != object) {
			object.setValidationErrors(validator.validate(object));
			if (object.isValid()) {
				Integer id = OutsideAffiliation.getDao().add(object);
				result = OutsideAffiliation.getDao().getById(id);
			}
		}
		return result;
	}

	@Override
	@Transactional
	public ValidationResults deleteObject(Integer objectId) {
		if (null != objectId) {
			OutsideAffiliation outsideAffiliation = OutsideAffiliation.getDao().getById(objectId);
			if (null == outsideAffiliation) {
				outsideAffiliation = new OutsideAffiliation();
			} else {
				//only delete if we found it...
				Set<ConstraintViolation<OutsideAffiliation>> validations = validator.validate(outsideAffiliation, DeleteChecks.class);
				if (!validations.isEmpty()) {
					outsideAffiliation.setValidationErrors(validations);
				} else {
					OutsideAffiliation.getDao().delete(outsideAffiliation);
				}
			}
			return outsideAffiliation.getValidationErrors();
		}
		return null;
	}
}