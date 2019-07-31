package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.PublicationSeries;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Repository
public class PublicationSeriesDao extends BaseDao<PublicationSeries> {

	private static final String NS = "publicationSeries";

	public static final String ACTIVE_SEARCH = "active";
	public static final String SUBTYPE_SEARCH = "publicationSubtypeId";
	public static final String TEXT_SEARCH = "text";
	public static final String UNIQUE_CHECK = ".uniqueCheck";

	@Autowired
	public PublicationSeriesDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional(readOnly = true)
	@Override
	public List<PublicationSeries> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional(readOnly = true)
	@Override
	public PublicationSeries getById(Integer domainID) {
		return (PublicationSeries) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public PublicationSeries getById(String domainID) {
		return getById(PubsUtilities.parseInteger(domainID));
	}

	@Transactional
	@Override
	public Integer add(PublicationSeries domainObject) {
		return insert(NS + ADD, domainObject);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getObjectCount(Map<String, Object> filters) {
		return getSqlSession().selectOne(NS + GET_COUNT, filters);
	}

	@Transactional
	@Override
	public void update(PublicationSeries domainObject) {
		update(NS + UPDATE, domainObject);
	}

	@Transactional
	@Override
	public void delete(PublicationSeries domainObject) {
		deleteById(domainObject.getId());
	}

	@Transactional
	@Override
	public void deleteById(Integer domainID) {
		delete(NS + DELETE, domainID);
	}

	@Transactional
	@Override
	public void deleteByParent(Integer domainID) {
		delete(NS + DELETE_BY_PARENT, domainID);
	}

	@Transactional(readOnly = true)
	public Map<Integer, Map<String, Object>> uniqueCheck(PublicationSeries domainObject) {
		return getSqlSession().selectMap(NS + UNIQUE_CHECK, domainObject, "id");
	}

}
