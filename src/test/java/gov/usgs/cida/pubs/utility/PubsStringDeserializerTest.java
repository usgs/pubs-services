package gov.usgs.cida.pubs.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PubsStringDeserializerTest {
	PubsStringDeserializer psd = new PubsStringDeserializer();

	@Test
	public void nullIfEmptyTest() {
		//NPE
		assertNull(psd.nullIfEmpty(null));

		//Happy paths
		assertNull(psd.nullIfEmpty(""));

		assertNull(psd.nullIfEmpty("    "));

		assertEquals("abc", psd.nullIfEmpty("  abc  "));

		assertEquals("a,b,c", psd.nullIfEmpty("a,b,c"));
	}
}
