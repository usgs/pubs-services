package gov.usgs.cida.pubs.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.dao.intfc.IPersonContributorDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class PersonContributorDao extends ContributorDao implements IPersonContributorDao {

	private static final String PERSON = "PersonContributor";
	private static final String GET_BY_PREFERRED = ".getByPreferred";

	private static final String REMOVE = ".remove";
	private static final String AFFILIATION = "Affiliation";
	private static final String AFFILIATIONS = "Affiliations";

	public static final String AFFILIATION_ID = "affiliationId";
	public static final String CONTRIBUTOR_ID = "contributorId";
	public static final String ORCID = "orcid";
	public static final String GIVEN = "given";
	public static final String EMAIL = "email";
	public static final String FAMILY = "family";
	public static final String PREFERRED = "preferred";
	public static final String USGS = "usgs";

	@Autowired
	public PersonContributorDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional
	@Override
	public Integer add(Contributor<?> domainObject) {
		return insert(NS + ADD + PERSON, domainObject);
	}

	@Transactional(readOnly = true)
	@Override
	public Contributor<?> getById(Integer domainID) {
		return (Contributor<?>) getSqlSession().selectOne(NS + GET_BY_ID + PERSON, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Contributor<?>> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP + PERSON, filters);
	}

	@Transactional
	@Override
	public void update(Contributor<?> domainObject) {
		update(NS + UPDATE + PERSON, domainObject);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getObjectCount(Map<String, Object> filters) {
		return getSqlSession().selectOne(NS + GET_COUNT, filters);
	}

	@Override
	@Transactional
	public void addAffiliation(Integer contributorId, Integer affiliationId) {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(CONTRIBUTOR_ID, contributorId);
		filters.put(AFFILIATION_ID, affiliationId);
		filters.put(INSERT_USERNAME, PubsUtils.getUsername());
		filters.put(UPDATE_USERNAME, PubsUtils.getUsername());

		getSqlSession().insert(NS + ADD + AFFILIATION, filters);
	}

	@Override
	@Transactional
	public void removeAffiliations(Integer contributorId) {
		delete(NS + REMOVE + AFFILIATIONS, contributorId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Contributor<?>> getByPreferred(PersonContributor<?> filter) {
		return getSqlSession().selectList(NS + GET_BY_PREFERRED, filter);
	}
}
