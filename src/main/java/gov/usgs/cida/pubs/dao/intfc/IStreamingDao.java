package gov.usgs.cida.pubs.dao.intfc;

import java.util.Map;

import org.apache.ibatis.session.ResultHandler;

public interface IStreamingDao<T> {

	/** 
	 * This Dao will stream all of the data for the given type and parameters.
	 * @param statement - the name of the mybatis select statement used
	 * @param parameterMap - the map of query parameters from the http request
	 * @param handler - the row handler to use for streaming data
	 */
	void stream(String statement, Map<String, Object> parameterMap, ResultHandler<T> handler);

}
