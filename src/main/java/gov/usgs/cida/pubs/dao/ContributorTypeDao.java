package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Repository
public class ContributorTypeDao extends BaseDao<ContributorType> {

	private static final String NS = "contributorType";

	@Autowired
	public ContributorTypeDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional(readOnly = true)
	@Override
	public ContributorType getById(Integer domainID) {
		return (ContributorType) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public ContributorType getById(String domainID) {
		return getById(PubsUtilities.parseInteger(domainID));
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContributorType> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

}
