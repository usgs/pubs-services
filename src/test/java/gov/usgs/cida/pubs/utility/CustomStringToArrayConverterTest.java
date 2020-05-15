package gov.usgs.cida.pubs.utility;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
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
