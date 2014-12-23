package gov.usgs.cida.pubs.dao.mp;

import gov.usgs.cida.pubs.aop.ISetDbContext;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.intfc.IMpListDao;
import gov.usgs.cida.pubs.domain.mp.MpList;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

public class MpListDao extends BaseDao<MpList> implements IMpListDao {

	private static final String NS = "mpList";
	private static final String GET_BY_IPDS_ID = ".getByIpdsId";
	public static final String LIST_TYPE_SEARCH = "listType";

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public MpList getById(Integer domainID) {
		return getSqlSession().selectOne(NS + GET_BY_ID, domainID);
	}

    /** 
     * {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IDao#getById(java.lang.String)
     */
    @Transactional(readOnly = true)
    @ISetDbContext
    @Override
    public MpList getById(String domainID) {
        return getById(PubsUtilities.parseInteger(domainID));
    }

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public List<MpList> getByMap(Map<String, Object> filters) {
		return getSqlSession().selectList(NS + GET_BY_MAP, filters);
	}

	@Transactional(readOnly = true)
	@ISetDbContext
	@Override
	public MpList getByIpdsId(Integer ipdsId) {
		return getSqlSession().selectOne(NS + GET_BY_IPDS_ID, ipdsId);
	}
}
