package gov.usgs.cida.pubs.busservice;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.validation.ValidationResults;

import java.util.List;
import java.util.Map;

import javax.validation.Validator;

/**
 * @author drsteini
 *
 */
public abstract class BusService<D> implements IBusService<D> {

    protected Validator validator;

    @Override
    public D getObject(Integer objectId) {
        throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
    }

    @Override
    public List<D> getObjects(Map<String, Object> filters) {
        throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
    }

    @Override
    public Integer getObjectCount(Map<String, Object> filters) {
        throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
    }

    @Override
    public D createObject(D object) {
        throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
    }

    @Override
    public D updateObject(D object) {
        throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
    }

    @Override
    public ValidationResults deleteObject(Integer objectId) {
        throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
    }

}
