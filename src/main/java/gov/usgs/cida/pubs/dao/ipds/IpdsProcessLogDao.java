package gov.usgs.cida.pubs.dao.ipds;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.ipds.IpdsProcessLog;

@Repository
public class IpdsProcessLogDao extends BaseDao<IpdsProcessLog> {

	@Autowired
	public IpdsProcessLogDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String NS = "ipdsProcessLog";

	@Transactional
	@Override
	public Integer add(IpdsProcessLog domainObject) {
		return insert(NS + ADD, domainObject);
	}

	@Transactional(readOnly=true)
	@Override
	public IpdsProcessLog getById(Integer domainId) {
		return getSqlSession().selectOne(NS + GET_BY_ID, domainId);
	}

}
