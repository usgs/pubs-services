package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class OutsideAffiliationDao extends AffiliationDao {

    private static final String OUTSIDE_AFFILIATION = "OutsideAffiliation";

    /**
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#add(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public Integer add(Affiliation<?> domainObject) {
        getSqlSession().insert(NS + ADD + OUTSIDE_AFFILIATION, domainObject);
        return domainObject.getId();
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public OutsideAffiliation getById(Integer domainID) {
        return (OutsideAffiliation) getSqlSession().selectOne(NS + GET_BY_ID + OUTSIDE_AFFILIATION, domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public OutsideAffiliation getById(String domainID) {
        return getById(Integer.parseInt(domainID));
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<Affiliation<?>> getByMap(Map<String, Object> filters) {
        return getSqlSession().selectList(NS + GET_BY_MAP + OUTSIDE_AFFILIATION, filters);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#update(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void update(Affiliation<?> domainObject) {
        getSqlSession().insert(NS + UPDATE + OUTSIDE_AFFILIATION, domainObject);
    }

}
