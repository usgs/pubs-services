package gov.usgs.cida.pubs.dao.intfc;

import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.CrossRefLog;


public interface ICrossRefLogDao {

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.core.dao.BaseDao#add(java.lang.Object)
	 */
	@Transactional
	Integer add(final CrossRefLog domainObject);
	
}
