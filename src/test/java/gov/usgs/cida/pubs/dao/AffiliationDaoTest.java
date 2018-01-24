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
	@DatabaseSetup("classpath:/testData/affiliation.xml")
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
	public void getByMapText() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(AffiliationDao.TEXT_SEARCH, "Affiliation Cost Center 1");
		List<? extends Affiliation<?>> results = Affiliation.getDao().getByMap(filters);
		assertAffiliation1(results.get(0));
		filters.put(AffiliationDao.TEXT_SEARCH, "Affiliation Cost Center");
		results = Affiliation.getDao().getByMap(filters);
		assertEquals("Affiliations starting with 'Affiliation Cost Center'", 2, results.size());
		filters.put(AffiliationDao.TEXT_SEARCH, "x");
		results = Affiliation.getDao().getByMap(filters);
		assertEquals("Affiliations starting with 'x'", 3, results.size());
	}

	@Test
	public void getByMapId() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(AffiliationDao.ID_SEARCH, "1");
		List<? extends Affiliation<?>> results = Affiliation.getDao().getByMap(filters);
		assertAffiliation1(results.get(0));
	}

	@Test
	public void getByMapActive() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(AffiliationDao.ACTIVE_SEARCH, "Y");
		List<? extends Affiliation<?>> results = Affiliation.getDao().getByMap(filters);
		assertEquals(5, results.size());
		filters.put(AffiliationDao.ACTIVE_SEARCH, "N");
		results = Affiliation.getDao().getByMap(filters);
		assertEquals(2, results.size());
	}

	@Test
	public void getByMapUsgs() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(AffiliationDao.USGS_SEARCH, "Y");
		List<? extends Affiliation<?>> results = Affiliation.getDao().getByMap(filters);
		assertEquals(4, results.size());
		filters.put(AffiliationDao.USGS_SEARCH, "N");
		results = Affiliation.getDao().getByMap(filters);
		assertEquals(3, results.size());
	}

	@Test
	public void getByMapIpds() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(PublicationDao.IPDS_ID, "4");
		List<? extends Affiliation<?>> results = Affiliation.getDao().getByMap(filters);
		assertEquals(1, results.size());
		assertAffiliation1(results.get(0));
	}

	@Test
	public void getByMapExact() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(AffiliationDao.EXACT_SEARCH, "Outside Affiliation 1");
		List<? extends Affiliation<?>> results = Affiliation.getDao().getByMap(filters);
		assertEquals(1, results.size());
		assertAffiliation5(results.get(0));
	}

	@Test
	public void getByMapMixed() {
		Map<String, Object> filters = new HashMap<>();
		filters.put(AffiliationDao.TEXT_SEARCH, "Affiliation Cost Center");
		filters.put(AffiliationDao.EXACT_SEARCH, "Affiliation Cost Center 1");
		filters.put(PublicationDao.IPDS_ID, "4");
		filters.put(AffiliationDao.USGS_SEARCH, "Y");
		filters.put(AffiliationDao.ACTIVE_SEARCH, "Y");
		List<? extends Affiliation<?>> results = Affiliation.getDao().getByMap(filters);
		assertEquals(1, results.size());
		assertAffiliation1(results.get(0));
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