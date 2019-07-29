package gov.usgs.cida.pubs.dao.mp;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Repository
public class MpPublicationLinkDao extends MpDao<MpPublicationLink> {

	@Autowired
	public MpPublicationLinkDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String NS = "mpPublicationLink";
	
	public static final String LINK_TYPE_SEARCH = "linkTypeId";
	public static final String PUB_SEARCH = "publicationId";

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.BaseDao#add(java.lang.Object)
	 */
	@Transactional
	@Override
	public Integer add(MpPublicationLink domainObject) {
		return insert(NS + ADD, domainObject);
	}

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.BaseDao#getById(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public MpPublicationLink getById(Integer domainID) {
		return (MpPublicationLink) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
	 */
	@Transactional(readOnly = true)
	@Override
	public MpPublicationLink getById(String domainID) {
		return getById(PubsUtilities.parseInteger(domainID));
	}

	/**
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(java.util.Map)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<MpPublicationLink> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#update(java.lang.Object)
	 */
	@Transactional
	@Override
	public void update(MpPublicationLink domainObject) {
		update(NS + UPDATE, domainObject);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(MpPublicationLink domainObject) {
		deleteById(domainObject.getId());
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#deleteById(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void deleteById(Integer domainID) {
		delete(NS + DELETE, domainID);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#deleteByParent(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void deleteByParent(Integer domainID) {
		delete(NS + DELETE_BY_PARENT, domainID);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IMpDao#copyFromPw(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void copyFromPw(Integer prodID) {
		insert(NS + COPY_FROM_PW, prodID);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IMpDao#publishToPw(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void publishToPw(Integer prodID) {
		delete(NS + PUBLISH_DELETE, prodID);
		insert(NS + PUBLISH, prodID);
	}

}
