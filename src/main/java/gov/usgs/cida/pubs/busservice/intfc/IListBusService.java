package gov.usgs.cida.pubs.busservice.intfc;

import gov.usgs.cida.pubs.validation.ValidationResults;

import java.util.Collection;

public interface IListBusService <D> {

    /**
     *  The merge will process the collection, adding objects without an identifier and updating those with an identifier.
     */
    void merge(Integer parentId, Collection<? super D> collection);

    ValidationResults deleteObject(final D object);

}
