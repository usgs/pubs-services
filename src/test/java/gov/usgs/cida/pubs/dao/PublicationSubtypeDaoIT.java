package gov.usgs.cida.pubs.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.domain.PublicationSubtype;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, PublicationSubtypeDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml")
})
public class PublicationSubtypeDaoIT extends BaseIT {

	public static final int pubSubTypeCnt = 29;

	@Autowired
	PublicationSubtypeDao publicationSubtypeDao;

	@Test
	public void getByIdInteger() {
		PublicationSubtype pubSubtype = publicationSubtypeDao.getById(5);
		assertEquals(5, pubSubtype.getId().intValue());
		assertEquals(18, pubSubtype.getPublicationType().getId().intValue());
		assertEquals("USGS Numbered Series", pubSubtype.getText());
	}

	@Test
	public void getByIdString() {
		PublicationSubtype pubSubtype = publicationSubtypeDao.getById("6");
		assertEquals(6, pubSubtype.getId().intValue());
		assertEquals(18, pubSubtype.getPublicationType().getId().intValue());
		assertEquals("USGS Unnumbered Series", pubSubtype.getText());
	}

	@Test
	public void getByMap() {
		List<PublicationSubtype> pubSubtypes = publicationSubtypeDao.getByMap(null);
		assertEquals(pubSubTypeCnt, pubSubtypes.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 10);
		pubSubtypes = publicationSubtypeDao.getByMap(filters);
		assertEquals(1, pubSubtypes.size());
		assertEquals(10, pubSubtypes.get(0).getId().intValue());
		assertEquals(2, pubSubtypes.get(0).getPublicationType().getId().intValue());
		assertEquals("Journal Article", pubSubtypes.get(0).getText());

		filters.clear();
		filters.put("publicationTypeId", 4);
		pubSubtypes = publicationSubtypeDao.getByMap(filters);
		assertEquals(5, pubSubtypes.size());
		for (PublicationSubtype pubSubtype : pubSubtypes) {
			assertEquals(4, pubSubtype.getPublicationType().getId().intValue());
		}

		filters.put("text", "hand");
		pubSubtypes = publicationSubtypeDao.getByMap(filters);
		assertEquals(1, pubSubtypes.size());
		assertEquals(13, pubSubtypes.get(0).getId().intValue());
		assertEquals(4, pubSubtypes.get(0).getPublicationType().getId().intValue());
		assertEquals("Handbook", pubSubtypes.get(0).getText());
	}

	@Test
	public void notImplemented() {
		try {
			publicationSubtypeDao.add(new PublicationSubtype());
			fail("Was able to add.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			publicationSubtypeDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			publicationSubtypeDao.update(new PublicationSubtype());
			fail("Was able to update.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			publicationSubtypeDao.delete(new PublicationSubtype());
			fail("Was able to delete.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			publicationSubtypeDao.deleteById(1);
			fail("Was able to delete by it.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}
	}

}
