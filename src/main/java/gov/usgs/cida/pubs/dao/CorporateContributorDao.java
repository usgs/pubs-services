package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.Contributor;

@Repository
public class CorporateContributorDao extends ContributorDao {

	private static final String CORPORATE = "CorporateContributor";

	@Autowired
	public CorporateContributorDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional
	@Override
	public Integer add(Contributor<?> domainObject) {
		return insert(NS + ADD + CORPORATE, domainObject);
	}

	@Transactional(readOnly = true)
	@Override
	public Contributor<?> getById(Integer domainID) {
		return (Contributor<?>) getSqlSession().selectOne(NS + GET_BY_ID + CORPORATE, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Contributor<?>> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP + CORPORATE, filters);
	}

	@Transactional
	@Override
	public void update(Contributor<?> domainObject) {
		update(NS + UPDATE + CORPORATE, domainObject);
	}

}
