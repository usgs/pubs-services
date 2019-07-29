package gov.usgs.cida.pubs.dao;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.utility.PubsUtilities;

/**
 * @author drsteini
 * @param <D> the specific domain of the object 
 *
 */
public abstract class BaseDao<D> extends SqlSessionDaoSupport implements IDao<D> {

	public static final String ADD = ".add";
	public static final String DELETE = ".delete";
	public static final String DELETE_BY_PARENT = ".deleteByParent";
	public static final String GET_BY_ID = ".getById";
	public static final String GET_BY_MAP = ".getByMap";
	public static final String UPDATE = ".update";
	public static final String GET_COUNT = ".getCount";
	public static final String ID_SEARCH = "id";
	public static final String TEXT_SEARCH = "text";

	public static final String PAGE_ROW_START = "page_row_start";
	public static final String PAGE_SIZE = "page_size";
	public static final String PAGE_NUMBER = "page_number";

	public static final String INSERT_USERNAME = "insertUsername";
	public static final String UPDATE_USERNAME = "updateUsername";
	public static final String DELETE_USERNAME = "deleteUsername";

	public BaseDao(SqlSessionFactory sqlSessionFactory) {
		setSqlSessionFactory(sqlSessionFactory);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#add(java.lang.Object)
	 */
	@Transactional(readOnly = true)
	@Override
	public Integer add(D domainObject) {
		throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public D getById(Integer domainID) {
		throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public D getById(String domainID) {
		throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getByMap(Map)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<D> getByMap(Map<String, Object> filters) {
		throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getObjectCount(Map)
	 */
	@Transactional(readOnly = true)
	@Override
	public Integer getObjectCount(Map<String, Object> filters) {
		throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#update(java.lang.Object)
	 */
	@Transactional(readOnly = true)
	@Override
	public void update(D domainObject) {
		throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#delete(java.lang.Object)
	 */
	@Transactional(readOnly = true)
	@Override
	public void delete(D domainObject) {
		throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#deleteById(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public void deleteById(Integer domainID) {
		throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#deleteByParent(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public void deleteByParent(Integer parentID) {
		throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#uniqueCheck(java.lang.Object)
	 */
	@Transactional(readOnly = true)
	@Override
	public Map<Integer, Map<String, Object>> uniqueCheck(D domainObject) {
		throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
	}

	protected Integer insert(String statement, BaseDomain<D> domainObject) {
		domainObject.setInsertUsername(PubsUtilities.getUsername());
		domainObject.setUpdateUsername(PubsUtilities.getUsername());
		getSqlSession().insert(statement, domainObject);
		return domainObject.getId();
	}

	protected void insert(String statement, Integer id) {
		Map<String, Object> parameters = buildMap(id, INSERT_USERNAME, UPDATE_USERNAME);
		getSqlSession().insert(statement, parameters);
	}

	protected void update(String statement, BaseDomain<D> domainObject) {
		domainObject.setUpdateUsername(PubsUtilities.getUsername());
		getSqlSession().update(statement, domainObject);
	}

	protected void update(String statement, Integer id) {
		Map<String, Object> parameters = buildMap(id, UPDATE_USERNAME);
		getSqlSession().update(statement, parameters);
	}

	protected void delete(String statement, Integer id) {
		Map<String, Object> parameters = buildMap(id, DELETE_USERNAME);
		getSqlSession().delete(statement, parameters);
	}

	protected Map<String, Object> buildMap(Integer id, String... usernameType) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(ID_SEARCH, id);
		for (String type:usernameType) {
			parameters.put(type, PubsUtilities.getUsername());
		}
		return parameters;
	}
}
