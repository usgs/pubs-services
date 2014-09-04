package gov.usgs.cida.pubs.dao.mp;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.mp.MpList;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class MpListDao extends BaseDao<MpList> {

	private static final String NS = "mpList";

	@Transactional
	@ISetDbContext
	@Override
	public Integer add(MpList domainObject) {
		getSqlSession().insert(NS + ADD, domainObject);
		return domainObject.getId();
	}

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public MpList getById(Integer domainID) {
		return getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public MpList getById(String domainID) {
        return getById(Integer.parseInt(domainID));
    }

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public List<MpList> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional
	@ISetDbContext
	@Override
	public void update(MpList domainObject) {
		getSqlSession().update(NS + UPDATE, domainObject);
	}

	@Transactional
	@ISetDbContext
	@Override
	public void delete(MpList domainObject) {
		deleteById(domainObject.getId());
	}

	@Transactional
	@ISetDbContext
	@Override
	public void deleteById(Integer domainID) {
		getSqlSession().delete(NS + DELETE, domainID);
	}
}
