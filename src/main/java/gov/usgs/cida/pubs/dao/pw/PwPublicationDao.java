package gov.usgs.cida.pubs.dao.pw;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ResultHandler;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 * NOTE: getbyId hits the base table to avoid the VPD - this method should only be used for business logic related to MyPubs
 * NOTE: all of the other methods use the VIEW so that the VPD will not show publications before the displayToPublicDate - they should be 
 *       used when dealing with the warehouse.
 */
public class PwPublicationDao extends BaseDao<PwPublication> implements IPwPublicationDao {

    private static final String NS = "pwPublication";
    private static final String GET_BY_INDEX_ID = ".getByIndexId";
    private static final String GET_BY_IPDS_ID = ".getByIpdsId";

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.BaseDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public PwPublication getById(Integer domainID) {
        return (PwPublication) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
    }

    /**
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(java.util.Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public PwPublication getByIpdsId(String ipdsId) {
        return getSqlSession().selectOne(NS + GET_BY_IPDS_ID, ipdsId);
    }
    
    /**
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(java.util.Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<PwPublication> getByMap(Map<String, Object> filters) {
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

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.intfc.IPwPublicationDao#getByIndexId(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
	@Override
	public PwPublication getByIndexId(String indexId) {
        return (PwPublication) getSqlSession().selectOne(NS + GET_BY_INDEX_ID, indexId);
	}

	@Override
	public void stream(Map<String, Object> filters, ResultHandler<PwPublication> handler) {
		getSqlSession().select(NS + GET_STREAM_BY_MAP, filters, handler);
	}

}
