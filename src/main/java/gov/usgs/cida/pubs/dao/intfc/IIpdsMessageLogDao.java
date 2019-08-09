package gov.usgs.cida.pubs.dao.intfc;

import java.util.List;
import java.util.Map;

import gov.usgs.cida.pubs.domain.ipds.IpdsMessageLog;

public interface IIpdsMessageLogDao extends IDao<IpdsMessageLog> {

	/**
	 * Get the a list of MP data from the ipds message.
	 * @param ipdsMessageLogId ID of the log to read.
	 * @return the list of IPDS data from the log
	 */
	List<Map<String, Object>> getFromIpds(Integer ipdsMessageLogId);

	/**
	 * Get the a list of IPDSBureauApprovals from the ipds message.
	 * @param ipdsMessageLogId ID of the log to read.
	 * @return the list of IPDSBureauApprovals from the log
	 */
	List<Map<String, Object>> getFromSipp(Integer ipdsMessageLogId);
}
