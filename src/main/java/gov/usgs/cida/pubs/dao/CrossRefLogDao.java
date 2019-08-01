package gov.usgs.cida.pubs.dao;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.dao.intfc.ICrossRefLogDao;
import gov.usgs.cida.pubs.domain.CrossRefLog;

@Repository
public class CrossRefLogDao extends BaseDao<CrossRefLog> implements ICrossRefLogDao {

	private static final String NS = "crossRefLog" ;

	@Autowired
	public CrossRefLogDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional
	@Override
	public Integer add(final CrossRefLog domainObject) {
		return insert(NS + ADD, domainObject);
	}

}
