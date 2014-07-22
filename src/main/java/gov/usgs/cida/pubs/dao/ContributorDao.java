package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.Contributor;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class ContributorDao extends BaseDao<Contributor> {

    private static final String NS = "contributor";

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public Contributor getById(Integer domainID) {
        return (Contributor) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public Contributor getById(String domainID) {
        return getById(Integer.parseInt(domainID));
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<Contributor> getByMap(Map<String, Object> filters) {
        return getSqlSession().selectList(NS + GET_BY_MAP, filters);
    }

}
