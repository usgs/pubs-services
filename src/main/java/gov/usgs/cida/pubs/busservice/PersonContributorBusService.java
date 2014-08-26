package gov.usgs.cida.pubs.busservice;

import javax.validation.Validator;

import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class PersonContributorBusService extends BusService<PersonContributor<?>> {

	@Autowired
	PersonContributorBusService(final Validator validator) {
		this.validator = validator;
	}

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.busservice.intfc.IBusService#createObject(java.lang.Object)
     */
    @Override
    @Transactional
    public PersonContributor<?> createObject(PersonContributor<?> object) {
        if (null != object) {
            if (object instanceof OutsideContributor) {
                Contributor<PersonContributor<OutsideContributor>> oc = (OutsideContributor) object;
                oc.setValidationErrors(validator.validate(oc));
            } else {
                Contributor<PersonContributor<UsgsContributor>> uc = (UsgsContributor) object;
                uc.setValidationErrors(validator.validate(uc));
            }
            if (object.getValidationErrors().isEmpty()) {
                Integer id = PersonContributor.getDao().add(object);
                object = (PersonContributor<?>) PersonContributor.getDao().getById(id);
            }
        }
        return object;
    }

}
