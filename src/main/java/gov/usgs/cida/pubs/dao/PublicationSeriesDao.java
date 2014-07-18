package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.PublicationSeries;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author drsteini
 *
 */
public class PublicationSeriesDao extends BaseDao<PublicationSeries> {

    private static final String NS = "publicationSeries";

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getByMap(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public List<PublicationSeries> getByMap(Map<String, Object> filters) {
        return getSqlSession().selectList(NS + ".getByMap", filters);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public PublicationSeries getById(Integer domainID) {
        return (PublicationSeries) getSqlSession().selectOne(NS + ".getById", domainID);
    }

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public PublicationSeries getById(String domainID) {
        return getById(Integer.parseInt(domainID));
    }


}
