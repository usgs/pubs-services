package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.utility.PubsUtilities;

/**
 * @author drsteini
 *
 */
@Repository
public class PublicationSeriesDao extends BaseDao<PublicationSeries> {

	@Autowired
	public PublicationSeriesDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String NS = "publicationSeries";

	public static final String ACTIVE_SEARCH = "active";
	public static final String SUBTYPE_SEARCH = "publicationSubtypeId";
	public static final String TEXT_SEARCH = "text";
	public static final String UNIQUE_CHECK = ".uniqueCheck";

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getByMap(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<PublicationSeries> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public PublicationSeries getById(Integer domainID) {
		return (PublicationSeries) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
	 */
	@Transactional(readOnly = true)
	@Override
	public PublicationSeries getById(String domainID) {
		return getById(PubsUtilities.parseInteger(domainID));
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#add(java.lang.Object)
	 */
	@Transactional
	@Override
	public Integer add(PublicationSeries domainObject) {
		return insert(NS + ADD, domainObject);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.core.dao.intfc.IDao#getObjectCount(java.util.Map)
	 */
	@Override
	@Transactional(readOnly = true)
	public Integer getObjectCount(Map<String, Object> filters) {
		return getSqlSession().selectOne(NS + GET_COUNT, filters);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#update(java.lang.Object)
	 */
	@Transactional
	@Override
	public void update(PublicationSeries domainObject) {
		update(NS + UPDATE, domainObject);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(PublicationSeries domainObject) {
		deleteById(domainObject.getId());
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#deleteById(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void deleteById(Integer domainID) {
		getSqlSession().delete(NS + DELETE, domainID);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#deleteByParent(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void deleteByParent(Integer domainID) {
		getSqlSession().delete(NS + DELETE_BY_PARENT, domainID);
	}

	@Transactional(readOnly = true)
	public Map<Integer, Map<String, Object>> uniqueCheck(PublicationSeries domainObject) {
		return getSqlSession().selectMap(NS + UNIQUE_CHECK, domainObject, "id");
	}

}
