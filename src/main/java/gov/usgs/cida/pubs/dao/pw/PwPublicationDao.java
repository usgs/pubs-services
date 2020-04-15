package gov.usgs.cida.pubs.dao.pw;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.dao.mp.MpDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationContributorDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationCostCenterDao;
import gov.usgs.cida.pubs.dao.mp.MpPublicationLinkDao;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.query.IFilterParams;

/**
 * NOTE: getbyId shows ALL data - this method should only be used for business logic related to MyPubs
 * NOTE: all of the other methods will not show publications before the displayToPublicDate - they should be 
 *	   used when dealing with the warehouse.
 */
@Repository
public class PwPublicationDao extends BaseDao<PwPublication> implements IPwPublicationDao {

	public static final String NS = "pwPublication";
	private static final String GET_BY_INDEX_ID = ".getByIndexId";
	private static final String GET_BY_IPDS_ID = ".getByIpdsId";
	public static final String GET_CROSSREF_PUBLICATIONS = ".getCrossrefPubs";
	public static final String GET_STREAM_BY_MAP = ".getStreamByMap";
	public static final String GET_RELATED_PUBLICATIONS = ".getRelatedPublications";

	public static final String SUBTYPE_ID = "subtypeId";
	public static final String CHORUS = "chorus";
	public static final String G = "g";
	public static final String MOD_DATE_HIGH = "mod_date_high";
	public static final String MOD_DATE_LOW = "mod_date_low";
	public static final String MOD_X_DAYS = "mod_x_days";
	public static final String PUB_DATE_HIGH = "pub_date_high";
	public static final String PUB_DATE_LOW = "pub_date_low";
	public static final String PUB_X_DAYS = "pub_x_days";

	@Autowired
	public PwPublicationDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional
	public void refreshTextIndex() {
		getSqlSession().update(NS + ".refreshTextIndex");
	}

	@Transactional(readOnly = true)
	@Override
	public PwPublication getById(Integer domainID) {
		return (PwPublication) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public PwPublication getByIpdsId(String ipdsId) {
		return getSqlSession().selectOne(NS + GET_BY_IPDS_ID, ipdsId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<PwPublication> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getObjectCount(Map<String, Object> filters) {
		return getSqlSession().selectOne(NS + GET_COUNT, filters);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getCountByFilter(IFilterParams filters) {
		return getSqlSession().selectOne(NS + GET_COUNT, filters);
	}

	@Transactional(readOnly = true)
	@Override
	public PwPublication getByIndexId(String indexId) {
		return (PwPublication) getSqlSession().selectOne(NS + GET_BY_INDEX_ID, indexId);
	}

	@Override
	public void stream(String statement, IFilterParams filters, ResultHandler<PwPublication> handler) {
		getSqlSession().select(statement, filters, handler);
	}

	@Override
	public List<PwPublication> getCrossrefPublications(Map<String, Object> filters){
		return getSqlSession().selectList(NS + GET_CROSSREF_PUBLICATIONS, filters);
	}

	@Transactional
	@Override
	public void purgePublication(Integer publicationId) {
		delete(MpPublicationContributorDao.NS + MpDao.PUBLISH_DELETE, publicationId);
		delete(MpPublicationCostCenterDao.NS + MpDao.PUBLISH_DELETE, publicationId);
		delete(MpPublicationLinkDao.NS + MpDao.PUBLISH_DELETE, publicationId);
		delete(NS + DELETE, publicationId);
		refreshTextIndex();
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> getRelatedPublications(Integer publicationId) {
		return getSqlSession().selectList(NS + GET_RELATED_PUBLICATIONS, publicationId);
	}

	@Override
	public List<PwPublication> getByFilter(IFilterParams filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}
}
