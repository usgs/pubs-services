package gov.usgs.cida.pubs.validation.mp.parent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.dao.AffiliationDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;
import gov.usgs.cida.pubs.validation.BaseValidatorTest;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we nned to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class ParentExistsValidatorForPersonContributorTest extends BaseValidatorTest {

	protected ParentExistsValidatorForPersonContributor validator;
	protected PersonContributor<?> personContributor;
	protected Affiliation<?> affiliation;

	@Mock
	protected AffiliationDao affiliationDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		validator = new ParentExistsValidatorForPersonContributor();
		personContributor = new UsgsContributor();
		affiliation = new Affiliation<>();
		affiliation.setAffiliationDao(affiliationDao);
	}

	@Test
	public void isValidNPETest() {
		assertTrue(validator.isValid(null, null));
		assertTrue(validator.isValid(null, context));
		assertTrue(validator.isValid(personContributor, null));

		assertTrue(validator.isValid(personContributor, context));

		personContributor.setAffiliation(affiliation);
		assertTrue(validator.isValid(personContributor, context));
	}

	@Test
	public void isValidTrueTest() {
		when(affiliationDao.getById(any(Integer.class))).thenReturn(new Affiliation<>());
		personContributor.setAffiliation(affiliation);

		affiliation.setId(1);
		assertTrue(validator.isValid(personContributor, context));
		verify(affiliationDao).getById(any(Integer.class));
	}

	@Test
	public void isValidFalseTest() {
		when(affiliationDao.getById(any(Integer.class))).thenReturn(null);
		personContributor.setAffiliation(affiliation);

		affiliation.setId(-1);
		assertFalse(validator.isValid(personContributor, context));
		verify(affiliationDao).getById(any(Integer.class));
	}

}
