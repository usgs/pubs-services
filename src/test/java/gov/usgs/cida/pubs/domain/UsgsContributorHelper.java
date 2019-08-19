package gov.usgs.cida.pubs.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

public final class UsgsContributorHelper {
	private UsgsContributorHelper() {
	}

	public static void assertJaneDoe(UsgsContributor contributor) {
		assertFalse(contributor.isCorporation());
		assertTrue(contributor.isUsgs());
		assertEquals("Doe", contributor.getFamily());
		assertEquals("Jane", contributor.getGiven());
		assertNull(contributor.getSuffix());
		assertNull(contributor.getEmail());
		assertEquals("0000-0000-0000-0000", contributor.getOrcid());
		Collection<Affiliation<? extends Affiliation<?>>> affiliations = contributor.getAffiliations();
		assertEquals(1, affiliations.size());
		assertEquals("Affiliation Cost Center 1", affiliations.toArray(new CostCenter[1])[0].getText());
		assertTrue(contributor.isPreferred());
	}

	public static void assertJaneNDoe(UsgsContributor contributor) {
		assertFalse(contributor.isCorporation());
		assertTrue(contributor.isUsgs());
		assertEquals("Doe", contributor.getFamily());
		assertEquals("Jane N.", contributor.getGiven());
		assertNull(contributor.getSuffix());
		assertNull(contributor.getEmail());
		assertEquals("0000-0000-0080-0000", contributor.getOrcid());
		assertTrue(contributor.getAffiliations().isEmpty());
		assertTrue(contributor.isPreferred());
	}
}
