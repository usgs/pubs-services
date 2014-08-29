package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.ContributorType;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MpPublicationContributorDaoTest extends BaseSpringDaoTest {

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

		filters.put("id", 1);
		mpContributors = MpPublicationContributor.getDao().getByMap(filters);
		assertNotNull(mpContributors);
		assertEquals(1, mpContributors.size());
	}

	@Test
	public void addUpdateDeleteTest() {
		MpPublicationContributor newContrib = new MpPublicationContributor();
		newContrib.setPublicationId(2);
		Contributor<?> contrib1 = new UsgsContributor();
		contrib1.setId(1);
		newContrib.setContributor(contrib1);
		ContributorType author = new ContributorType();
		author.setId(ContributorType.AUTHORS);
		newContrib.setContributorType(author);
		newContrib.setRank(98);
		MpPublicationContributor.getDao().add(newContrib);
		
		MpPublicationContributor persistedA = MpPublicationContributor.getDao().getById(newContrib.getId());
		assertNotNull(persistedA);
		assertNotNull(persistedA.getId());
		assertDaoTestResults(MpPublicationContributor.class, newContrib, persistedA, IGNORE_PROPERTIES, true, true);
	
		Contributor<?> contrib2 = new UsgsContributor();
		contrib2.setId(2);
		persistedA.setContributor(contrib2);
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

	public static void assertMpContributor1(MpPublicationContributor contrib) {
		assertNotNull(contrib);
		assertEquals(1, contrib.getId().intValue());
		assertEquals(1, contrib.getPublicationId().intValue());
		assertNotNull(contrib.getContributor());
		assertEquals(1, contrib.getContributor().getId().intValue());
		assertNotNull(contrib.getContributorType());
		assertEquals(1, contrib.getContributorType().getId().intValue());
		assertEquals(1, contrib.getRank().intValue());
	}

	public static void assertMpContributor4(MpPublicationContributor contrib) {
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

	public static void assertMpContributor11(MpPublicationContributor contrib) {
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
