package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.CrossRefLog;
import org.springframework.transaction.annotation.Transactional;


public interface ICrossRefLogDao {

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.core.dao.BaseDao#add(java.lang.Object)
	 */
	@Transactional
	@ISetDbContext
	Integer add(final CrossRefLog domainObject);
	
}
