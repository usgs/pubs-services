package gov.usgs.cida.pubs.busservice;

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
        throw new RuntimeException("NOT IMPLEMENTED.");
    }

    @Override
    public List<D> getObjects(Map<String, Object> filters) {
        throw new RuntimeException("NOT IMPLEMENTED.");
    }

    @Override
    public Integer getObjectCount(Map<String, Object> filters) {
        throw new RuntimeException("NOT IMPLEMENTED.");
    }

    @Override
    public D createObject(D object) {
        throw new RuntimeException("NOT IMPLEMENTED.");
    }

    @Override
    public D updateObject(D object) {
        throw new RuntimeException("NOT IMPLEMENTED.");
    }

    @Override
    public ValidationResults deleteObject(D object) {
        throw new RuntimeException("NOT IMPLEMENTED.");
    }

}
