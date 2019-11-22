package gov.usgs.cida.pubs.busservice;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.validation.ValidationResults;
import gov.usgs.cida.pubs.validation.constraint.DeleteChecks;

@Service
public class PublicationSeriesBusService extends BusService<PublicationSeries> {

	@Autowired
	PublicationSeriesBusService(final Validator validator) {
		this.validator = validator;
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#getObject(java.lang.Integer) 
	 */
	@Override
	public PublicationSeries getObject(Integer objectId) {
		PublicationSeries result = null;
		if (null != objectId) {
			result = PublicationSeries.getDao().getById(objectId);
		}
		return result;
	}

	@Override
	public List<PublicationSeries> getObjects(Map<String, Object> filters) {
		return PublicationSeries.getDao().getByMap(filters);
	}

	@Override
	public Integer getObjectCount(Map<String, Object> filters) {
		return PublicationSeries.getDao().getObjectCount(filters);
	}

	@Override
	@Transactional
	public PublicationSeries updateObject(PublicationSeries object, Class<?>... groups) {
		PublicationSeries result = object;
		if (null != object && null != object.getId()) {
			Integer id = object.getId();
			object.setValidationErrors(validator.validate(object));
			if (object.isValid()) {
				PublicationSeries.getDao().update(object);
				result = PublicationSeries.getDao().getById(id);
			}
		}
		return result;
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#createObject(java.lang.Object)
	 */
	@Override
	@Transactional
	public PublicationSeries createObject(PublicationSeries object, Class<?>... groups) {
		PublicationSeries result = object;
		if (null != object) {
			object.setValidationErrors(validator.validate(object));
			if (object.isValid()) {
				Integer id = PublicationSeries.getDao().add(object);
				result = PublicationSeries.getDao().getById(id);
			}
		}
		return result;
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#deleteObject(Integer)
	 */
	@Override
	@Transactional
	public ValidationResults deleteObject(Integer objectId, Class<?>... groups) {
		if (null != objectId) {
			PublicationSeries publicationSeries = PublicationSeries.getDao().getById(objectId);
			if (null == publicationSeries) {
				publicationSeries = new PublicationSeries();
			} else {
				//only delete if we found it...
				Set<ConstraintViolation<PublicationSeries>> validations = validator.validate(publicationSeries, DeleteChecks.class);
				if (!validations.isEmpty()) {
					publicationSeries.setValidationErrors(validations);
				} else {
					PublicationSeries.getDao().delete(publicationSeries);
				}
			}
			return publicationSeries.getValidationErrors();
		}
		return null;
	}

}
