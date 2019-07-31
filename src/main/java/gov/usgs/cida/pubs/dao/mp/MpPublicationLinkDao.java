package gov.usgs.cida.pubs.dao.mp;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class MpPublicationLinkDao extends MpDao<MpPublicationLink> {

	private static final String NS = "mpPublicationLink";
	
	public static final String LINK_TYPE_SEARCH = "linkTypeId";
	public static final String PUB_SEARCH = "publicationId";

	@Autowired
	public MpPublicationLinkDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional
	@Override
	public Integer add(MpPublicationLink domainObject) {
		return insert(NS + ADD, domainObject);
	}

	@Transactional(readOnly = true)
	@Override
	public MpPublicationLink getById(Integer domainID) {
		return (MpPublicationLink) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public MpPublicationLink getById(String domainID) {
		return getById(PubsUtils.parseInteger(domainID));
	}

	@Transactional(readOnly = true)
	@Override
	public List<MpPublicationLink> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional
	@Override
	public void update(MpPublicationLink domainObject) {
		update(NS + UPDATE, domainObject);
	}

	@Transactional
	@Override
	public void delete(MpPublicationLink domainObject) {
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
