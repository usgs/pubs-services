package gov.usgs.cida.pubs.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import gov.usgs.cida.pubs.SeverityLevel;
import gov.usgs.cida.pubs.dao.intfc.IDao;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CostCenter;
import gov.usgs.cida.pubs.domain.OutsideContributor;

//The Dao mocking works because the getDao() methods are all static and JAVA/Spring don't redo them 
//for each reference. This does mean that we need to let Spring know that the context is now dirty...
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class PersonContributorValidationTest extends BaseValidatorTest {
	private static final Logger LOG = LoggerFactory.getLogger(PersonContributorValidationTest.class);

	public static final String INVALID_AFFILIATION = new ValidatorResult("affiliation", NO_PARENT_MSG, SeverityLevel.FATAL, null).toString();
	public static final String NOT_NULL_FAMILY = new ValidatorResult("family", NOT_NULL_MSG, SeverityLevel.FATAL, null).toString();

	public static final String INVALID_FAMILY_LENGTH = new ValidatorResult("family", LENGTH_1_TO_XXX_MSG + "40", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_GIVEN_LENGTH = new ValidatorResult("given", LENGTH_0_TO_XXX_MSG + "40", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_SUFFIX_LENGTH = new ValidatorResult("suffix", LENGTH_0_TO_XXX_MSG + "40", SeverityLevel.FATAL, null).toString();
	public static final String INVALID_EMAIL_LENGTH = new ValidatorResult("email", LENGTH_0_TO_XXX_MSG + "400", SeverityLevel.FATAL, null).toString();

	public static final String INVALID_EMAIL_FOMAT = new ValidatorResult("email", EMAIL_FORMAT_MSG, SeverityLevel.FATAL, null).toString();

	@Autowired
	public Validator validator;

	@Mock
	protected IDao<Contributor<?>> contributorDao;
	@Mock
	protected IDao<CostCenter> affiliationDao;

	//Using OutsideContributor  because it works easier (all validations are the same via PersonContributor...)
	private OutsideContributor contributor;
	//Using CostCenter because it works easier (all validations are the same via Affiliation...)
	private CostCenter affiliation;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		contributor = new OutsideContributor();
		contributor.setContributorDao(contributorDao);
		affiliation = new CostCenter();
		affiliation.setAffiliationDao(affiliationDao);
		contributor.setAffiliation(affiliation);
		contributor.setFamily("a");
	}

	@Test
	public void wiringTest() {
		when(affiliationDao.getById(any(Integer.class))).thenReturn(null);
		affiliation.setId(1);
		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.getValidationErrors().isEmpty());
		assertEquals(1, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From ParentExistsValidatorForPersonContributor
				INVALID_AFFILIATION
				);
	}

	@Test
	public void notNullTest() {
		contributor.setFamily(null);
		//voodoo follows
		try {
			Class<?> c = contributor.getClass();
			Field corporation = c.getSuperclass().getSuperclass().getDeclaredField("corporation");
			corporation.setAccessible(true);
			corporation.set(contributor, null);
			Field usgs = c.getSuperclass().getSuperclass().getDeclaredField("usgs");
			usgs.setAccessible(true);
			usgs.set(contributor, null);
		} catch (Exception e) {
			LOG.info(e.getMessage());
			fail(e.getMessage());
		}
		//end voodoo

		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.getValidationErrors().isEmpty());
		assertEquals(3, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From PersonContributor
				NOT_NULL_FAMILY,
				//From Contributor
				CorporateContributorValidationTest.NOT_NULL_USGS,
				CorporateContributorValidationTest.NOT_NULL_CORPORATION
				);
	}

	@Test
	public void minLengthTest() {
		contributor.setFamily("");

		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.getValidationErrors().isEmpty());
		assertEquals(1, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From PersonContributor
				INVALID_FAMILY_LENGTH
				);

		contributor.setFamily("a");
		contributor.setValidationErrors(validator.validate(contributor));
		assertTrue(contributor.getValidationErrors().isEmpty());
	}

	@Test
	public void maxLengthTest() {
		contributor.setFamily(StringUtils.repeat('X', 41));
		contributor.setGiven(StringUtils.repeat('X', 41));
		contributor.setSuffix(StringUtils.repeat('X', 41));
		contributor.setEmail(StringUtils.repeat('X', 401));
		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.getValidationErrors().isEmpty());
		assertEquals(5, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From PersonContributor
				INVALID_FAMILY_LENGTH,
				INVALID_GIVEN_LENGTH,
				INVALID_SUFFIX_LENGTH,
				INVALID_EMAIL_LENGTH,
				INVALID_EMAIL_FOMAT
				);

		contributor.setFamily(StringUtils.repeat('X', 40));
		contributor.setGiven(StringUtils.repeat('X', 40));
		contributor.setSuffix(StringUtils.repeat('X', 40));
		contributor.setEmail(StringUtils.repeat('X', 400));
		contributor.setValidationErrors(validator.validate(contributor));
		assertFalse(contributor.getValidationErrors().isEmpty());
		assertEquals(1, contributor.getValidationErrors().getValidationErrors().size());
		assertValidationResults(contributor.getValidationErrors().getValidationErrors(),
				//From PersonContributor
				INVALID_EMAIL_FOMAT
				);
	}

}
