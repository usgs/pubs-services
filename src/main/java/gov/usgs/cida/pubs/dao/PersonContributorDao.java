package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.intfc.IPersonContributorDao;
import gov.usgs.cida.pubs.domain.Contributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PersonContributorDao extends ContributorDao implements IPersonContributorDao {

	@Autowired
	public PersonContributorDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String PERSON = "PersonContributor";
	
	private static final String REMOVE = ".remove";
	private static final String AFFILIATION = "Affiliation";
	private static final String AFFILIATIONS = "Affiliations";

	public static final String AFFILIATION_ID = "affiliationId";
	public static final String CONTRIBUTOR_ID = "contributorId";
	public static final String ORCID = "orcid";
	public static final String GIVEN = "given";
	public static final String FAMILY = "family";

	@Transactional
	@ISetDbContext
	@Override
	public Integer add(Contributor<?> domainObject) {
		getSqlSession().insert(NS + ADD + PERSON, domainObject);
		return domainObject.getId();
	}

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public Contributor<?> getById(Integer domainID) {
		return (Contributor<?>) getSqlSession().selectOne(NS + GET_BY_ID + PERSON, domainID);
	}

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public List<Contributor<?>> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP + PERSON, filters);
	}

	@Transactional
	@ISetDbContext
	@Override
	public void update(Contributor<?> domainObject) {
		getSqlSession().insert(NS + UPDATE + PERSON, domainObject);
	}

	@Override
	@Transactional(readOnly = true)
	@ISetDbContext
	public Integer getObjectCount(Map<String, Object> filters) {
		return getSqlSession().selectOne(NS + GET_COUNT, filters);
	}
	
	@Override
	@Transactional
	@ISetDbContext
	public void addAffiliation(Integer contributorId, Integer affiliationId) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(CONTRIBUTOR_ID, contributorId);
		filters.put(AFFILIATION_ID, affiliationId);
		getSqlSession().insert(NS + ADD + AFFILIATION, filters);
	}

	@Override
	@Transactional
	@ISetDbContext
	public void removeAffiliations(Integer contributorId) {
		getSqlSession().delete(NS + REMOVE + AFFILIATIONS, contributorId);
	}
}