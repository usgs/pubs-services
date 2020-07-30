package gov.usgs.cida.pubs.domain.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PublicationFilterParamsTest {

	@Test
	public void nullSplitTermTest() {
		assertEquals(new ArrayList<String>(), PublicationFilterParams.splitTerm(null));
	}

	@Test
	public void emptySplitTermTest() {
		assertEquals(new ArrayList<String>(), PublicationFilterParams.splitTerm(""));
	}

	@Test
	public void splitTermTest() {
		assertEquals(List.of("abc", "def", "12", "34", "ghi", "567", "89", "0", "78", "1", "55", "2020", "1032"),
				PublicationFilterParams.splitTerm("abc,def,12,34 ghi 567 89,0 78.1 -55 2020-1032"));
	}
}
