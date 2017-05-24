package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.intfc.ICrossRefLogDao;
import gov.usgs.cida.pubs.domain.CrossRefLog;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CrossRefLogDao extends BaseDao<CrossRefLog> implements ICrossRefLogDao {

	@Autowired
	public CrossRefLogDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String NS = "crossRefLog" ;
	
	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.core.dao.BaseDao#add(java.lang.Object)
	 */
	@Transactional
	@ISetDbContext
	@Override
	public Integer add(final CrossRefLog domainObject) {
		return getSqlSession().insert(NS + ADD, domainObject);
	}

}
