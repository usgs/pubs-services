package gov.usgs.cida.pubs.dao.intfc;

import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;
import gov.usgs.cida.pubs.domain.mp.MpPublication;

import java.util.List;

public interface IIpdsMessageLogDao extends IDao<IpdsMessageLog> {

    /**
     * Get the a list of MP data from the ipds message.
     * @param ipdsMessageLogId ID of the log to read.
     * @return the list of mpPublication data from the log
     */
    List<MpPublication> getFromIpds(Integer ipdsMessageLogId);

}
