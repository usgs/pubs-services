package gov.usgs.cida.pubs.dao.ipds;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.ipds.IpdsProcessLog;

import org.springframework.transaction.annotation.Transactional;

public class IpdsProcessLogDao extends BaseDao<IpdsProcessLog> {

    private static final String NS = "ipdsProcessLog";

    @Transactional
    @ISetDbContext
    @Override
    public Integer add(IpdsProcessLog domainObject) {
        getSqlSession().insert(NS + ".add", domainObject);
        return domainObject.getId();
    }

    @Transactional(readOnly=true)
    @ISetDbContext
    @Override
    public IpdsProcessLog getById(Integer domainId) {
        return getSqlSession().selectOne(NS + ".getById", domainId);
    }

}
