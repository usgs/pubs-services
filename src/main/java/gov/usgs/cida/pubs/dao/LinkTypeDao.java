package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class LinkTypeDao extends BaseDao<LinkType> {

	private static final String NS = "linkType";

	@Autowired
	public LinkTypeDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional(readOnly = true)
	@Override
	public LinkType getById(Integer domainID) {
		return (LinkType) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public LinkType getById(String domainID) {
		return getById(PubsUtils.parseInteger(domainID));
	}

	@Transactional(readOnly = true)
	@Override
	public List<LinkType> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

}
