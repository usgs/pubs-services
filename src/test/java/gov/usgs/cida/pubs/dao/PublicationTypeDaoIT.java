package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.domain.PublicationType;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, PublicationTypeDao.class})
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml")
})
public class PublicationTypeDaoIT extends BaseIT {

	public static final int pubTypeCnt = 11;

	@Autowired
	PublicationTypeDao publicationTypeDao;

	@Test
	public void getByIdInteger() {
		PublicationType pubType = publicationTypeDao.getById(2);
		assertEquals(2, pubType.getId().intValue());
		assertEquals("Article", pubType.getText());
	}

	@Test
	public void getByIdString() {
		PublicationType pubType = publicationTypeDao.getById(PublicationType.REPORT);
		assertEquals(PublicationType.REPORT, pubType.getId());
		assertEquals("Report", pubType.getText());
	}

	@Test
	public void getByMap() {
		List<PublicationType> pubTypes = publicationTypeDao.getByMap(null);
		assertEquals(pubTypeCnt, pubTypes.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 16);
		pubTypes = publicationTypeDao.getByMap(filters);
		assertEquals(1, pubTypes.size());
		assertEquals(16, pubTypes.get(0).getId().intValue());
		assertEquals("Patent", pubTypes.get(0).getText());

		filters.clear();
		filters.put("text", "p");
		pubTypes = publicationTypeDao.getByMap(filters);
		assertEquals(2, pubTypes.size());
	}

	@Test
	public void notImplemented() {
		try {
			publicationTypeDao.add(new PublicationType());
			fail("Was able to add.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			publicationTypeDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			publicationTypeDao.update(new PublicationType());
			fail("Was able to update.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			publicationTypeDao.delete(new PublicationType());
			fail("Was able to delete.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			publicationTypeDao.deleteById(1);
			fail("Was able to delete by it.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}
	}

}
