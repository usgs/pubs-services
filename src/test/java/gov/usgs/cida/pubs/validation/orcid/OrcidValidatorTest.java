package gov.usgs.cida.pubs.validation.orcid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gov.usgs.cida.pubs.validation.BaseValidatorTest;

public class OrcidValidatorTest extends BaseValidatorTest {

	protected OrcidValidator validator;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new OrcidValidator();
	}

	@Test
	public void isValidNPE() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid("", null));
	}

	@Test
	public void isValidAllDigits() {
		assertTrue(validator.isValid("0000-0003-1415-9269", context));
	}

	@Test
	public void isValidWithX() {
		assertTrue(validator.isValid("0000-0002-1694-233X", context));
	}

	@Test
	public void isInvalidAllDigits() {
		assertFalse(validator.isValid("1000-0002-1825-0097", context));
	}

	@Test
	public void isInvalidWithX() {
		assertFalse(validator.isValid("1000-0002-1694-233X", context));
	}

	@Test
	public void isInvalidBadFormat() {
		assertFalse(validator.isValid("123", context));
		assertFalse(validator.isValid("abc", context));
	}
}
