package gov.usgs.cida.pubs.dao.resulthandler;
import gov.usgs.cida.pubs.transform.intfc.ITransformer;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamingResultHandler<T> implements ResultHandler<T> {
	private static final Logger LOG = LoggerFactory.getLogger(StreamingResultHandler.class);
	private final ITransformer transformer;

	public StreamingResultHandler(ITransformer transformer) {
		this.transformer = transformer;
	}

	@Override
	public void handleResult(ResultContext<? extends T> context) {
		LOG.trace("streaming handle result : {}", context==null ? "null" :"context");
		try {
			transformer.write(context.getResultObject());
		} catch (Exception e) {
			LOG.warn("Error MapResultHandler", e);
			throw new RuntimeException("Error MapResultHandler", e);
		}
	}
}