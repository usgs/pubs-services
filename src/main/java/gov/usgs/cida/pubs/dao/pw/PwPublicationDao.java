package gov.usgs.cida.pubs.dao.pw;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.pw.PwPublication;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class PwPublicationDao extends BaseDao<PwPublication> {

    private static final String NS = "pwPublication";

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.BaseDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public PwPublication getById(Integer domainID) {
        return (PwPublication) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
    }

}
