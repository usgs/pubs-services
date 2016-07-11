package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class CustomStringToStringConverterTest {

	CustomStringToStringConverter conv = new CustomStringToStringConverter();

	@Test
	public void convertTest() {
		//NPE
		assertNull(conv.convert(null));

		//Happy paths
		assertNull(conv.convert(""));

		assertEquals("abc", conv.convert("abc"));

		assertEquals("a,b,c", conv.convert("a,b,c"));
	}
}
