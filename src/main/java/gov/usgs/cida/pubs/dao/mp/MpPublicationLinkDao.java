package gov.usgs.cida.pubs.dao.mp;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class MpPublicationLinkDao extends MpDao<MpPublicationLink> {

	private static final String NS = "mpPublicationLink";

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.BaseDao#add(java.lang.Object)
	 */
	@Transactional
	@ISetDbContext
	@Override
	public Integer add(MpPublicationLink domainObject) {
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
	public MpPublicationLink getById(Integer domainID) {
		return (MpPublicationLink) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public MpPublicationLink getById(String domainID) {
        return getById(Integer.parseInt(domainID));
    }

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(java.util.Map)
	 */
	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public List<MpPublicationLink> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#update(java.lang.Object)
	 */
	@Transactional
	@ISetDbContext
	@Override
	public void update(MpPublicationLink domainObject) {
		getSqlSession().update(NS + UPDATE, domainObject);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#delete(java.lang.Object)
	 */
	@Transactional
	@ISetDbContext
	@Override
	public void delete(MpPublicationLink domainObject) {
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
