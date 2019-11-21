package gov.usgs.cida.pubs.busservice;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.validation.ValidationResults;

@Service
public class PersonContributorBusService extends BusService<PersonContributor<?>> {

	@Autowired
	PersonContributorBusService(final Validator validator) {
		this.validator = validator;
	}

	@Override
	public PersonContributor<?> getObject(Integer objectId) {
		PersonContributor<?> result = null;
		if (null != objectId) {
			result = (PersonContributor<?>) PersonContributor.getDao().getById(objectId);
		}
		return result;
	}

	@Override
	@Transactional
	public PersonContributor<?> updateObject(PersonContributor<?> object, Class<?>... groups) {
		PersonContributor<?> result = object;
		if (null != object && null != object.getId()) {
			Integer id = object.getId();
			if (object instanceof OutsideContributor) {
				Contributor<PersonContributor<OutsideContributor>> oc = (OutsideContributor) object;
				oc.setValidationErrors(validator.validate(oc, groups));
			} else {
				Contributor<PersonContributor<UsgsContributor>> uc = (UsgsContributor) object;
				uc.setValidationErrors(validator.validate(uc, groups));
			}
			if (object.isValid()) {
				updateAffiliations(id, object);
				PersonContributor.getDao().update(object);
				result = (PersonContributor<?>) PersonContributor.getDao().getById(id);
			}
		}
		return result;
	}

	@Override
	@Transactional
	public PersonContributor<?> createObject(PersonContributor<?> object, Class<?>... groups) {
		PersonContributor<?> result = object;
		ValidationResults validationErrors = new ValidationResults();
		if (null != result) {
			if (result instanceof OutsideContributor) {
				Contributor<PersonContributor<OutsideContributor>> oc = (OutsideContributor) result;
				Set<ConstraintViolation<Contributor<PersonContributor<OutsideContributor>>>> results = validator.validate(oc, groups);
				oc.setValidationErrors(results);
				validationErrors.addValidationResults(oc.getValidationErrors());
			} else {
				Contributor<PersonContributor<UsgsContributor>> uc = (UsgsContributor) result;
				Set<ConstraintViolation<Contributor<PersonContributor<UsgsContributor>>>> results = validator.validate(uc, groups);
				uc.setValidationErrors(results);
				validationErrors.addValidationResults(uc.getValidationErrors());
			}
			if (result.isValid()) {
				Integer id = PersonContributor.getDao().add(result);
				updateAffiliations(id, result);
				result = (PersonContributor<?>) PersonContributor.getDao().getById(id);
			}
		}
		result.addValidationResults(validationErrors);
		return result;
	}

	private void updateAffiliations(Integer contributorId, PersonContributor<?> object) {
		if (null != object) {
			PersonContributor.getDao().removeAffiliations(contributorId);
			if (null != object.getAffiliations()) {
				for (Affiliation<?> affiliation : object.getAffiliations()) {
					if (null != affiliation) {
						PersonContributor.getDao().addAffiliation(contributorId, affiliation.getId());
					}
				}
			}
		}
	}

}
