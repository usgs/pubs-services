package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.OutsideAffiliation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;


@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class AffiliationDaoTest extends BaseSpringTest {

	public static final int affiliationCnt = 8;

	@Test
	public void getByIdInteger() {
		Affiliation<?> costCenter = Affiliation.getDao().getById(1);
		assertAffiliation1(costCenter);

		Affiliation<?> outsideAffiliation = Affiliation.getDao().getById(5);
		assertAffiliation5(outsideAffiliation);
	}

	@Test
	public void getByIdString() {
		Affiliation<?> costCenter = Affiliation.getDao().getById("1");
		assertAffiliation1(costCenter);

		Affiliation<?> outsideAffiliation = Affiliation.getDao().getById("5");
		assertAffiliation5(outsideAffiliation);
	}

	@Test
	public void getByMap() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(AffiliationDao.TEXT_SEARCH, "1");
		List<? extends Affiliation<?>> results = Affiliation.getDao().getByMap(filters);
		assertAffiliation1(results.get(0));
		assertAffiliation5(results.get(1));
	}

	@Test
	public void notImplemented() {
		try {
			Affiliation.getDao().add(null);
			fail("Was able to add.");
		} catch (Exception e) {
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			Affiliation.getDao().getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			Affiliation.getDao().update(null);
			fail("Was able to update.");
		} catch (Exception e) {
			assertEquals(PubsConstants.NOT_IMPLEMENTED, e.getMessage());
		}
	}

	public static void assertAffiliation1(Affiliation<?> affiliation) {
		assertEquals(1, affiliation.getId().intValue());
		assertEquals("Affiliation Cost Center 1", affiliation.getText());
		assertTrue(affiliation.isActive());
		assertTrue(affiliation.isUsgs());
		assertTrue(affiliation instanceof CostCenter);
		assertEquals(4, ((CostCenter) affiliation).getIpdsId().intValue());
	}

	public static void assertAffiliation5(Affiliation<?> affiliation) {
		assertEquals(5, affiliation.getId().intValue());
		assertEquals("Outside Affiliation 1", affiliation.getText());
		assertTrue(affiliation.isActive());
		assertFalse(affiliation.isUsgs());
		assertTrue(affiliation instanceof OutsideAffiliation);
	}

	public static void assertAffiliation7(Affiliation<?> affiliation) {
		assertEquals(7, affiliation.getId().intValue());
		assertEquals("Outside Affiliation 3", affiliation.getText());
		assertTrue(affiliation.isActive());
		assertFalse(affiliation.isUsgs());
		assertTrue(affiliation instanceof OutsideAffiliation);
	}
}