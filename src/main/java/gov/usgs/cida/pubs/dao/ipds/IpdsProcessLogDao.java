package gov.usgs.cida.pubs.dao.ipds;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.ipds.IpdsProcessLog;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class IpdsProcessLogDao extends BaseDao<IpdsProcessLog> {

	@Autowired
	public IpdsProcessLogDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String NS = "ipdsProcessLog";

	@Transactional
	@ISetDbContext
	@Override
	public Integer add(IpdsProcessLog domainObject) {
		getSqlSession().insert(NS + ADD, domainObject);
		return domainObject.getId();
	}

	@Transactional(readOnly=true)
	@ISetDbContext
	@Override
	public IpdsProcessLog getById(Integer domainId) {
		return getSqlSession().selectOne(NS + GET_BY_ID, domainId);
	}

}
