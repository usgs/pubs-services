package gov.usgs.cida.pubs.dao.mp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.dao.BaseSpringDaoTest;
import gov.usgs.cida.pubs.domain.LinkFileType;
import gov.usgs.cida.pubs.domain.LinkType;
import gov.usgs.cida.pubs.domain.PublicationLink;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationLink;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author drsteini
 *
 */
public class MpPublicationLinkDaoTest extends BaseSpringDaoTest {

	public static final List<String> IGNORE_PROPERTIES = Arrays.asList("validationErrors", "valErrors");

	@Test
	public void getbyIdTests() {
		MpPublicationLink link = MpPublicationLink.getDao().getById(1);
		assertMpLink1(link);
		link = MpPublicationLink.getDao().getById("2");
		assertMpLink2(link);
	}

	@Test
	public void getByMapTests() {
		Map<String, Object> filters = new HashMap<>();
		filters.put("id", 1);
		List<MpPublicationLink> mpLinks = MpPublicationLink.getDao().getByMap(filters);
		assertNotNull(mpLinks);
		assertEquals(1, mpLinks.size());
		assertMpLink1((MpPublicationLink) mpLinks.get(0));

		filters.clear();
		filters.put("publicationId", 1);
		mpLinks = MpPublicationLink.getDao().getByMap(filters);
		assertNotNull(mpLinks);
		assertEquals(2, mpLinks.size());

		filters.clear();
		filters.put("linkTypeId", 6);
		mpLinks = MpPublicationLink.getDao().getByMap(filters);
		assertNotNull(mpLinks);
		assertEquals(1, mpLinks.size());
	}

	@Test
	public void addUpdateDeleteTest() {
		MpPublicationLink newLink = new MpPublicationLink();
		newLink.setPublicationId(1);
		newLink.setRank(4);
		LinkType linkType = new LinkType();
		linkType.setId(1);
		newLink.setLinkType(linkType);
		newLink.setUrl("www.newlink.org");
		newLink.setText("newlink text");
		newLink.setSize("15 bytes");
		newLink.setDescription("my link description");
		LinkFileType linkFileType = new LinkFileType();
		linkFileType.setId(4);
		newLink.setLinkFileType(linkFileType);
		MpPublicationLink.getDao().add(newLink);
		
		MpPublicationLink persistedA = MpPublicationLink.getDao().getById(newLink.getId());
		assertNotNull(persistedA);
		assertNotNull(persistedA.getId());
		assertDaoTestResults(MpPublicationLink.class, newLink, persistedA, IGNORE_PROPERTIES, true, true);

		persistedA.setRank(5);
		LinkType newLinkType = new LinkType();
		newLinkType.setId(2);
		persistedA.setLinkType(newLinkType);
		persistedA.setUrl("www.updated.org");
		persistedA.setText("updated text");
		persistedA.setSize("86 TB");
		newLink.setDescription("my new link description");
		LinkFileType newLinkFileType = new LinkFileType();
		newLinkFileType.setId(3);
		persistedA.setLinkFileType(newLinkFileType);
		MpPublicationLink.getDao().update(persistedA);

		MpPublicationLink persistedC = MpPublicationLink.getDao().getById(newLink.getId());
		assertNotNull(persistedC);
		assertNotNull(persistedC.getId());
		assertDaoTestResults(MpPublicationLink.class, persistedA, persistedC, IGNORE_PROPERTIES, true, true);

		MpPublicationLink.getDao().delete(persistedC);
		assertNull(MpPublicationLink.getDao().getById(newLink.getId()));

		MpPublicationLink.getDao().deleteById(2);
		assertNull(MpPublicationLink.getDao().getById(2));

		MpPublicationLink.getDao().deleteByParent(1);
		Map<String, Object> filters = new HashMap<>();
		filters.put("publicationId", 1);
		List<MpPublicationLink> mpLinks = MpPublicationLink.getDao().getByMap(filters);
		assertTrue(mpLinks.isEmpty());
	}

	@Test
	public void copyFromPwTest() {
		MpPublication.getDao().copyFromPw(4);
		MpPublicationLink.getDao().copyFromPw(4);
		MpPublicationLink link = MpPublicationLink.getDao().getById(10);
		assertMpLink10(link);
	}

	public static void assertMpLink1(MpPublicationLink link) {
		assertNotNull(link);
		assertEquals(1, link.getId().intValue());
		assertEquals(1, link.getPublicationId().intValue());
		assertEquals(1, link.getRank().intValue());
		assertEquals(1, link.getLinkType().getId().intValue());
		assertEquals("www.wow.org", link.getUrl());
		assertEquals("amazing link", link.getText());
		assertEquals("12 GB", link.getSize());
		assertEquals(2, link.getLinkFileType().getId().intValue());
		assertEquals("This description is wow!", link.getDescription());
	}

	public static void assertMpLink2(PublicationLink<?> link) {
		assertNotNull(link);
		assertEquals(2, link.getId().intValue());
		assertEquals(1, link.getPublicationId().intValue());
		assertEquals(2, link.getRank().intValue());
		assertEquals(2, link.getLinkType().getId().intValue());
		assertEquals("www.xyz.org", link.getUrl());
		assertEquals("end of the line", link.getText());
		assertEquals("1 TB", link.getSize());
		assertEquals(3, link.getLinkFileType().getId().intValue());
		assertEquals("I'm at the end", link.getDescription());
	}

	public static void assertMpLink10(PublicationLink<?> link) {
		assertNotNull(link);
		assertEquals(10, link.getId().intValue());
		assertEquals(4, link.getPublicationId().intValue());
		assertEquals(1, link.getRank().intValue());
		assertEquals("url", link.getUrl());
		assertEquals("text", link.getText());
		assertEquals("12 GB", link.getSize());
		assertEquals(1, link.getLinkFileType().getId().intValue());
		assertEquals("just a normal description", link.getDescription());
	}
}
