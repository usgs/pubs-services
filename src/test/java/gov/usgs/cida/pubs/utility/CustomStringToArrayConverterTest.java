package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class CustomStringToArrayConverterTest {

	CustomStringToArrayConverter conv = new CustomStringToArrayConverter();
	
	@Test
	public void convertTest() {
		//NPE
		assertNull(conv.convert(null));
		
		//Happy paths
		assertNull(conv.convert(""));
		
		assertArrayEquals(new String[]{"abc"}, conv.convert("abc"));

		assertArrayEquals(new String[]{"a,b,c"}, conv.convert("a,b,c"));
	}
}
