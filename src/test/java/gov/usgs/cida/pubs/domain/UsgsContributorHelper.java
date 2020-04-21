package gov.usgs.cida.pubs.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Set;

public final class UsgsContributorHelper {
	private UsgsContributorHelper() {
	}

	public static UsgsContributor JANE_DOE = new  UsgsContributor();
	static {
		JANE_DOE.setFamily("Doe");
		JANE_DOE.setGiven("Jane");
		JANE_DOE.setAffiliations(Set.of(CostCenterHelper.AFFILIATION_COST_CENTER_1));
		JANE_DOE.setOrcid("0000-0000-0000-0000");
	}

	public static UsgsContributor JANE_N_DOE = new  UsgsContributor();
	static {
		JANE_N_DOE.setFamily("ODoul");
		JANE_N_DOE.setGiven("Jane q");
		JANE_N_DOE.setOrcid("0000-0000-0080-0000");
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
