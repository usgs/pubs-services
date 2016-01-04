package gov.usgs.cida.pubs.domain.pw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.usgs.cida.pubs.dao.intfc.IPwPublicationDao;
import gov.usgs.cida.pubs.domain.Publication;

/**
 * @author drsteini
 *
 */
@Component
public class PwPublication extends Publication<PwPublication> {

    private static final long serialVersionUID = 1176886529474726822L;

    private static IPwPublicationDao pwPublicationDao;

    /**
     * @return the pwPublicationDao
     */
    public static IPwPublicationDao getDao() {
        return pwPublicationDao;
    }

    /**
     * The setter for pwPublicationDao.
     * @param inPwPublicationDao the pwPublicationDao to set
     */
    @Autowired
    public void setPwPublicationDao(final IPwPublicationDao inPwPublicationDao) {
        pwPublicationDao = inPwPublicationDao;
    }

}
