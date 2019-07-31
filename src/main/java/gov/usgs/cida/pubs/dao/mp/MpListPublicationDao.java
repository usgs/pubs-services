package gov.usgs.cida.pubs.dao.mp;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class MpListPublicationDao extends MpDao<MpListPublication> {

	private static final String NS = "mpListPublication";

	@Autowired
	public MpListPublicationDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional
	@Override
	public Integer add(MpListPublication domainObject) {
		insert(NS + ADD, domainObject);
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public MpListPublication getById(Integer domainID) {
		return (MpListPublication) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public MpListPublication getById(String domainID) {
		return getById(PubsUtils.parseInteger(domainID));
	}

	@Transactional(readOnly = true)
	@Override
	public List<MpListPublication> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional
	@Override
	public void update(MpListPublication domainObject) {
		update(NS + UPDATE, domainObject);
	}

	@Transactional
	@Override
	public void delete(MpListPublication domainObject) {
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
}
