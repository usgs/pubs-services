package gov.usgs.cida.pubs.validation.nochildren;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.when;
import gov.usgs.cida.pubs.TestAffiliation;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class NoChildrenValidatorForAffiliationTest extends BaseValidatorTest {

	protected NoChildrenValidatorForAffiliation validator;
	protected TestAffiliation affiliation;
	protected PersonContributor<?> personContributor;

	@Mock
	protected PersonContributorDao personContributorDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new NoChildrenValidatorForAffiliation();
		personContributor = new PersonContributor<>();
		personContributor.setPersonContributorDao(personContributorDao);
		affiliation = new TestAffiliation();
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(affiliation, null));
	}

	@Test
	public void isValidTrueTest() {
		when(personContributorDao.getObjectCount(anyMapOf(String.class, Object.class))).thenReturn(0);
		assertTrue(validator.isValid(affiliation, context));
	}

	@Test
	public void isValidFalseTest() {
		when(personContributorDao.getObjectCount(anyMapOf(String.class, Object.class))).thenReturn(10);
		assertFalse(validator.isValid(affiliation, context));
	}
}