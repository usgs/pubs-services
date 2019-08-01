package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.CostCenter;

@Repository
public class CostCenterDao extends AffiliationDao<CostCenter> {

	private static final String COST_CENTER = "CostCenter";

	@Autowired
	public CostCenterDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional
	@Override
	public Integer add(CostCenter domainObject) {
		return insert(NS + ADD + COST_CENTER, domainObject);
	}

	@Transactional(readOnly = true)
	@Override
	public CostCenter getById(Integer domainID) {
		return getSqlSession().selectOne(NS + GET_BY_ID + COST_CENTER, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public List<CostCenter> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP + COST_CENTER, filters);
	}

	@Transactional
	@Override
	public void update(CostCenter domainObject) {
		update(NS + UPDATE + COST_CENTER, domainObject);
	}
}
