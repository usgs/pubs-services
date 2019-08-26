package gov.usgs.cida.pubs.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

public final class OutsideContributorHelper {
	private OutsideContributorHelper() {
	}

	public static OutsideContributor JANE_M_DOE = new OutsideContributor();
	static {
		JANE_M_DOE.setFamily("Doe");
		JANE_M_DOE.setGiven("Jane M.");
		JANE_M_DOE.setAffiliations(Set.of(OutsideAffiliationHelper.OUTER_AFFILIATION_1));
	}

	public static OutsideContributor JANE_Q_ODOUL = new OutsideContributor();
	static {
		JANE_Q_ODOUL.setFamily("ODoul");
		JANE_Q_ODOUL.setGiven("Jane q");
		JANE_Q_ODOUL.setAffiliations(Set.of(OutsideAffiliationHelper.OUTER_AFFILIATION_1));
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
