package gov.usgs.cida.pubs.dao;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.BaseDomain;
import gov.usgs.cida.pubs.utility.PubsUtils;

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

	@Transactional(readOnly = true)
	@Override
	public Integer add(D domainObject) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	@Transactional(readOnly = true)
	@Override
	public D getById(Integer domainID) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	@Transactional(readOnly = true)
	@Override
	public D getById(String domainID) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	@Transactional(readOnly = true)
	@Override
	public List<D> getByMap(Map<String, Object> filters) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	@Transactional(readOnly = true)
	@Override
	public Integer getObjectCount(Map<String, Object> filters) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	@Transactional(readOnly = true)
	@Override
	public void update(D domainObject) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#delete(java.lang.Object)
	 */
	@Transactional(readOnly = true)
	@Override
	public void delete(D domainObject) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	@Transactional(readOnly = true)
	@Override
	public void deleteById(Integer domainID) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	@Transactional(readOnly = true)
	@Override
	public void deleteByParent(Integer parentID) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	@Transactional(readOnly = true)
	@Override
	public Map<Integer, Map<String, Object>> uniqueCheck(D domainObject) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	protected Integer insert(String statement, BaseDomain<D> domainObject) {
		domainObject.setInsertUsername(PubsUtils.getUsername());
		domainObject.setUpdateUsername(PubsUtils.getUsername());
		getSqlSession().insert(statement, domainObject);
		return domainObject.getId();
	}

	protected void insert(String statement, Integer id) {
		Map<String, Object> parameters = buildMap(id, INSERT_USERNAME, UPDATE_USERNAME);
		getSqlSession().insert(statement, parameters);
	}

	protected void update(String statement, BaseDomain<D> domainObject) {
		domainObject.setUpdateUsername(PubsUtils.getUsername());
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
			parameters.put(type, PubsUtils.getUsername());
		}
		return parameters;
	}
}
