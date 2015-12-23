package gov.usgs.cida.pubs.dao.mp;


import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.mp.MpListPublication;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
@Repository
public class MpListPublicationDao extends MpDao<MpListPublication> {

	@Autowired
	public MpListPublicationDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String NS = "mpListPublication";

	@Transactional
	@ISetDbContext
	@Override
	public Integer add(MpListPublication domainObject) {
		getSqlSession().insert(NS + ADD, domainObject);
		return null;
	}

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public MpListPublication getById(Integer domainID) {
		return (MpListPublication) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public MpListPublication getById(String domainID) {
        return getById(PubsUtilities.parseInteger(domainID));
    }

    @Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public List<MpListPublication> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional
	@ISetDbContext
	@Override
	public void update(MpListPublication domainObject) {
		getSqlSession().update(NS + UPDATE, domainObject);
	}

	@Transactional
	@ISetDbContext
	@Override
	public void delete(MpListPublication domainObject) {
		deleteById(domainObject.getId());
	}

	@Transactional
	@ISetDbContext
	@Override
	public void deleteById(Integer domainID) {
		getSqlSession().delete(NS + DELETE, domainID);
	}

	@Transactional
	@ISetDbContext
	@Override
	public void deleteByParent(Integer domainID) {
		getSqlSession().delete(NS + DELETE_BY_PARENT, domainID);
	}
}
