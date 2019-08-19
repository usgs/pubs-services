package gov.usgs.cida.pubs.dao.sipp;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.sipp.SippProcessLog;

@Repository
public class SippProcessLogDao extends BaseDao<SippProcessLog> {

	@Autowired
	public SippProcessLogDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String NS = "sippProcessLog";

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public Integer add(SippProcessLog domainObject) {
		return insert(NS + ADD, domainObject);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void update(SippProcessLog domainObject) {
		update(NS + UPDATE, domainObject);
	}

}
