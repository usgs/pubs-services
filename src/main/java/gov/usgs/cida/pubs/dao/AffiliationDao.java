package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class AffiliationDao<D extends Affiliation<D>> extends BaseDao<D> {

	protected static final String NS = "affiliation";
	public static final String ACTIVE_SEARCH = "active";
	public static final String USGS_SEARCH = "usgs";
	public static final String EXACT_SEARCH = "exactText";

	@Autowired
	public AffiliationDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional(readOnly = true)
	@Override
	public D getById(Integer domainID) {
		return getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public D getById(String domainID) {
		return getById(PubsUtils.parseInteger(domainID));
	}

	@Transactional(readOnly = true)
	@Override
	public List<D> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional
	@Override
	public void delete(D domainObject) {
		deleteById(domainObject.getId());
	}

	@Transactional
	@Override
	public void deleteById(Integer domainID) {
		delete(NS + DELETE, domainID);
	}
}
