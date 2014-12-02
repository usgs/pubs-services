package gov.usgs.cida.pubs.dao.mp;

import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.dao.BaseDao;
import gov.usgs.cida.pubs.dao.intfc.IMpDao;

import org.springframework.transaction.annotation.Transactional;

public abstract class MpDao<D> extends BaseDao<D> implements IMpDao<D> {

    public static final String COPY_FROM_PW = ".copyMpFromPw";
    public static final String PUBLISH = ".publish";
    public static final String PUBLISH_DELETE = ".publishDelete";

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IMpDao#copyFromPw(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public void copyFromPw(Integer prodID) {
        throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
    }

    /** {@inheritDoc}
     * @see gov.usgs.cida.pubs.dao.intfc.IMpDao#publishToPw(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public void publishToPw(Integer prodID) {
        throw new RuntimeException(PubsConstants.NOT_IMPLEMENTED);
    }

}
