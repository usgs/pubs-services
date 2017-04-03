package gov.usgs.cida.pubs.busservice;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;

@Service
public class CostCenterBusService extends BusService<CostCenter> {

	@Autowired
	CostCenterBusService(final Validator validator) {
		this.validator = validator;
	}

	@Override
	public CostCenter getObject(Integer objectId) {
		CostCenter result = null;
		if (null != objectId) {
			result = CostCenter.getDao().getById(objectId);
		}
		return result;
	}

	@Override
	public List<CostCenter> getObjects(Map<String, Object> filters) {
		return CostCenter.getDao().getByMap(filters);
	}

	@Override
	public Integer getObjectCount(Map<String, Object> filters) {
		return CostCenter.getDao().getObjectCount(filters);
	}

	@Override
	@Transactional
	public CostCenter updateObject(CostCenter object) {
		CostCenter result = object;
		if (null != object && null != object.getId()) {
			Integer id = object.getId();
			object.setValidationErrors(validator.validate(object));
			if (object.isValid()) {
				CostCenter.getDao().update(object);
				result = CostCenter.getDao().getById(id);
			}
		}
		return result;
	}

	@Override
	@Transactional
	public CostCenter createObject(CostCenter object) {
		if (null != object) {
			object.setValidationErrors(validator.validate(object));
			if (object.isValid()) {
				Integer id = CostCenter.getDao().add(object);
				object = CostCenter.getDao().getById(id);
			}
		}
		return object;
	}

	@Override
	@Transactional
	public ValidationResults deleteObject(Integer objectId) {
		if (null != objectId) {
			CostCenter costCenter = CostCenter.getDao().getById(objectId);
			if (null == costCenter) {
				costCenter = new CostCenter();
			} else {
				//only delete if we found it...
				Set<ConstraintViolation<CostCenter>> validations = validator.validate(costCenter, DeleteChecks.class);
				if (!validations.isEmpty()) {
					costCenter.setValidationErrors(validations);
				} else {
					CostCenter.getDao().delete(costCenter);
				}
			}
			return costCenter.getValidationErrors();
		}
		return null;
	}
}