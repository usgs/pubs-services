package gov.usgs.cida.pubs.dao.mp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.dao.intfc.IMpPublicationDao;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class MpPublicationDao extends MpDao<MpPublication> implements IMpPublicationDao {

	private static final String NS = "mpPublication";
	public static final String GET_NEW_ID = ".getNewProdId";
	public static final String LOCK_PUB = ".lockPub";
	public static final String RELEASE_LOCKS = ".releaseLocks";
	public static final String RELEASE_LOCKS_USER = RELEASE_LOCKS + "User";
	public static final String RELEASE_LOCKS_PUB = RELEASE_LOCKS + "Pub";
	public static final String GET_BY_INDEX_ID = ".getByIndexId";

	public static final String GLOBAL = "global";
	public static final String LIST_ID = "listId";
	public static final String SEARCH_TERMS = "searchTerms";

	@Autowired
	public MpPublicationDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional
	@Override
	public Integer add(MpPublication domainObject) {
		return insert(NS + ADD, domainObject);
	}

	@Transactional(readOnly = true)
	@Override
	public MpPublication getById(Integer domainID) {
		return (MpPublication) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MpPublication> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getObjectCount(Map<String, Object> filters) {
		return getSqlSession().selectOne(NS + GET_COUNT, filters);
	}

	@Transactional
	@Override
	public void update(MpPublication domainObject) {
		update(NS + UPDATE, domainObject);
	}

	@Transactional
	@Override
	public void delete(MpPublication domainObject) {
		deleteById(domainObject.getId());
	}

	@Transactional
	@Override
	public void deleteById(Integer domainID) {
		delete(NS + DELETE, domainID);
	}

	@Transactional
	@Override
	public void copyFromPw(Integer domainID) {
		insert(NS + COPY_FROM_PW, domainID);
	}

	@Transactional
	@Override
	public void publishToPw(Integer domainID) {
		update(NS + PUBLISH, domainID);
	}

	@Transactional
	@Override
	public Integer getNewProdId() {
		return getSqlSession().selectOne(NS + GET_NEW_ID);
	}

	@Transactional
	@Override
	public void lockPub(Integer domainId) {
		Map<String, Object> params = new HashMap<>();
		params.put("lockUsername", PubsUtils.getUsername());
		params.put("updateUsername", PubsUtils.getUsername());
		params.put("publicationId", domainId);
		getSqlSession().update(NS + LOCK_PUB, params);
	}

	@Transactional
	@Override
	public void releaseLocksUser(String lockUsername) {
		Map<String, Object> params = new HashMap<>();
		params.put("lockUsername", lockUsername);
		params.put("updateUsername", PubsUtils.getUsername());
		getSqlSession().update(NS + RELEASE_LOCKS_USER, params);
	}

	@Transactional
	@Override
	public void releaseLocksPub(Integer domainID) {
		getSqlSession().update(NS + RELEASE_LOCKS_PUB, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public MpPublication getByIndexId(String indexID) {
		return (MpPublication) getSqlSession().selectOne(NS + GET_BY_INDEX_ID, indexID);
	}

	@Transactional
	@Override
	public void purgePublication(Integer publicationId) {
		MpListPublication.getDao().deleteByParent(publicationId);
		MpPublicationContributor.getDao().deleteByParent(publicationId);
		MpPublicationCostCenter.getDao().deleteByParent(publicationId);
		MpPublicationLink.getDao().deleteByParent(publicationId);
		deleteById(publicationId);
	}
}
