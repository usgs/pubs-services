package gov.usgs.cida.pubs.dao.ipds;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.intfc.IIpdsMessageLogDao;
import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;

@Repository
public class IpdsMessageLogDao extends BaseDao <IpdsMessageLog> implements IIpdsMessageLogDao {

	@Autowired
	public IpdsMessageLogDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	private static final String NS = "ipdsMessageLog";

	@Transactional
	@Override
	public Integer add(IpdsMessageLog domainObject) {
		return insert(NS + ADD, domainObject);
	}

	@Transactional(readOnly = true)
	@Override
	public IpdsMessageLog getById(Integer domainID) {
		return (IpdsMessageLog) getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

	@Transactional(readOnly = true)
	@Override
	public List<IpdsMessageLog> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP);
	}

	@Transactional
	@Override
	public void update(IpdsMessageLog domainObject) {
		update(NS + UPDATE, domainObject);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> getFromIpds(Integer ipdsMessageLogId) {
		return getSqlSession().selectList(NS + ".getMpPublicationFromIpds", ipdsMessageLogId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Map<String, Object>> getFromSipp(Integer ipdsMessageLogId) {
		return getSqlSession().selectList(NS + ".getMpPublicationFromSipp", ipdsMessageLogId);
	}

}
