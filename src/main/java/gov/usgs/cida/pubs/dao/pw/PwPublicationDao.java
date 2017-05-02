package gov.usgs.cida.pubs.dao.pw;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

/**
 * @author drsteini
 * NOTE: getbyId hits the base table to avoid the VPD - this method should only be used for business logic related to MyPubs
 * NOTE: all of the other methods use the VIEW so that the VPD will not show publications before the displayToPublicDate - they should be 
 *	   used when dealing with the warehouse.
 */
@Repository
public class PwPublicationDao extends BaseDao<PwPublication> implements IPwPublicationDao {

	@Autowired
	public PwPublicationDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	public static final String NS = "pwPublication";
	private static final String GET_BY_INDEX_ID = ".getByIndexId";
	private static final String GET_BY_IPDS_ID = ".getByIpdsId";
	public static final String GET_STREAM = ".getStream";
	public static final String GET_STREAM_BY_MAP = ".getStreamByMap";

	public static final String CHORUS = "chorus";
	public static final String G = "g";
	public static final String MOD_DATE_HIGH = "mod_date_high";
	public static final String MOD_DATE_LOW = "mod_date_low";
	public static final String MOD_X_DAYS = "mod_x_days";
	public static final String PUB_DATE_HIGH = "pub_date_high";
	public static final String PUB_DATE_LOW = "pub_date_low";
	public static final String PUB_X_DAYS = "pub_x_days";

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
	public void stream(String statement, Map<String, Object> filters, ResultHandler<PwPublication> handler) {
		getSqlSession().select(statement, filters, handler);
	}

}
