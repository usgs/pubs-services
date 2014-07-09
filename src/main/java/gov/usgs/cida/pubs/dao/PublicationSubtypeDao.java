package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.PublicationSubtype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class PublicationSubtypeDao extends BaseDao<PublicationSubtype> {

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public PublicationSubtype getById(Integer domainID) {
        return (PublicationSubtype) getSqlSession().selectOne("publicationSubtype.getById", domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public PublicationSubtype getById(String domainID) {
        return getById(Integer.parseInt(domainID));
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.BaseDao#getByMap(Map)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<PublicationSubtype> getByMap(Map<String, Object> filters) {
        List<PublicationSubtype> types = getSqlSession().selectList("publicationSubtype.getByMap", filters);
        return types;
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.core.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<PublicationSubtype> getAll() {
        return getByMap(new HashMap<String, Object>());
    }

}
