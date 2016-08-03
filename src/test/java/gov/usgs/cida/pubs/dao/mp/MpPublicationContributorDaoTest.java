package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;
import gov.usgs.cida.pubs.domain.pw.PwPublication;
import gov.usgs.cida.pubs.domain.pw.PwPublicationContributor;
import gov.usgs.cida.pubs.utility.PubsUtilities;

import java.util.Arrays;
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
public class MpPublicationContributorDaoTest extends BaseSpringTest {

	public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors");

	@Test
	public void getbyIdTests() {
		MpPublicationContributor contrib = MpPublicationContributor.getDao().getById(1);
		assertMpContributor1(contrib);
		contrib = MpPublicationContributor.getDao().getById("4");
		assertMpContributor4(contrib);
	}

	@Test
	public void getByMapTests() {
		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 1);
		List<MpPublicationContributor> mpContributors = MpPublicationContributor.getDao().getByMap(filters);
		assertNotNull(mpContributors);
		assertEquals(1, mpContributors.size());
		assertMpContributor1((MpPublicationContributor) mpContributors.get(0));

		filters.clear();
		filters.put("publicationId", 1);
		mpContributors = MpPublicationContributor.getDao().getByMap(filters);
		assertNotNull(mpContributors);
		assertEquals(4, mpContributors.size());

		filters.clear();
		filters.put("contributorTypeId", 1);
		mpContributors = MpPublicationContributor.getDao().getByMap(filters);
		assertNotNull(mpContributors);
		assertEquals(3, mpContributors.size());

		filters.clear();
		filters.put("contributorId", 1);
		mpContributors = MpPublicationContributor.getDao().getByMap(filters);
		assertNotNull(mpContributors);
		assertEquals(3, mpContributors.size());

