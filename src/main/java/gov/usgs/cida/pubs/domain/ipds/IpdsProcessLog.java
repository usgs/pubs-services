package gov.usgs.cida.pubs.domain.ipds;

import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.BaseDomain;

/**
 * An object for logging the issues mapping IPDS mqueues.
 * @author duselman
 *
 */
public class IpdsProcessLog extends BaseDomain<IpdsProcessLog> {

    private static IDao<IpdsProcessLog> ipdsProcessLogDao;
    public static IDao<IpdsProcessLog> getDao() {
        return ipdsProcessLogDao;
    }
    public void setIpdsProcessLogDao(final IDao<IpdsProcessLog> ipdsProcessLogDao) {
        IpdsProcessLog.ipdsProcessLogDao = ipdsProcessLogDao;
    }

    private String ipdsNumber;

    private String uri;

    private String message;

    public IpdsProcessLog() {
        // Spring requirement
    }

    public IpdsProcessLog(String ipdsNum, String msg, String inUri) {
        this.ipdsNumber  = ipdsNum;
        this.message = msg;
        uri = inUri;
    }

    public String getIpdsNumber() {
        return ipdsNumber;
    }

    public void setIpdsNumber(String ipdsNumber) {
        this.ipdsNumber = ipdsNumber;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String inUri) {
        uri = inUri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
