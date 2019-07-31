package gov.usgs.cida.pubs.dao.mp;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.mp.MpPublicationCostCenter;

@Repository
public class MpPublicationCostCenterDao extends MpDao<MpPublicationCostCenter> {

	private static final String NS = "mpPublicationCostCenter";

	@Autowired
	public MpPublicationCostCenterDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional
	@Override
	public Integer add(MpPublicationCostCenter domainObject) {
		 return insert(NS + ADD, domainObject);
	}

	@Transactional(readOnly = true)
	@Override
	public MpPublicationCostCenter getById(Integer domainID) {
		return (MpPublicationCostCenter) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MpPublicationCostCenter> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	/** {@inheritDoc}
	 * @see gov.usgs.cida.pubs.dao.intfc.IDao#update(java.lang.Object)
	 */
	@Transactional
	@Override
	public void update(MpPublicationCostCenter domainObject) {
		update(NS + UPDATE, domainObject);
	}

	@Transactional
	@Override
	public void delete(MpPublicationCostCenter domainObject) {
		deleteById(domainObject.getId());
	}

	@Transactional
	@Override
	public void deleteById(Integer domainID) {
		delete(NS + DELETE, domainID);
	}

	@Transactional
	@Override
	public void deleteByParent(Integer domainID) {
		delete(NS + DELETE_BY_PARENT, domainID);
	}

	@Transactional
	@Override
	public void copyFromPw(Integer prodID) {
		insert(NS + COPY_FROM_PW, prodID);
	}

	@Transactional
	@Override
	public void publishToPw(Integer prodID) {
		delete(NS + PUBLISH_DELETE, prodID);
		insert(NS + PUBLISH, prodID);
	}

}
