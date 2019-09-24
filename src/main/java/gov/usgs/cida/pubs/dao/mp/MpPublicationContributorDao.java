package gov.usgs.cida.pubs.dao.mp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class MpPublicationContributorDao extends MpDao<MpPublicationContributor> {

	public static final String NS = "mpPublicationContributor";

	@Autowired
	public MpPublicationContributorDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional
	@Override
	public Integer add(MpPublicationContributor domainObject) {
		return insert(NS + ADD, domainObject);
	}

	@Transactional(readOnly = true)
	@Override
	public MpPublicationContributor getById(Integer domainID) {
		return (MpPublicationContributor) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public MpPublicationContributor getById(String domainID) {
		return getById(PubsUtils.parseInteger(domainID));
	}

	@Override
	public List<MpPublicationContributor> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional
	@Override
	public void update(MpPublicationContributor domainObject) {
		update(NS + UPDATE, domainObject);
	}

	@Transactional
	@Override
	public void delete(MpPublicationContributor domainObject) {
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
		Map<String, Object> params = new HashMap<>();
		params.put("publicationId", prodID);
		params.put("updateUsername", PubsUtils.getUsername());
		getSqlSession().insert(NS + PUBLISH, params);
	}

}
