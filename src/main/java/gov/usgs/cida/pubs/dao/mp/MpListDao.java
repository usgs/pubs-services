package gov.usgs.cida.pubs.dao.mp;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.utility.PubsUtils;

@Repository
public class MpListDao extends BaseDao<MpList> {

	private static final String NS = "mpList";
	public static final String LIST_TYPE_SEARCH = "listType";

	@Autowired
	public MpListDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional(readOnly = true)
	@Override
	public MpList getById(Integer domainID) {
		return getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public MpList getById(String domainID) {
		return getById(PubsUtils.parseInteger(domainID));
	}

	@Transactional(readOnly = true)
	@Override
	public List<MpList> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}
}
