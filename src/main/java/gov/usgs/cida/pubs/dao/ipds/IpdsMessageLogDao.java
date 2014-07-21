package gov.usgs.cida.pubs.dao.ipds;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.intfc.IIpdsMessageLogDao;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class IpdsMessageLogDao extends BaseDao <IpdsMessageLog> implements IIpdsMessageLogDao {

    private static final String NS = "ipdsMessageLog";

    /** {@inheritDoc}
     * @see gov.usgs.cida.mypubsJMS.dao.intfc.IDao#add(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public Integer add(IpdsMessageLog domainObject) {
        getSqlSession().insert(NS + ".add", domainObject);
        return domainObject.getId();
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.mypubsJMS.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public IpdsMessageLog getById(Integer domainID) {
        return (IpdsMessageLog) getSqlSession().selectOne(NS + ".getById", domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.BaseDao#getAll()
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<IpdsMessageLog> getAll() {
        return getSqlSession().selectList(NS + ".getAll");
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.intfc.IDao#update(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void update(IpdsMessageLog domainObject) {
        getSqlSession().update(NS + ".update", domainObject);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.dao.intfc.IIpdsMessageLogDao#getFromIpds(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<MpPublication> getFromIpds(Integer ipdsMessageLogId) {
        return getSqlSession().selectList(NS + ".getMpPublicationFromIpds", ipdsMessageLogId);
    }

}
