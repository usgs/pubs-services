package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.CrossRefLog;

import org.springframework.transaction.annotation.Transactional;

public class CrossRefLogDao extends BaseDao<CrossRefLog> {

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
