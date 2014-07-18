package gov.usgs.cida.pubs.dao.mp;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.intfc.IMpDao;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class MpPublicationContributorDao extends BaseDao<MpPublicationContributor>  implements IMpDao<MpPublicationContributor> {

    private static final String NS = "mpPublicationContributor";

   /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#add(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public Integer add(MpPublicationContributor domainObject) {
        getSqlSession().insert(NS + ".add", domainObject);
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
        return (MpPublicationContributor) getSqlSession().selectOne(NS + ".getById", domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(java.util.Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<MpPublicationContributor> getByMap(Map<String, Object> filters) {
        return getSqlSession().selectList(NS + ".getByMap", filters);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#update(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void update(MpPublicationContributor domainObject) {
        getSqlSession().update(NS + ".update", domainObject);
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
        getSqlSession().delete(NS + ".delete", domainID);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IMpDao#copyFromPw(java.lang.Integer)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void copyFromPw(Integer prodID) {
        getSqlSession().insert(NS + ".copyMpFromPw", prodID);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IMpDao#publishToPw(java.lang.Integer)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void publishToPw(Integer prodID) {
        getSqlSession().update(NS + ".publish", prodID);
    }

}
