package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.Contributor;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CorporateContributorDao extends ContributorDao {

	@Autowired
	public CorporateContributorDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String CORPORATE = "CorporateContributor";

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.BaseDao#add(java.lang.Object)
	 */
	@Transactional
	@ISetDbContext
	@Override
	public Integer add(Contributor<?> domainObject) {
		getSqlSession().insert(NS + ADD + CORPORATE, domainObject);
		return domainObject.getId();
	}

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public Contributor<?> getById(Integer domainID) {
		return (Contributor<?>) getSqlSession().selectOne(NS + GET_BY_ID + CORPORATE, domainID);
	}

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(Map)
	 */
	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public List<Contributor<?>> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP + CORPORATE, filters);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#update(java.lang.Object)
	 */
	@Transactional
	@ISetDbContext
	@Override
	public void update(Contributor<?> domainObject) {
		getSqlSession().insert(NS + UPDATE + CORPORATE, domainObject);
	}

}
