package gov.usgs.cida.pubs.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.mp.MpPublication;
import gov.usgs.cida.pubs.domain.mp.MpPublicationContributor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class PublicationTest extends BaseSpringTest {

	@Test
	public void testMappingContributors() {
		MpPublication pub = new MpPublication();
		assertNull(pub.getContributorsToMap());
		
		Collection<PublicationContributor<?>> cl = new ArrayList<PublicationContributor<?>>();
		pub.setContributors(cl);
		assertNull(pub.getContributorsToMap());

		PublicationContributor<?> author = new MpPublicationContributor();
		cl.add(author);
		//Not really an author yet...
		Map<String, Collection<PublicationContributor<?>>> hm = pub.getContributorsToMap();
		assertTrue(hm.containsKey("unknown"));
		author.setContributorType(new ContributorType());
		hm = pub.getContributorsToMap();
		assertTrue(hm.containsKey("unknown"));
		
		ContributorType act = new ContributorType();
		act.setId(ContributorType.AUTHORS);
		author.setContributorType(act);
		//Now I am by ID
		hm = pub.getContributorsToMap();
		assertTrue(hm.containsKey("authors"));
		act.setText("Authors");
		//Now I am by name
		hm = pub.getContributorsToMap();
		assertTrue(hm.containsKey("authors"));

		//make sure we add more keys to the hm
		cl.add(new MpPublicationContributor());
		ContributorType bad = new ContributorType();
		bad.setId(-1);
		PublicationContributor<?> badc = new MpPublicationContributor();
		badc.setContributorType(bad);
		cl.add(badc);
		hm = pub.getContributorsToMap();
		assertTrue(hm.containsKey("authors"));
		assertEquals(1, hm.get("authors").size());
		assertTrue(hm.containsKey("unknown"));
		assertEquals(2, hm.get("unknown").size());
	}
	
	@Test
	public void testSetMappingContributors() {
		MpPublication pub = new MpPublication();
		pub.setContributorsFromMap(null);
		assertTrue(pub.getContributors().isEmpty());
		
		Map<String, Collection<PublicationContributor<?>>> cm = new HashMap<String, Collection<PublicationContributor<?>>>();
		pub.setContributorsFromMap(cm);
		assertTrue(pub.getContributors().isEmpty());
		
		cm.put("unknown", null);
		pub.setContributorsFromMap(cm);
		assertTrue(pub.getContributors().isEmpty());

		Collection<PublicationContributor<?>> cl = new ArrayList<PublicationContributor<?>>();
		cm.put("unknown", cl);
		pub.setContributorsFromMap(cm);
		assertTrue(pub.getContributors().isEmpty());

		cl.add(new MpPublicationContributor());
		cl.add(new MpPublicationContributor());
		cl.add(new MpPublicationContributor());
		cm.put("authors", cl);
		pub.setContributorsFromMap(cm);
		//3 from unknown & 3 from authors...
		assertEquals(6, pub.getContributors().size());
	}

}
