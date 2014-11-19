package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.intfc.IPublicationIndexDao;
import gov.usgs.cida.pubs.domain.PublicationIndex;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.transaction.annotation.Transactional;

public class PublicationIndexDao extends SqlSessionDaoSupport implements IPublicationIndexDao {

    protected static final String NS = "publicationIndex";

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IPublicationIndexDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public PublicationIndex getById(Integer domainID) {
        return (PublicationIndex) getSqlSession().selectOne(NS + BaseDao.GET_BY_ID, domainID);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IPublicationIndexDao#publish(java.lang.Integer)
     */
    @Transactional
    @Override
    public void publish(Integer publicationId) {
    	getSqlSession().selectOne(NS + BaseDao.UPDATE, publicationId);
    }

}
