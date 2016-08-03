package gov.usgs.cida.pubs.transform;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class DelimitedTransformerTest {

	@Test
	public void writeDataTest() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map<String, String> mapping = new HashMap<>();
		mapping.put("A", "colA");
		mapping.put("B", "colB");
		
		DelimitedTransformer transformer = new DelimitedTransformer(baos, mapping, ";");

		Map<String, Object> result = new HashMap<>();
		result.put("A", "1");
		result.put("B", "2");
		result.put("C", "3");

		try {
			transformer.write(result);
			assertEquals(14, baos.size());
			assertEquals("colA;colB\n1;2\n", new String(baos.toByteArray(), "UTF-8"));

			result.put("A", "11");
			result.put("B", "12");
			transformer.write(result);
			assertEquals(20, baos.size());
			assertEquals("colA;colB\n1;2\n11;12\n", new String(baos.toByteArray(), "UTF-8"));
			
			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

	}

	@Test
	public void writeDataMapTest() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map<String, String> mapping = new HashMap<>();
		mapping.put("A", "colA");
		mapping.put("B", "colB");
		
		DelimitedTransformer transformer = new DelimitedTransformer(baos, mapping, "\t");

		Map<String, Object> result = new HashMap<>();
		result.put("A", "1\r\n\t");
		result.put("B", "2");
		result.put("C", "3");

		try {
			transformer.writeData(result);
			assertEquals(7, baos.size());
			assertEquals("1   \t2\n", new String(baos.toByteArray(), "UTF-8"));

			baos.reset();
			result.clear();
			result.put("C", "3");
			result.put("A", "1");
			result.put("B", "2");
			transformer.writeData(result);
			assertEquals(4, baos.size());
			assertEquals("1\t2\n", new String(baos.toByteArray(), "UTF-8"));
			
			baos.reset();
			result.put("A", null);
			transformer.writeData(result);
			assertEquals(3, baos.size());
			assertEquals("\t2\n", new String(baos.toByteArray(), "UTF-8"));
			transformer.close();
			
			
			transformer = new DelimitedTransformer(baos, mapping, ",");
			baos.reset();
			result.clear();
			result.put("A", "b,1");
			result.put("B", "2\"x\td\"34\"");
			transformer.writeData(result);
			assertEquals(21, baos.size());
			assertEquals("\"b,1\",\"2\"\"x\td\"\"34\"\"\"\n", new String(baos.toByteArray(), "UTF-8"));

			baos.reset();
			result.clear();
			result.put("A", "\"sdjfhjks\"");
			result.put("B", "\"");
			transformer.writeData(result);
			assertEquals(20, baos.size());
			assertEquals("\"\"\"sdjfhjks\"\"\",\"\"\"\"\n", new String(baos.toByteArray(), "UTF-8"));

			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

	}
	
	@Test
	public void writeHeaderTest() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map<String, String> mapping = new HashMap<>();
		mapping.put("A", "colA");
		mapping.put("B", "colB");
		
		DelimitedTransformer transformer = new DelimitedTransformer(baos, mapping, ";");

		Map<String, Object> result = new HashMap<>();
		result.put("A", "1");
		result.put("B", "2");
		result.put("C", "3");

		try {
			transformer.writeHeader(result);
			assertEquals(10, baos.size());
			assertEquals("colA;colB\n", new String(baos.toByteArray(), "UTF-8"));
			
			baos.reset();
			result.clear();
			result.put("C", "3");
			result.put("A", "1");
			result.put("B", "2");
			transformer.writeHeader(result);
			assertEquals(10, baos.size());
			assertEquals("colA;colB\n", new String(baos.toByteArray(), "UTF-8"));

			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}

	}
	
	@Test
	public void endRowTest() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DelimitedTransformer transformer = new DelimitedTransformer(baos, null, null);
		try {
			transformer.endRow();
			assertEquals(1, baos.size());
			assertEquals("\n", new String(baos.toByteArray(), "UTF-8"));

			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void copyStringTest() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DelimitedTransformer transformer = new DelimitedTransformer(baos, null, null);
		try {
			transformer.copyString(null);
			assertEquals(0, baos.size());
			
			transformer.copyString("abc");
			assertEquals(3, baos.size());
			assertEquals("abc", new String(baos.toByteArray(), "UTF-8"));

			transformer.close();
		} catch (IOException e) {
			fail(e.getLocalizedMessage());
		}
	}

}
