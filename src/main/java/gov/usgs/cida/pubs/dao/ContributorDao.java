package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Repository
public class ContributorDao extends BaseDao<Contributor<?>> {

	@Autowired
	public ContributorDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	protected static final String NS = "contributor";

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public Contributor<?> getById(Integer domainID) {
		return (Contributor<?>) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
	 */
	@Transactional(readOnly = true)
	@Override
	public Contributor<?> getById(String domainID) {
		return getById(PubsUtilities.parseInteger(domainID));
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(Map)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<Contributor<?>> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(Contributor<?> domainObject) {
		deleteById(domainObject.getId());
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#deleteById(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void deleteById(Integer domainID) {
		delete(NS + DELETE, domainID);
	}

	protected Integer insert(String statement, Contributor<?> domainObject) {
		domainObject.setInsertUsername(PubsUtilities.getUsername());
		domainObject.setUpdateUsername(PubsUtilities.getUsername());
		getSqlSession().insert(statement, domainObject);
		return domainObject.getId();
	}

	protected void update(String statement, Contributor<?> domainObject) {
		domainObject.setInsertUsername(PubsUtilities.getUsername());
		domainObject.setUpdateUsername(PubsUtilities.getUsername());
		getSqlSession().update(statement, domainObject);
	}
}
