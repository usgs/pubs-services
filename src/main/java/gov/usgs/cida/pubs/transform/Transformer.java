package gov.usgs.cida.pubs.transform;

import gov.usgs.cida.pubs.transform.intfc.ITransformer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Transformer extends OutputStream implements ITransformer {

	protected OutputStream target;
	protected Map<String, String> mapping;

	/** Is this the first write to the stream. */
	private boolean first = true;

	public Transformer(OutputStream target, Map<String, String> mapping) {
		this.target = target;
		this.mapping = mapping;
	}

	protected String getMappedName(Entry<?, ?> entry) {
		return mapping.get(entry.getKey());
	}

	@Override
	public void write(Object result) throws IOException {
		if (null == result) {
			return;
		}
		
		if (result instanceof Map<?,?>) {
			Map<?,?> resultMap = (Map<?, ?>) result;
			if (first) {
				init();
				writeHeader(resultMap);
				first = false;
			}
			writeData(resultMap);
		}
		target.flush();
	}

	protected abstract void init() throws IOException;

	protected abstract void writeHeader(Map<?,?> resultMap) throws IOException;

	protected abstract void writeData(Map<?,?> resultMap) throws IOException;

	@Override
	public void write(int b) throws IOException {
		//Nothing to do here, but we need to override because we are extending OutpuStream.
		throw new RuntimeException("Writing a single byte is not supported");
	}

	@Override
	public void end() {
		try {
			target.flush();
			this.close();
		} catch (IOException ex) {
			throw new RuntimeException("Error ending transformation", ex);
		}
	}
}
