package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.Contributor;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class PersonContributorDao extends ContributorDao {

    private static final String PERSON = "PersonContributor";

    /**
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#add(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public Integer add(Contributor<?> domainObject) {
        getSqlSession().insert(NS + ADD + PERSON, domainObject);
        return domainObject.getId();
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public Contributor<?> getById(Integer domainID) {
        return (Contributor<?>) getSqlSession().selectOne(NS + GET_BY_ID + PERSON, domainID);
    }

//    /** 
//     * {@inheritDoc}
//     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
//     */
//    @Transactional(readOnly = true)
//    @ISetDbContext
//    @Override
//    public Contributor<?> getById(String domainID) {
//        return getById(Integer.parseInt(domainID));
//    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<Contributor<?>> getByMap(Map<String, Object> filters) {
        return getSqlSession().selectList(NS + GET_BY_MAP + PERSON, filters);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#update(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void update(Contributor<?> domainObject) {
        getSqlSession().insert(NS + UPDATE + PERSON, domainObject);
    }

}
