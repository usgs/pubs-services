package gov.usgs.cida.pubs.utility;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

public class StringArrayCleansingConverterTest {

	StringArrayCleansingConverter conv = new StringArrayCleansingConverter();

	@Test
	public void convertTest() {
		//NPE
		assertArrayEquals(new String[]{}, conv.convert(null));

		//Happy paths
		assertArrayEquals(new String[]{}, conv.convert(new String[]{}));

		assertArrayEquals(new String[]{"abc"}, conv.convert(new String[]{"abc"}));

		assertArrayEquals(new String[]{"a,b,c"}, conv.convert(new String[]{"a,b,c"}));

		assertArrayEquals(new String[]{"a","b","c"}, conv.convert(new String[]{" a ",null, "b", "", " ", "c"}));
	}

}
