package gov.usgs.cida.pubs.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TransformerTest {

	private class TTransformer extends Transformer {
		public int initCalled = 0;
		public int writeHeaderCalled = 0;
		public int writeDataCalled = 0;
		public TTransformer(OutputStream target, Map<String, String> mapping) {
			super(target, mapping);
		}
		@Override
		protected void init() throws IOException {
			initCalled = initCalled + 1;
		}
		@Override
		protected void writeHeader(Map<?, ?> resultMap) throws IOException {
			writeHeaderCalled = writeHeaderCalled + 1;
		}
		@Override
		protected void writeData(Map<?, ?> resultMap) throws IOException {
			writeDataCalled = writeDataCalled + 1;
		}
	}
	
    @Test
	public void writeTest() {
    	TTransformer transformer = new TTransformer(new ByteArrayOutputStream(), null);

    	//Don't process null results
		try {
			transformer.write((Object) null);
			assertEquals(0, transformer.initCalled);
			assertEquals(0, transformer.writeHeaderCalled);
			assertEquals(0, transformer.writeDataCalled);
			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

    	//Don't process results that aren't a map
		try {
			transformer.write((Object) "ABCDEFG");
			assertEquals(0, transformer.initCalled);
			assertEquals(0, transformer.writeHeaderCalled);
			assertEquals(0, transformer.writeDataCalled);
			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
    	
    	Map<String, Object> result = new HashMap<>();
    	result.put("A", "1");
    	result.put("B", "2");
    	
		try {
			transformer.write((Object) result);
			transformer.write((Object) result);
			assertEquals(1, transformer.initCalled);
			assertEquals(1, transformer.writeHeaderCalled);
			assertEquals(2, transformer.writeDataCalled);
			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}
    
}
