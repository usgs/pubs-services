package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.dao.intfc.IPublishingServiceCenterDao;
import gov.usgs.cida.pubs.domain.PublishingServiceCenter;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class PublishingServiceCenterDao extends BaseDao<PublishingServiceCenter> implements IPublishingServiceCenterDao {

	private static final String NS = "publishingServiceCenter";
	private static final String GET_BY_IPDS_ID = ".getByIpdsId";

	@Autowired
	public PublishingServiceCenterDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional(readOnly = true)
	@Override
	public PublishingServiceCenter getById(Integer domainID) {
		return (PublishingServiceCenter) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public PublishingServiceCenter getById(String domainID) {
		return getById(PubsUtils.parseInteger(domainID));
	}

	@Transactional(readOnly = true)
	@Override
	public List<PublishingServiceCenter> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional(readOnly = true)
	@Override
	public PublishingServiceCenter getByIpdsId(Integer ipdsId) {
		return getSqlSession().selectOne(NS + GET_BY_IPDS_ID, ipdsId);
	}

}
