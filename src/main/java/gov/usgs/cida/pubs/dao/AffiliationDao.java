package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.Affiliation;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class AffiliationDao extends BaseDao<Affiliation<?>> {

    protected static final String NS = "affiliation";

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public Affiliation<?> getById(Integer domainID) {
        return (Affiliation<?>) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public Affiliation<?> getById(String domainID) {
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
        return getSqlSession().selectList(NS + GET_BY_MAP, filters);
    }

}
