package gov.usgs.cida.pubs.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

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
