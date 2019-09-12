package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class ContributorDao extends BaseDao<Contributor<?>> {

	protected static final String NS = "contributor";

	@Autowired
	public ContributorDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional(readOnly = true)
	@Override
	public Contributor<?> getById(Integer domainID) {
		return (Contributor<?>) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public Contributor<?> getById(String domainID) {
		return getById(PubsUtils.parseInteger(domainID));
	}

	@Transactional(readOnly = true)
	@Override
	public List<Contributor<?>> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional
	@Override
	public void delete(Contributor<?> domainObject) {
		deleteById(domainObject.getId());
	}

	@Transactional
	@Override
	public void deleteById(Integer domainID) {
		delete(NS + DELETE, domainID);
	}

	protected Integer insert(String statement, Contributor<?> domainObject) {
		domainObject.setInsertUsername(PubsUtils.getUsername());
		domainObject.setUpdateUsername(PubsUtils.getUsername());
		getSqlSession().insert(statement, domainObject);
		return domainObject.getId();
	}

	protected void update(String statement, Contributor<?> domainObject) {
		domainObject.setInsertUsername(PubsUtils.getUsername());
		domainObject.setUpdateUsername(PubsUtils.getUsername());
		getSqlSession().update(statement, domainObject);
	}
}
