package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class LinkFileTypeDao extends BaseDao<LinkFileType> {

	private static final String NS = "linkFileType";

	@Autowired
	public LinkFileTypeDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional(readOnly = true)
	@Override
	public LinkFileType getById(Integer domainID) {
		return (LinkFileType) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public LinkFileType getById(String domainID) {
		return getById(PubsUtils.parseInteger(domainID));
	}

	@Transactional(readOnly = true)
	@Override
	public List<LinkFileType> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

}
