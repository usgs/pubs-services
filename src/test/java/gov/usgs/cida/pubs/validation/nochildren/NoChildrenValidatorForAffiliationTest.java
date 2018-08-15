package gov.usgs.cida.pubs.validation.nochildren;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class NoChildrenValidatorForAffiliationTest extends BaseValidatorTest {

	protected NoChildrenValidatorForAffiliation validator;
	protected CostCenter affiliation;
	protected PersonContributor<?> personContributor;

	@MockBean
	protected PersonContributorDao personContributorDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new NoChildrenValidatorForAffiliation();
		personContributor = new PersonContributor<>();
		personContributor.setPersonContributorDao(personContributorDao);
		affiliation = new CostCenter();
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(affiliation, null));
	}

	@Test
	public void isValidTrueTest() {
		when(personContributorDao.getObjectCount(anyMap())).thenReturn(0);
		assertTrue(validator.isValid(affiliation, context));
	}

	@Test
	public void isValidFalseTest() {
		when(personContributorDao.getObjectCount(anyMap())).thenReturn(10);
		assertFalse(validator.isValid(affiliation, context));
	}
}