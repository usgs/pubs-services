package gov.usgs.cida.pubs.dao.ipds;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.intfc.IIpdsMessageLogDao;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

import java.util.List;
import java.util.Map;

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
        getSqlSession().insert(NS + ADD, domainObject);
        return domainObject.getId();
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.mypubsJMS.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public IpdsMessageLog getById(Integer domainID) {
        return (IpdsMessageLog) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.BaseDao#getByMap(Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<IpdsMessageLog> getByMap(Map<String, Object> filters) {
        return getSqlSession().selectList(NS + GET_BY_MAP);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.intfc.IDao#update(java.lang.Object)
     */
    @Transactional
    @ISetDbContext
    @Override
    public void update(IpdsMessageLog domainObject) {
        getSqlSession().update(NS + UPDATE, domainObject);
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
