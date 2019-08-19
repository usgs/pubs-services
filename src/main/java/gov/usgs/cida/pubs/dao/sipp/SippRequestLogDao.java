package gov.usgs.cida.pubs.dao.sipp;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.sipp.SippRequestLog;

@Repository
public class SippRequestLogDao extends BaseDao<SippRequestLog> {

	@Autowired
	public SippRequestLogDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String NS = "sippRequestLog";

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public Integer add(SippRequestLog domainObject) {
		return insert(NS + ADD, domainObject);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void update(SippRequestLog domainObject) {
		update(NS + UPDATE, domainObject);
	}
}
