package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.Publication;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class PublicationDao extends BaseDao<Publication<?>> {

    private static final String NS = "publication";

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.core.dao.intfc.IDao#getById(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public Publication<?> getById(Integer domainID) {
		return getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}
	
	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.core.dao.intfc.IDao#getByMap(java.util.Map)
	 */
	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public List<Publication<?>> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.core.dao.intfc.IDao#getObjectCount(java.util.Map)
	 */
	@Override
	@Transactional(readOnly = true)
	@ISetDbContext
	public Integer getObjectCount(Map<String, Object> filters) {
		return getSqlSession().selectOne(NS + GET_COUNT, filters);
	}

}
