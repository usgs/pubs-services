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

import gov.usgs.cida.pubs.BaseIT;
import gov.usgs.cida.pubs.PubsConstantsHelper;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.springinit.DbTestConfig;

@SpringBootTest(webEnvironment=WebEnvironment.NONE,
	classes={DbTestConfig.class, ContributorType.class, ContributorTypeDao.class})
public class ContributorTypeDaoIT extends BaseIT {

	public static final int CONTRIBUTOR_TYPE_CNT = 3;

	@Autowired
	ContributorTypeDao contributorTypeDao;

	@Test
	public void getByIdInteger() {
		ContributorType contributorType = contributorTypeDao.getById(1);
		assertEquals(1, contributorType.getId().intValue());
		assertEquals("Authors", contributorType.getText());
	}

	@Test
	public void getByIdString() {
		ContributorType contributorType = contributorTypeDao.getById("2");
		assertEquals(2, contributorType.getId().intValue());
		assertEquals("Editors", contributorType.getText());
	}

	@Test
	public void getByMap() {
		List<ContributorType> contributorTypes = contributorTypeDao.getByMap(null);
		assertEquals(CONTRIBUTOR_TYPE_CNT, contributorTypes.size());

		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 1);
		contributorTypes = contributorTypeDao.getByMap(filters);
		assertEquals(1, contributorTypes.size());
		assertEquals(1, contributorTypes.get(0).getId().intValue());
		assertEquals("Authors", contributorTypes.get(0).getText());

		filters.clear();
		filters.put("text", "ed");
		contributorTypes = contributorTypeDao.getByMap(filters);
		assertEquals(1, contributorTypes.size());

	}

	@Test
	public void notImplemented() {
		try {
			contributorTypeDao.add(new ContributorType());
			fail("Was able to add.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(PublicationDao.PROD_ID, 1);
			contributorTypeDao.getObjectCount(params);
			fail("Was able to get count.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			contributorTypeDao.update(new ContributorType());
			fail("Was able to update.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			contributorTypeDao.delete(new ContributorType());
			fail("Was able to delete.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}

		try {
			contributorTypeDao.deleteById(1);
			fail("Was able to delete by it.");
		} catch (Exception e) {
			assertEquals(PubsConstantsHelper.NOT_IMPLEMENTED, e.getMessage());
		}
	}

	public static ContributorType getAuthor() {
		ContributorType author = new ContributorType();
		author.setId(ContributorType.AUTHORS);
		author.setText(ContributorType.AUTHOR_KEY);
		return author;
	}

	public static ContributorType getEditor() {
		ContributorType editor = new ContributorType();
		editor.setId(ContributorType.EDITORS);
		editor.setText(ContributorType.EDITOR_KEY);
		return editor;
	}

	public static ContributorType getCompiler() {
		ContributorType compiler = new ContributorType();
		compiler.setId(ContributorType.COMPILERS);
		compiler.setText(ContributorType.COMPILER_KEY);
		return compiler;
	}

}
