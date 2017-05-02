package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.intfc.IPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PublicationDao extends BaseDao<Publication<?>> implements IPublicationDao {

	@Autowired
	public PublicationDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String NS = "publication";
	private static final String FILTER_BY_INDEX_ID = ".filterLookupByIndexId";
	private static final String VALIDATE_BY_MAP = ".validateByMap";

	public static final String SERIES_ID_SEARCH = "publicationSeriesId";

	public static final String PUB_ABSTRACT = "abstract";
	public static final String CONTRIBUTING_OFFICE = "contributingOffice";
	public static final String CONTRIBUTOR = "contributor";
	public static final String ORCID = PersonContributorDao.ORCID;
	public static final String DOI = "doi";
	public static final String END_YEAR = "endYear";
	public static final String INDEX_ID = "indexId";
	public static final String IPDS_ID = "ipdsId";
	public static final String LINK_TYPE = "linkType";
	public static final String ORDER_BY = "orderBy";
	public static final String PROD_ID = "prodId";
	public static final String Q = "q";
	public static final String REPORT_NUMBER = "reportNumber";
	public static final String SERIES_NAME = "seriesName";
	public static final String START_YEAR = "startYear";
	public static final String SUBTYPE_NAME = "subtypeName";
	public static final String SUBTYPE_ID = "subtypeId";
	public static final String TITLE = "title";
	public static final String TYPE_NAME = "typeName";
	public static final String YEAR = "year";

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public Publication<?> getById(Integer domainID) {
		return getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public List<Publication<?>> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public List<Publication<?>> validateByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + VALIDATE_BY_MAP, filters);
	}

	@Override
	@Transactional(readOnly = true)
	@ISetDbContext
	public Integer getObjectCount(Map<String, Object> filters) {
		return getSqlSession().selectOne(NS + GET_COUNT, filters);
	}

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public List<Publication<?>> filterByIndexId(String indexId) {
		return  getSqlSession().selectList(NS + FILTER_BY_INDEX_ID, indexId);
	}
}