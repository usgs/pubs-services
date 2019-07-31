package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class PublicationSubtypeDao extends BaseDao<PublicationSubtype> {

	private static final String NS = "publicationSubtype";

	@Autowired
	public PublicationSubtypeDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional(readOnly = true)
	@Override
	public PublicationSubtype getById(Integer domainID) {
		return (PublicationSubtype) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public PublicationSubtype getById(String domainID) {
		return getById(PubsUtils.parseInteger(domainID));
	}

	@Transactional(readOnly = true)
	@Override
	public List<PublicationSubtype> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

}
