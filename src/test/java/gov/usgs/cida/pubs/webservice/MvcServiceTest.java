package gov.usgs.cida.pubs.webservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class MvcServiceTest extends BaseSpringTest {

	private class TestMvcService extends MvcService<MvcServiceTest> {
		
	}
	
	private TestMvcService testMvcService = new TestMvcService();
	
	@Test
    public void addToFiltersIfNotNullStringTest() {
		Map<String, Object> filters = new HashMap<>();
		String values = null;
		//A null value should not add to the map
		testMvcService.addToFiltersIfNotNull(filters, "x", values);
		assertEquals(0, filters.keySet().size());
		
		//An empty value should not add to the map
		testMvcService.addToFiltersIfNotNull(filters, "x", "");
		assertEquals(0, filters.keySet().size());

		testMvcService.addToFiltersIfNotNull(filters, "x", "y");
		assertEquals(1, filters.keySet().size());
		assertTrue(filters.containsKey("x"));
		assertEquals("y", filters.get("x"));
    }

	@Test
    public void addToFiltersIfNotNullArrayTest() {
		Map<String, Object> filters = new HashMap<>();
		String[] values = null;
		//A null value should not add to the map
		testMvcService.addToFiltersIfNotNull(filters, "x", values);
		assertEquals(0, filters.keySet().size());
		
		//An empty value should not add to the map
		testMvcService.addToFiltersIfNotNull(filters, "x", new String[]{""});
		assertEquals(0, filters.keySet().size());

		testMvcService.addToFiltersIfNotNull(filters, "x", new String[]{"","y"});
		assertEquals(1, filters.keySet().size());
		assertTrue(filters.containsKey("x"));
		assertEquals("y", ((Object[]) filters.get("x"))[0].toString());
    }

	@Test
	public void configureSingleSearchFiltersTest() {
		Map<String, Object> filters = new HashMap<>();

		//A null value should not add to the map
		testMvcService.configureSingleSearchFilters(filters, null);
		assertEquals(0, filters.keySet().size());
		
		//An empty value should not add to the map
		testMvcService.configureSingleSearchFilters(filters, "");
		assertEquals(0, filters.keySet().size());
		testMvcService.configureSingleSearchFilters(filters, "   ,  ");
		assertEquals(0, filters.keySet().size());

		testMvcService.configureSingleSearchFilters(filters, "turtles and, loggerhead,, ");
		assertEquals(2, filters.keySet().size());
		assertTrue(filters.containsKey("searchTerms"));
		Object[] searchTerms = (Object[]) filters.get("searchTerms");
		assertEquals(2, searchTerms.length);
		assertEquals("turtles", searchTerms[0].toString());
		assertEquals("loggerhead", searchTerms[1].toString());
		assertTrue(filters.containsKey("searchTerms"));
		String q = (String) filters.get("q");
		assertEquals("turtles and loggerhead", q);
	}
}
