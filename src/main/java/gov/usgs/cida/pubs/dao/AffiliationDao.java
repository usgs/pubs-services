package gov.usgs.cida.pubs.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.utility.PubsUtilities;

@Repository
public class AffiliationDao<D extends Affiliation<D>> extends BaseDao<D> {

	@Autowired
	public AffiliationDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	protected static final String NS = "affiliation";
	public static final String ACTIVE_SEARCH = "active";
	public static final String USGS_SEARCH = "usgs";
	public static final String EXACT_SEARCH = "exactText";

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	@Override
	public D getById(Integer domainID) {
		return getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
	 */
	@Transactional(readOnly = true)
	@Override
	public D getById(String domainID) {
		return getById(PubsUtilities.parseInteger(domainID));
	}

	/** 
	 * {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.BaseDao#getByMap(Map)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<D> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#delete(java.lang.Object)
	 */
	@Transactional
	@Override
	public void delete(D domainObject) {
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
}