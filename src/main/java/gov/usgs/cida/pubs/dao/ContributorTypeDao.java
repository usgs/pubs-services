package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.ContributorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class ContributorTypeDao extends BaseDao<ContributorType> {

    private static final String NS = "contributorType";

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public ContributorType getById(Integer domainID) {
        return (ContributorType) getSqlSession().selectOne(NS + ".getById", domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public ContributorType getById(String domainID) {
        return getById(Integer.parseInt(domainID));
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<ContributorType> getByMap(Map<String, Object> filters) {
        return getSqlSession().selectList(NS + ".getByMap", filters);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<ContributorType> getAll() {
        return getByMap(new HashMap<String, Object>());
    }

}
