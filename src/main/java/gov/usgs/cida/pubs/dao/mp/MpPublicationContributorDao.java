package gov.usgs.cida.pubs.dao.mp;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class MpPublicationContributorDao extends MpDao<MpPublicationContributor> {

    private static final String NS = "mpPublicationContributor";

   /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#add(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public Integer add(MpPublicationContributor domainObject) {
        getSqlSession().insert(NS + ADD, domainObject);
        return domainObject.getId();
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public MpPublicationContributor getById(Integer domainID) {
        return (MpPublicationContributor) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public MpPublicationContributor getById(String domainID) {
        return getById(Integer.parseInt(domainID));
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(java.util.Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<MpPublicationContributor> getByMap(Map<String, Object> filters) {
        return getSqlSession().selectList(NS + GET_BY_MAP, filters);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#update(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void update(MpPublicationContributor domainObject) {
        getSqlSession().update(NS + UPDATE, domainObject);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#delete(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void delete(MpPublicationContributor domainObject) {
        deleteById(domainObject.getId());
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#deleteById(java.lang.Integer)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void deleteById(Integer domainID) {
        getSqlSession().delete(NS + DELETE, domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#deleteByParent(java.lang.Integer)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void deleteByParent(Integer domainID) {
        getSqlSession().delete(NS + DELETE_BY_PARENT, domainID);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IMpDao#copyFromPw(java.lang.Integer)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void copyFromPw(Integer prodID) {
        getSqlSession().insert(NS + COPY_FROM_PW, prodID);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IMpDao#publishToPw(java.lang.Integer)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void publishToPw(Integer prodID) {
        getSqlSession().update(NS + PUBLISH, prodID);
    }

}
