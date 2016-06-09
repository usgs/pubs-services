package gov.usgs.cida.pubs.dao.intfc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author drsteini
 *
 * @param <D> the specific domain object this DAO serves 
 */
public interface IDao<D> {

	/**
	 * Add a domain object.
	 * @param domainObject domain object to add
	 * @return the new domain id
	 */
	 Integer add(D domainObject);

	/** 
	 * Get a domain object by it's unique id.
	 * @param domainID - id of the object to retrieve. 
	 * @return the domain object.
	 */
	D getById(Integer domainID);

	D getById(String domainID);

	/** 
	 * Get domain objects by the given filter.
	 * @param filters - id of the object to retrieve. 
	 * @return the domain object.
	 */
	List<D> getByMap(Map<String, Object> filters);

	/** 
	 * Get a count of domain objects matching the given filter.
	 * @param filters - id of the object to retrieve. 
	 * @return the domain object.
	 */
	Integer getObjectCount(Map<String, Object> filters);

	/** 
	 * Update domain object.
	 * @param domainObject domain object to update.
	 */
	void update(D domainObject);

	/** 
	 * Delete domain object.
	 * @param domainObject domain object to delete.
	 */
	void delete(D domainObject);

	/** 
	 * Delete domain object by id.
	 * @param domainID - id of the object to delete. 
	 */
	void deleteById(Integer domainID);

	/** 
	 * Delete domain object(s) by parent's id.
	 * @param parentID - id of the parent of the object(s) to delete. 
	 */
	void deleteByParent(Integer parentID);

	/** 
	 * Get back all domainObjects that have duplicate values to this one.
	 * @param domainObject domain object to check for duplicates. 
	 */
	Map<BigDecimal, Map<String, Object>> uniqueCheck(D domainObject);

	/** 
	 * Get client ID from the database.  For testing purposes only.
	 * @return the database client ID.
	 */
	String getClientId();

}
