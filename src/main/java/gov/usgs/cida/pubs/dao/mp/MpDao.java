package gov.usgs.cida.pubs.dao.mp;

import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.intfc.IMpDao;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class MpDao<D> extends BaseDao<D> implements IMpDao<D> {

	public static final String COPY_FROM_PW = ".copyMpFromPw";
	public static final String PUBLISH = ".publish";
	public static final String PUBLISH_DELETE = ".publishDelete";

	@Autowired
	public MpDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	@Transactional(readOnly = true)
	@Override
	public void copyFromPw(Integer prodID) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

	@Transactional(readOnly = true)
	@Override
	public void publishToPw(Integer prodID) {
		throw new RuntimeException(PubsConstantsHelper.NOT_IMPLEMENTED);
	}

}
