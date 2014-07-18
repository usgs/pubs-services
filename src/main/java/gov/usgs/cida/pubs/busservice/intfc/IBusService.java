package gov.usgs.cida.pubs.busservice.intfc;


import gov.usgs.cida.pubs.validation.ValidationResults;

import java.util.List;
import java.util.Map;

/**
 * @author drsteini
 *
 */
public interface IBusService <D> {

    /**
     * Given the object's id, return it.
     * 
     * @param objectId
     * @return the specified object
     */
    D getObject(final Integer objectId);

    /**
     * Returns a list of all objects in the system.
     * 
     * @return a list of all objects
     */
    List<D> getObjects();

    /**
     * Returns a list of all objects in the system filtered by the parameters.
     *
     * @param filter
     * @return a list of all matching objects
     */
    List<D> getObjects(final Map<String, Object> filters);

    /**
     * Returns a count of all objects in the system filtered by the parameters.
     *
     * @param filter
     * @return a count of all matching objects
     */
    Integer getObjectCount(final Map<String, Object> filters);

    /**
     * Create an object.
     * 
     * @param object to create
     * @return The created object.
     */
    D createObject(final D object);

    /**
     * Update an object.
     * 
     * @param object to update
     * @return The updated object.
     */
    D updateObject(final D object);

    /**
     * Delete an object
     * 
     * @param object to delete
     * @return TODO
     */
    ValidationResults deleteObject(final D object);

}
