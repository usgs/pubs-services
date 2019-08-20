package gov.usgs.cida.pubs.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

public final class OutsideContributorHelper {
	private OutsideContributorHelper() {
	}

	public static void assertJaneMDoe(OutsideContributor contributor) {
		assertFalse(contributor.isCorporation());
		assertFalse(contributor.isUsgs());
		assertEquals("Doe", contributor.getFamily());
		assertEquals("Jane M.", contributor.getGiven());
		assertNull(contributor.getSuffix());
		assertNull(contributor.getEmail());
		assertEquals("0000-0000-0000-0001", contributor.getOrcid());
		Collection<Affiliation<? extends Affiliation<?>>> affiliations = contributor.getAffiliations();
		assertEquals(1, affiliations.size());
		assertEquals("Outer Affiliation 1", affiliations.toArray(new Affiliation[1])[0].getText());
		assertFalse(contributor.isPreferred());
	}

	public static void assertJaneQODoul(OutsideContributor contributor) {
		assertFalse(contributor.isCorporation());
		assertFalse(contributor.isUsgs());
		assertEquals("ODoul", contributor.getFamily());
		assertEquals("Jane Q", contributor.getGiven());
		assertNull(contributor.getSuffix());
		assertNull(contributor.getEmail());
		assertNull(contributor.getOrcid());
		assertTrue(contributor.getAffiliations().isEmpty());
		assertFalse(contributor.isPreferred());
	}
}
