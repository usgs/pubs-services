package gov.usgs.cida.pubs.dao.resulthandler;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamingResultHandler implements ResultHandler {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ITransformer transformer;
	
	public StreamingResultHandler(ITransformer transformer) {
		this.transformer = transformer;
	}
	
	@Override
	public void handleResult(ResultContext context) {
		log.trace("streaming handle result : {}", (context==null ?"null" :"context"));
		try {
			transformer.write(context.getResultObject());
		} catch (Exception e) {
			log.warn("Error MapResultHandler", e);
			throw new RuntimeException("Error MapResultHandler", e);
		}
	}
	
}