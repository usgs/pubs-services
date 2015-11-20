package gov.usgs.cida.pubs.dao.intfc;

import java.util.Map;

import org.apache.ibatis.session.ResultHandler;

public interface IStreamingDao<T> {

    static final String GET_STREAM_BY_MAP = ".getStreamByMap";

	/** 
	 * This Dao will stream all of the data for the given type and parameters.
	 * @param parameterMap - the map of query parameters from the http request
	 * @param handler - the row handler to use for streaming data
	 */
	void stream(Map<String, Object> parameterMap, ResultHandler<T> handler);

}
