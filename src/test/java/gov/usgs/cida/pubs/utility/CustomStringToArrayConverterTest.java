package gov.usgs.cida.pubs.utility;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

public class CustomStringToArrayConverterTest {

	CustomStringToArrayConverter conv = new CustomStringToArrayConverter();
	
	@Test
	public void convertTest() {
		//NPE
		assertArrayEquals(new String[1],conv.convert(null));
		
		//Happy paths
		assertArrayEquals(new String[]{""}, conv.convert(""));
		
		assertArrayEquals(new String[]{"abc"}, conv.convert("abc"));

		assertArrayEquals(new String[]{"a,b,c"}, conv.convert("a,b,c"));
	}
}
