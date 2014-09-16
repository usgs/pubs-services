package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

import org.junit.Test;

public class ParentExistsValidatorForPersonContributorTest extends BaseValidatorTest {

	ParentExistsValidatorForPersonContributor validator;

	@Test
	public void isValidTest() {
		validator = new ParentExistsValidatorForPersonContributor();
		PersonContributor<?> personContributor = new UsgsContributor();
		Affiliation<?> affiliation = new Affiliation<CostCenter>();

		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(personContributor, null));
		
		assertTrue(validator.isValid(personContributor, context));
		
		personContributor.setAffiliation(affiliation);
		assertTrue(validator.isValid(personContributor, context));
		
		affiliation.setId(1);
		assertTrue(validator.isValid(personContributor, context));
		
		affiliation.setId(-1);
		assertFalse(validator.isValid(personContributor, context));
	}

}