		filters.clear();
		filters.put("id", 6);
		filters.put("publicationId", 2);
		filters.put("contributorTypeId", 2);
		filters.put("contributorId", 2);
		mpContributors = MpPublicationContributor.getDao().getByMap(filters);
		assertNotNull(mpContributors);
		assertEquals(1, mpContributors.size());
	}

	@Test
	public void addUpdateDeleteTest() {
		MpPublicationContributor newContrib = new MpPublicationContributor();
		newContrib.setPublicationId(2);
		Contributor<?> contrib3 = new UsgsContributor();
		contrib3.setId(3);
		newContrib.setContributor(contrib3);
		ContributorType author = new ContributorType();
		author.setId(ContributorType.AUTHORS);
		newContrib.setContributorType(author);
		newContrib.setRank(98);
		MpPublicationContributor.getDao().add(newContrib);
		
		MpPublicationContributor persistedA = MpPublicationContributor.getDao().getById(newContrib.getId());
		assertNotNull(persistedA);
		assertNotNull(persistedA.getId());
		assertDaoTestResults(MpPublicationContributor.class, newContrib, persistedA, IGNORE_PROPERTIES, true, true);
	
		Contributor<?> contrib1 = new UsgsContributor();
		contrib1.setId(1);
		persistedA.setContributor(contrib1);
		ContributorType editor = new ContributorType();
		editor.setId(ContributorType.EDITORS);
		persistedA.setContributorType(editor);
		persistedA.setRank(99);
		MpPublicationContributor.getDao().update(persistedA);
	
		MpPublicationContributor persistedC = MpPublicationContributor.getDao().getById(newContrib.getId());
		assertNotNull(persistedC);
		assertNotNull(persistedC.getId());
		assertDaoTestResults(MpPublicationContributor.class, persistedA, persistedC, IGNORE_PROPERTIES, true, true);
	
		MpPublicationContributor.getDao().delete(persistedC);
		assertNull(MpPublicationContributor.getDao().getById(newContrib.getId()));
	
		MpPublicationContributor.getDao().deleteById(1);
		assertNull(MpPublicationContributor.getDao().getById(1));
	
		MpPublicationContributor.getDao().deleteByParent(2);
		Map<String, Object> filters = new HashMap<>();
		filters.put("publicationId", 2);
		List<MpPublicationContributor> mpContribs = MpPublicationContributor.getDao().getByMap(filters);
		assertTrue(mpContribs.isEmpty());
	}
	
	@Test
	public void copyFromPwTest() {
		MpPublication.getDao().copyFromPw(4);
		MpPublicationContributor.getDao().copyFromPw(4);
		MpPublicationContributor contrib = MpPublicationContributor.getDao().getById(10);
		assertMpContributor10(contrib);
		contrib = MpPublicationContributor.getDao().getById(11);
		assertMpContributor11(contrib);
	}

	@Test
	public void publishToPwTest() {
		MpPublicationContributor.getDao().publishToPw(null);
		MpPublicationContributor.getDao().publishToPw(-1);
		MpPublication.getDao().publishToPw(1);
		
		//this one should be a straight add.
		MpPublicationContributor.getDao().publishToPw(1);
		PwPublication pub = PwPublication.getDao().getById(1);
		assertEquals(4, pub.getContributors().size());
		assertEquals(2, pub.getContributorsToMap().get(PubsUtilities.getAuthorKey()).size());
		for (PublicationContributor<?> contrib : pub.getContributorsToMap().get(PubsUtilities.getAuthorKey())) {
			if (1 == contrib.getId()) {
				assertPwContributor1(contrib);
			} else if (2 == contrib.getId()) {
				assertPwContributor2(contrib);
			} else {
				fail("Got a bad contributor:" + contrib.getId());
			}
		}
		assertEquals(2, pub.getContributorsToMap().get(PubsUtilities.getEditorKey()).size());
		for (PublicationContributor<?> contrib : pub.getContributorsToMap().get(PubsUtilities.getEditorKey())) {
			if (3 == contrib.getId()) {
				assertPwContributor3(contrib);
			} else if (4 == contrib.getId()) {
				assertPwContributor4(contrib);
			} else {
				fail("Got a bad contributor:" + contrib.getId());
			}
		}
		
		//this one should be a merge.
		MpPublication.getDao().copyFromPw(4);
		MpPublicationContributor.getDao().copyFromPw(4);
		MpPublication.getDao().publishToPw(4);
		MpPublicationContributor.getDao().deleteById(10);
		MpPublicationContributor.getDao().publishToPw(4);
		pub = PwPublication.getDao().getById(4);
		assertEquals(1, pub.getContributors().size());
		assertNull(pub.getContributorsToMap().get(PubsUtilities.getAuthorKey()));
		assertEquals(1, pub.getContributorsToMap().get(PubsUtilities.getEditorKey()).size());
		for (PublicationContributor<?> contrib :pub.getContributorsToMap().get(PubsUtilities.getEditorKey())) {
			assertPwContributor11(contrib);
		}
	}

	public static void assertPwContributor1(PublicationContributor<?> contrib) {
		assertTrue(contrib instanceof PwPublicationContributor);
		assertContributor1(contrib);
	}

	public static void assertMpContributor1(PublicationContributor<?> contrib) {
		assertTrue(contrib instanceof MpPublicationContributor);
		assertContributor1(contrib);
	}

	public static void assertContributor1(PublicationContributor<?> contrib) {
		assertNotNull(contrib);
		assertEquals(1, contrib.getId().intValue());
		assertEquals(1, contrib.getPublicationId().intValue());
		assertNotNull(contrib.getContributor());
		assertEquals(1, contrib.getContributor().getId().intValue());
		assertNotNull(contrib.getContributorType());
		assertEquals(1, contrib.getContributorType().getId().intValue());
		assertEquals(1, contrib.getRank().intValue());
	}

	public static void assertPwContributor2(PublicationContributor<?> contrib) {
		assertTrue(contrib instanceof PwPublicationContributor);
		assertContributor2(contrib);
	}

	public static void assertMpContributor2(PublicationContributor<?> contrib) {
		assertTrue(contrib instanceof MpPublicationContributor);
		assertContributor2(contrib);
	}

	public static void assertContributor2(PublicationContributor<?> contrib) {
		assertNotNull(contrib);
		assertEquals(2, contrib.getId().intValue());
		assertEquals(1, contrib.getPublicationId().intValue());
		assertNotNull(contrib.getContributor());
		assertEquals(2, contrib.getContributor().getId().intValue());
		assertNotNull(contrib.getContributorType());
		assertEquals(1, contrib.getContributorType().getId().intValue());
		assertEquals(2, contrib.getRank().intValue());
	}

	public static void assertPwContributor3(PublicationContributor<?> contrib) {
		assertTrue(contrib instanceof PwPublicationContributor);
		assertContributor3(contrib);
	}

	public static void assertMpContributor3(PublicationContributor<?> contrib) {
		assertTrue(contrib instanceof MpPublicationContributor);
		assertContributor3(contrib);
	}

	public static void assertContributor3(PublicationContributor<?> contrib) {
		assertNotNull(contrib);
		assertEquals(3, contrib.getId().intValue());
		assertEquals(1, contrib.getPublicationId().intValue());
		assertNotNull(contrib.getContributor());
		assertEquals(1, contrib.getContributor().getId().intValue());
		assertNotNull(contrib.getContributorType());
		assertEquals(2, contrib.getContributorType().getId().intValue());
		assertEquals(2, contrib.getRank().intValue());
	}

	public static void assertPwContributor4(PublicationContributor<?> contrib) {
		assertTrue(contrib instanceof PwPublicationContributor);
		assertContributor4(contrib);
	}

	public static void assertMpContributor4(PublicationContributor<?> contrib) {
		assertTrue(contrib instanceof MpPublicationContributor);
		assertContributor4(contrib);
	}

	public static void assertContributor4(PublicationContributor<?> contrib) {
		assertNotNull(contrib);
		assertEquals(4, contrib.getId().intValue());
		assertEquals(1, contrib.getPublicationId().intValue());
		assertNotNull(contrib.getContributor());
		assertEquals(2, contrib.getContributor().getId().intValue());
		assertNotNull(contrib.getContributorType());
		assertEquals(2, contrib.getContributorType().getId().intValue());
		assertEquals(1, contrib.getRank().intValue());
	}

	public static void assertMpContributor10(MpPublicationContributor contrib) {
		assertNotNull(contrib);
		assertEquals(10, contrib.getId().intValue());
		assertEquals(4, contrib.getPublicationId().intValue());
		assertNotNull(contrib.getContributor());
		assertEquals(1, contrib.getContributor().getId().intValue());
		assertNotNull(contrib.getContributorType());
		assertEquals(1, contrib.getContributorType().getId().intValue());
		assertEquals(1, contrib.getRank().intValue());
	}

	public static void assertPwContributor11(PublicationContributor<?> contrib) {
		assertTrue(contrib instanceof PwPublicationContributor);
		assertContributor11(contrib);
	}

	public static void assertMpContributor11(PublicationContributor<?> contrib) {
		assertTrue(contrib instanceof MpPublicationContributor);
		assertContributor11(contrib);
	}

	public static void assertContributor11(PublicationContributor<?> contrib) {
		assertNotNull(contrib);
		assertEquals(11, contrib.getId().intValue());
		assertEquals(4, contrib.getPublicationId().intValue());
		assertNotNull(contrib.getContributor());
		assertEquals(2, contrib.getContributor().getId().intValue());
		assertNotNull(contrib.getContributorType());
		assertEquals(2, contrib.getContributorType().getId().intValue());
		assertEquals(1, contrib.getRank().intValue());
	}

}
